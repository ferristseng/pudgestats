package com.steam.net.service

import org.slf4j.LoggerFactory
import com.google.protobuf.ByteString
import com.google.common.eventbus.Subscribe

import com.steam.util.MsgUtil
import com.steam.{CMClient, ClientPacketReceived}
import com.steam.event.GCMsgReceived
import com.steam.const.EMsg
import com.steam.net.{PacketMsg => GeneralPacketMsg}
import com.steam.net.msg.ClientMsgProtoBuf
import com.steam.net.gc.{ClientGCMsg, PacketGCMsg => GeneralPacketGCMsg}
import com.steam.net.gc.msg.{PacketClientGCMsgProtoBuf, PacketClientGCMsg}
import com.steam.proto.SteammessagesClientserver2.CMsgGCClient

class SteamGameCoordinator(client: CMClient) {
  private val logger = LoggerFactory.getLogger("com.steam")

  /** Sends a GC message with a specific app ID */
  def send(req: ClientGCMsg, appID: Integer) = {
    var msg = ClientMsgProtoBuf.createSend[
      CMsgGCClient.Builder,
      CMsgGCClient](EMsg.ClientToGC)

    msg.body.setMsgtype(MsgUtil.makeGCMsg(req.msg, req.isProto))
    msg.body.setAppid(appID)
    msg.body.setPayload(ByteString.copyFrom(req.serialize))

    this.client.send(msg)
  }

  @Subscribe
  def onMsgReceived(evt: ClientPacketReceived) = {
    evt.pkt.msg match {
      case EMsg.ClientFromGC => {
        val msg = ClientMsgProtoBuf.createReceived[
          CMsgGCClient.Builder,
          CMsgGCClient](evt.pkt)
        val data: CMsgGCClient = msg.body.build
        val pkt = this.getPacketGCMsg(data)
        val rec = new GCMsgReceived(
          MsgUtil.getGCMsg(data.getMsgtype),
          data.getAppid,
          pkt)

        logger.debug(s"(PKT) [GC] Received: ${pkt}")

        this.client.eventbus.post(rec)
      }
      case _ =>
    }
  }

  private def getPacketGCMsg(data: CMsgGCClient): GeneralPacketGCMsg = {
    val msg = MsgUtil.getGCMsg(data.getMsgtype)

    if (MsgUtil.isProtoBuf(data.getMsgtype)) {
      new PacketClientGCMsgProtoBuf(msg, data.getPayload.toByteArray)
    } else {
      new PacketClientGCMsg(msg, data.getPayload.toByteArray)
    }
  }
}
