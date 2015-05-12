package com.steam.net

import org.slf4j.LoggerFactory

import com.google.common.net.HostAndPort
import com.google.common.eventbus.{EventBus, Subscribe, AllowConcurrentEvents}
import com.google.protobuf.{CodedInputStream, CodedOutputStream}

import java.io.{IOException, InputStream, OutputStream}
import java.net.{Socket, InetSocketAddress, InetAddress, SocketException}
import java.lang.Thread

trait Connection {
  var eventBus = new EventBus(this.getClass.getName)
  def connect(ipAddr: HostAndPort, timeout: Integer)
  def disconnect
  def send(clientMsg: ClientMsg)
  def addEventHandler(obj: Any) = eventBus.register(obj)
}

package object connection {
  /** Events that can be triggered by a Connection class */
  class NetMsgEvent(val data: Array[Byte]) 
  class DisconnectEvent
  class ConnectEvent

  /** Trait for classes that will handle events triggered by a Connection */
  trait ConnectionSubscriber {
    @Subscribe def onDisconnectEvent(e: DisconnectEvent)
    @Subscribe def onConnectEvent(e: ConnectEvent)
    @Subscribe def onNetMsgEvent(e: NetMsgEvent)
  }

  object TcpConnection {
    protected val magic = 0x31305456
  }

  /** Wrapper for a TcpConnection
    *
    * Create a class implementing  ConnectionSubscriber to handle events 
    * triggered by the Connection
    *
    * {{{
    * var conn = new TcpConnection
    * conn.addEventHandler(new ConnectionSubscriberImpl)
    * conn.connect(ipAddr)
    * }}}
    */
  class TcpConnection extends Connection {
    private val logger = LoggerFactory.getLogger("com.steam")
    private var socket: Option[Socket] = None
    private var instream: Option[InputStream] = None
    private var outstream: Option[OutputStream] = None
    private var reader: Option[CodedInputStream] = None
    private var writer: Option[CodedOutputStream] = None
    private var _netThread = new Thread(NetLoop, "TcpConnection Thread")
    var netFilter: Option[NetFilter] = None

    /** Readonly NetThread */
    private def netThead_=(t: Thread) = this._netThread = t
    def netThread = this._netThread

    def localIP: InetAddress = this.socket match {
      case Some(socket) => socket.getLocalAddress
      case _ => InetAddress.getLoopbackAddress 
    }
    
    private object NetLoop extends Runnable {
      def run: Unit = while (true) {
        socket match {
          case Some(sock) if !reader.get.isAtEnd => { 
            readPacket
          }
          case None => return 
          case _ =>
        }
        
        Thread.sleep(100)
      }
    }

    def isConnected: Boolean = this.socket match {
      case Some(socket) => socket.isConnected
      case None => false
    }

    def remoteAddress: String = this.socket match {
      case Some(socket) => socket.getRemoteSocketAddress.toString
      case None => "0.0.0.0"
    }

    /** Establishes a TcpConnection to the given IP address.
      *
      * Connects to a given IP address, and sets up the input and output 
      * streams, and the reading thread.
      */
    def connect(ipAddr: HostAndPort, timeout: Integer = 5) = {
      this.disconnect
      this.socket = Some(new Socket)

      val addr = new InetSocketAddress(ipAddr.getHostText, ipAddr.getPort)

      try {
        this.socket.get.connect(addr, timeout * 1000)
        this.instream   = Some(this.socket.get.getInputStream)
        this.outstream  = Some(this.socket.get.getOutputStream)
        this.writer     = Some(CodedOutputStream.newInstance(this.outstream.get))
        this.reader     = Some(CodedInputStream.newInstance(this.instream.get))
        this.netThread.start
        this.eventBus.post(new ConnectEvent)
      } catch {
        case ex: IOException => this.disconnect
      }
    }

    /** Disconnects the connection
      *
      * Disconnects the TcpConnection. Closes the socket, closes input and 
      * output the stream, and waits for `netThread` to complete. Finally, 
      * sets all internal streams to `None`, and posts a `DisconnectEvent`
      * to the `eventBus`.
      */ 
    def disconnect = {
      this.socket match {
        case Some(socket) => { 
          if (socket.isConnected) {
            socket.shutdownInput
            socket.shutdownOutput
          }

          this.socket = None
          this.netThread.join(1000)
          this.instream match { case Some(stream) => stream.close; case None => }
          this.outstream match { case Some(stream) => stream.close case None => }

          socket.close

          this.eventBus.post(new DisconnectEvent)
        }
        case _ => 
      }
      this.writer     = None
      this.reader     = None
      this.instream   = None
      this.outstream  = None
      this.netFilter  = None
    }

    /** Internal method to read a packet from the stream
      * 
      * Reads a packet (len -> magic -> data), then 
      * posts a new NetMsgEvent containing the read `data`
      */
    private def readPacket = this.reader match {
      case Some(reader) => {
        val packetlen = reader.readRawLittleEndian32
        val packetmagic = reader.readRawLittleEndian32
        var data = new Array[Byte](packetlen)

        if (packetmagic != TcpConnection.magic) {
          throw new IOException("invalid magic")
        }

        reader.resetSizeCounter 

        data = reader.readRawBytes(packetlen)

        logger.debug("---NEW PACKET---")
        logger.debug(s"Expected Packet Len: ${packetlen}")
        logger.debug(s"Bytes Read:          ${reader.getTotalBytesRead}")
        logger.debug("---END PACKET---")

        if (packetlen != reader.getTotalBytesRead) {
          throw new IOException("len of packet doesnt match")
        }

        netFilter match {
          case Some(filter) => data = filter.decrypt(data)
          case None =>
        }

        this.eventBus.post(new NetMsgEvent(data)) 
      }
      case None => throw new IOException("reader is not set")
    }

    /** Sends a ClientMsg across the connection.
      * 
      * {{{
      * var conn = new TcpConnection
      * conn.connect
      * conn.send(clientMsg)
      * }}}
      */
    def send(clientMsg: ClientMsg) = this.writer match {
      case Some(out) if this.isConnected && !this.outstream.isEmpty => {
        var data = clientMsg.serialize

        netFilter match {
          case Some(filter) => data = filter.encrypt(data)
          case None =>
        }

        out.synchronized {
          out.writeRawLittleEndian32(data.length)
          out.writeRawLittleEndian32(TcpConnection.magic)
          out.writeRawBytes(data)
          out.flush
          this.outstream.get.flush
        }
      }
      case None => throw new IOException("no writer set?")
      case _ => throw new SocketException("couldn't connect to the socket!")
    }

    override def toString = s"<TcpConnection addr='${this.remoteAddress}' conn='${this.isConnected}'>"
  }
}
