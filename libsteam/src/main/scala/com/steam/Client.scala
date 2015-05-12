package com.steam

import com.steam.net.msg.Msg
import com.steam.net.service._
import com.steam.const.{EMsg, EResult}
import com.steam.net.msg.protocol.MsgChannelEncryptResult
import com.steam.net.{PacketMsg => GeneralPacketMsg}

import com.google.common.eventbus.Subscribe

/** Events posted by SteamClient */
class SteamClientConnected(val msg: Msg[MsgChannelEncryptResult])
class SteamClientDisconnected

/** Steam Client */
class SteamClient extends CMClient {
  val steamUser     = new SteamUser(this)
  var steamFriends  = new SteamFriends(this)
  var steamGC       = new SteamGameCoordinator(this)

  this.eventbus.register(this)
  this.eventbus.register(this.steamFriends)
  this.eventbus.register(this.steamUser)
  this.eventbus.register(this.steamGC)

  @Subscribe
  def onPacketReceived(msg: ClientPacketReceived) = {
    msg.pkt.msg match {
      case EMsg.ChannelEncryptResult => {
        this.onChannelEncryptResult(msg.pkt) 
      }
      case _ =>
    }
  }

  @Subscribe
  def onDisconnected(msg: ClientDisconnected) = {
    this.eventbus.post(new SteamClientDisconnected)
  }

  private def onChannelEncryptResult(pkt: GeneralPacketMsg) = {
    this.eventbus.post(
      new SteamClientConnected(
        Msg.createReceived[MsgChannelEncryptResult](pkt)))
  }
}
