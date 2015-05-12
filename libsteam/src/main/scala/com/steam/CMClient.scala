package com.steam

import org.slf4j.LoggerFactory
import com.google.common.eventbus.EventBus
import com.google.protobuf.CodedInputStream

import java.lang.RuntimeException
import java.io.{ByteArrayInputStream}
import java.nio.{ByteBuffer, ByteOrder}
import java.util.concurrent.{Executors, TimeUnit}
import java.util.zip.GZIPInputStream

import com.steam.net.msg._
import com.steam.net.{ClientMsg => GeneralClientMsg, 
  PacketMsg => GeneralPacketMsg, NetFilter}
import com.steam.net.msg.protocol._
import com.steam.util.{MsgUtil, CryptoHelper, RSACrypto}
import com.steam.const.{PublicKey, ServerList, EMsg, EUniverse, EResult}
import com.steam.proto.SteammessagesBase.CMsgMulti
import com.steam.proto.SteammessagesClientserver.{CMsgClientHeartBeat, 
  CMsgClientLogonResponse, CMsgClientLoggedOff}
import com.steam.net.connection.{TcpConnection, ConnectionSubscriber,
  NetMsgEvent, ConnectEvent, DisconnectEvent}

/** Events posted by the CMClient */
class ClientConnected
class ClientDisconnected
class ClientPacketReceived(val pkt: GeneralPacketMsg)

/** Mimics a CM client, using an established protocol
  *
  * Connects to a random CM server. Connection may timeout, and reconnection
  * methods are left up to the user.
  * 
  * {{{
  * var client = new CMClient
  * while (!client.connection.isConnected) {
  *   client.connect
  * }
  * }}}
  */
class CMClient extends ConnectionSubscriber {
  val eventbus = new EventBus 
  val connection = new TcpConnection
  var euniverse = EUniverse.Invalid
  private val logger = LoggerFactory.getLogger("com.steam")
  private var executor = Executors.newSingleThreadScheduledExecutor
  private var tempSessionKey: Option[Array[Byte]] = None
  private var sessionid: Option[Int] = None 
  private var steamid: Option[Long] = None
  private var cellid: Option[Int] = None

  def isConnected = connection.isConnected
  def netThread   = connection.netThread

  /** Use `this` as a EventHandler for the underlying connection */
  this.connection.addEventHandler(this)

  /** Impl for ConnectionSubscriber */
  def onConnectEvent(e: ConnectEvent) = {
    this.logger.debug("connected!")
    this.eventbus.post(new ClientConnected)
  }

  /** Impl for ConnectionSubscriber */
  def onDisconnectEvent(e: DisconnectEvent) = {
    this.logger.debug("disconnected")
    this.disconnect
    this.eventbus.post(new ClientDisconnected)
  }

  /** Impl for ConnectionSubscriber */
  def onNetMsgEvent(e: NetMsgEvent) = {
    try {
      this.onMsgReceived(this.getPacketMsg(e.data))
    } catch {
      case ex: Throwable => this.logger.error(s"error when reading packet: ${ex}") 
    }
  }

  /** Pings the server with a heartbeat...still alive! */
  object HeartBeat extends Runnable {
    def run = { 
      try {
        send(ClientMsgProtoBuf.createSend[
          CMsgClientHeartBeat.Builder, 
          CMsgClientHeartBeat](EMsg.ClientHeartBeat))
      } catch {
        case ex: Throwable => logger.warn(s"failed to send with: ${ex}"); disconnect
      }
    }
  }

  /** Internal method to convert a byte buffer info a usable PacketMsg */
  protected def getPacketMsg(data: Array[Byte]): GeneralPacketMsg = {
    val byteBuf = ByteBuffer.wrap(data)
    val rawEmsg = byteBuf.order(ByteOrder.LITTLE_ENDIAN).getInt
    val emsg = MsgUtil.getMsg(rawEmsg)

    emsg match {
      case EMsg.ChannelEncryptRequest | 
           EMsg.ChannelEncryptResponse | 
           EMsg.ChannelEncryptResult => {
        new PacketMsg(emsg, byteBuf.array)      
      }
      case _ => {
        if (MsgUtil.isProtoBuf(rawEmsg)) {
          new PacketClientMsgProtoBuf(emsg, byteBuf.array)
        } else {
          new PacketClientMsg(emsg, byteBuf.array) 
        }
      }
    }
  }

  /** Sends a message across the underlying connection */
  def send(msg: GeneralClientMsg) = {
    this.sessionid match {
      case Some(id) => msg.sessionID = id
      case None =>
    }

    this.steamid match {
      case Some(id) => msg.steamID = id
      case None =>
    }

    this.logger.debug(s"Sending ${msg.msg}...")
    this.connection.send(msg)
  }

  /** Connect to a random CM server
    * 
    * Uses a list of prepopulated CM servers (see Servers.scala), 
    * attempts to connect to a random one.
    */
  def connect(timeout: Int = 5) = {
    this.disconnect 
    this.connection.connect(ServerList.randomServer, timeout)
  }

  /** Disconnects from the CM server
    * 
    * Shuts down the heartbeat function, and disconnect the 
    * internal connection.
    */
  def disconnect = {
    this.stopHeartBeat
    this.cellid = None
    this.steamid = None
    this.sessionid = None
    this.tempSessionKey = None
    this.connection.disconnect
  }

