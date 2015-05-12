package com.steam.net.service

import com.google.protobuf.ByteString
import com.google.common.eventbus.Subscribe

import com.steam.event._
import com.steam.types.SteamID
import com.steam.ClientPacketReceived
import com.steam.net.msg.ClientMsgProtoBuf
import com.steam.net.{PacketMsg => GeneralPacketMsg}
import com.steam.net.msg.protocol.MsgClientLogon
import com.steam.util.{NetHelper, OSHelper, MachineID}
import com.steam.const.{EMsg, EAccountType, EResult}
import com.steam.{SteamClientDisconnected, SteamClientConnected, CMClient}
import com.steam.proto.SteammessagesClientserver.{CMsgClientLogon,
  CMsgClientNewLoginKey, CMsgClientNewLoginKeyAccepted} 
import com.steam.proto.SteammessagesClientserver2.{
  CMsgClientUpdateMachineAuthResponse, CMsgClientUpdateMachineAuth}

class SteamUser(client: CMClient) extends SteamService(client) {
  /** Log on a user
    * 
    * Does not handle STEAMGUARD enabled users. PLEASE make sure 
    * that STEAMGUARD is disabled
    *
    * TODO - consider adding support for SteamGuard
    */
  def logOn(username: String, password: String) = {
    var id = new SteamID(0, SteamID.desktopInstance, client.euniverse, EAccountType.Individual)
    val localIP = NetHelper.getIPAddress(client.connection.localIP)
    var req = ClientMsgProtoBuf.createSend[CMsgClientLogon.Builder, CMsgClientLogon](
      EMsg.ClientLogon)

    req.header.proto.setClientSessionid(0)
    req.header.proto.setSteamid(id.toLong)

    req.body.setObfustucatedPrivateIp(localIP.toInt ^ MsgClientLogon.obfuscationMask)
    req.body.setAccountName(username)
    req.body.setPassword(password)
    req.body.setProtocolVersion(MsgClientLogon.currentProtocol)
    req.body.setClientOsType(OSHelper.getOSType.id)
    req.body.setClientLanguage("english")
    req.body.setSteam2TicketRequest(false) // Unneeded for my purposes
    req.body.setClientPackageVersion(1771)
    req.body.setMachineId(ByteString.copyFrom(MachineID.generate))
    req.body.clearShaSentryfile
    req.body.setEresultSentryfile(EResult.FileNotFound.id)

    this.client.send(req)
  }

  @Subscribe
  def onPacketReceived(evt: ClientPacketReceived) = {
    evt.pkt.msg match {
      case EMsg.ClientNewLoginKey => this.onNewLoginKey(evt.pkt)
      case _ =>
    }
  }

  private def onNewLoginKey(pkt: GeneralPacketMsg) = {
    val req = ClientMsgProtoBuf.createReceived[CMsgClientNewLoginKey.Builder,
      CMsgClientNewLoginKey](pkt)
    var res = ClientMsgProtoBuf.createSend[CMsgClientNewLoginKeyAccepted.Builder,
      CMsgClientNewLoginKeyAccepted](EMsg.ClientNewLoginKeyAccepted)

    res.body.setUniqueId(req.body.getUniqueId)

    this.client.send(res)
    this.client.eventbus.post(LoginKeyReceived())
  }
}