  /** Start HeartBeat thread */
  private def startHeartBeat(delay: Int) = {
    this.executor = Executors.newSingleThreadScheduledExecutor 
    this.executor.scheduleWithFixedDelay(HeartBeat, 0, delay, TimeUnit.SECONDS)
  }

  /** Stop HeartBeat thread */
  private def stopHeartBeat = {
    this.executor.shutdown
    if (!this.executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
      this.executor.shutdownNow
    }
  }

  /** Delegates to a separate method what to do upon the 
    * receival of particular packets
    */
  private def onMsgReceived(pkt: GeneralPacketMsg): Unit = {
    logger.debug(s"(PKT) Received: ${pkt}")

    pkt.msg match {
      case EMsg.ChannelEncryptRequest => this.onChannelEncryptRequest(pkt)
      case EMsg.ChannelEncryptResult => this.onChannelEncryptResult(pkt)
      case EMsg.Multi => this.onChannelMulti(pkt)
      case EMsg.ClientLogOnResponse => this.onClientLogOnResponse(pkt)
      case EMsg.ClientLoggedOff => this.onClientLoggedOff(pkt) 
      case EMsg.ClientServerList =>
      case EMsg.ClientCMList =>
      case EMsg.ClientSessionToken =>
      case _ =>
    }

    this.eventbus.post(new ClientPacketReceived(pkt))
  }

  /** Handle a MsgChannelEncryptRequest message */
  private def onChannelEncryptRequest(pkt: GeneralPacketMsg) = {
    var req = Msg.createReceived[MsgChannelEncryptRequest](pkt)
    var res = Msg.createSend[MsgChannelEncryptResponse]()
    val euniverse = req.body.euniverse
    val protocolVersion = req.body.protocolVersion

    this.euniverse = euniverse

    tempSessionKey = Some(CryptoHelper.generateRandomBlock(32))
    val cryptSessionKey = new RSACrypto(
      PublicKey.getKey(euniverse)).encrypt(tempSessionKey.get)
    val keyCRC = CryptoHelper.CRCHash(cryptSessionKey)

    res.payload.writeRawBytes(cryptSessionKey, 0, cryptSessionKey.length)
    res.payload.writeRawBytes(keyCRC, 0, keyCRC.length)
    res.payload.writeRawLittleEndian32(0)
    res.payload.flush

    this.send(res)
  }
  
  /** Handle a MsgChannelEncryptResult message */
  private def onChannelEncryptResult(pkt: GeneralPacketMsg) = {
    var req = Msg.createReceived[MsgChannelEncryptResult](pkt)

    if (req.body.result == EResult.OK) {
      tempSessionKey match {
        case Some(key) => this.connection.netFilter = Some(new NetFilter(key))
        case None => new RuntimeException("tempSessionKey wasn't set")
      }
    }
  }

  /** Handle a CMsgMulti packet
    * 
    * Decompress and read the inner body of the received 
    * message as another Packet. Then delegate the next action to 
    * the general message handler.
    */
  private def onChannelMulti(pkt: GeneralPacketMsg) = if (pkt.isProto) {
    val req = ClientMsgProtoBuf.createReceived[CMsgMulti.Builder, CMsgMulti](pkt)
    val payload = if (req.body.getSizeUnzipped > 0) {
      val body = req.body.getMessageBody.toByteArray
      val uncompressed = new Array[Byte](req.body.getSizeUnzipped)
      val instream = new ByteArrayInputStream(body)
      val gzipstream = new GZIPInputStream(instream)
      var bytesRead = 0

      while (bytesRead < body.length) {
        bytesRead += 
          gzipstream.read(uncompressed, bytesRead, 
            req.body.getSizeUnzipped - bytesRead)
      }

      uncompressed
    } else { req.body.getMessageBody.toByteArray }
    val instream = new ByteArrayInputStream(payload)  
    val reader = CodedInputStream.newInstance(instream)

    while (!reader.isAtEnd) {
      val size = reader.readRawLittleEndian32
      val data = reader.readRawBytes(size)

      this.onMsgReceived(this.getPacketMsg(data))
    }
  } else { logger.error(s"received a non-proto pkt: ${pkt}") }

  /** Handle a CMsgLogonResponse */
  private def onClientLogOnResponse(pkt: GeneralPacketMsg) = if (pkt.isProto) {
    val req = ClientMsgProtoBuf.createReceived[
      CMsgClientLogonResponse.Builder, 
      CMsgClientLogonResponse](pkt)

    if (req.body.getEresult == EResult.OK.id) {
      val hbDelay = req.body.getOutOfGameHeartbeatSeconds
      this.steamid = Some(req.header.proto.getSteamid)
      this.sessionid = Some(req.header.proto.getClientSessionid)
      this.cellid = Some(req.body.getCellId)
      this.startHeartBeat(hbDelay)
    } else { 
      logger.error(s"received a non-ok logon response: ${req.body.getEresult}")
    }
  } else { logger.error(s"received a non-proto pkt: ${pkt}"); this.disconnect }

  private def onClientLoggedOff(pkt: GeneralPacketMsg) = {
    if (pkt.isProto) { 
      val req = ClientMsgProtoBuf.createReceived[
        CMsgClientLoggedOff.Builder,
        CMsgClientLoggedOff](pkt)

      logger.debug(s"logged off with result: '${EResult(req.body.getEresult)}'")
    }

    this.connection.disconnect 
  }
}
