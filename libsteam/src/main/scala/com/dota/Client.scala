package com.dota

import java.lang.Thread
import org.slf4j.LoggerFactory
import com.google.common.eventbus.Subscribe

import com.dota.event._
import com.dota.const.Game
import com.dota.proto.Gcsystemmsgs.EGCBaseClientMsg
import com.dota.proto.GcsdkGcmessages.{CMsgClientHello, CMsgClientWelcome}

import com.steam._
import com.steam.event._
import com.steam.net.{ClientMsg => GeneralClientMsg}
import com.steam.net.msg.ClientMsgProtoBuf
import com.steam.net.gc.{PacketGCMsg => GeneralPacketGCMsg}
import com.steam.net.gc.msg.ClientGCMsgProtoBuf
import com.steam.const.{EMsg, EPersonaState}
import com.steam.proto.SteammessagesClientserver.CMsgClientGamesPlayed

/** Dota 2 Client */
class Dota2Client(username: String, password: String)
{
  private val logger = LoggerFactory.getLogger("com.dota")

  val steamClient                 = new SteamClient

  def steamGC                     = this.steamClient.steamGC
  def send(msg: GeneralClientMsg) = this.steamClient.send(msg)
  def isConnected                 = this.steamClient.isConnected
  def addHandler(handler: Any)    = this.steamClient.eventbus.register(handler)
  def disconnect                  = this.steamClient.disconnect
  
  /** Connect to the Steam Network */
  def connect(timeout: Int = 5) = this.steamClient.connect(timeout)

  /** Register this as a handler */
  this.steamClient.eventbus.register(this)

  /** Event handler for steam connect event.
    *
    * On connect, try to login with the user's 
    * provided credentials. SteamGuard is not handled
    * by the underlying steam client, so disable before
    * using!
    */
  @Subscribe
  def onSteamConnect(evt: SteamClientConnected) = 
    this.steamClient.steamUser.logOn(username, password)

  @Subscribe
  def onSteamFriendsEvent(evt: SteamFriendsEvent) = {
    evt match {
      case LoginSuccess() => this.onLoginSuccess
      case _ =>
    }
  }

  /** Event handler for a GC event
    *
    * Delegates event handling to other methods depending on 
    * which message is received.
    */
  @Subscribe
  def onSteamGCEvent(evt: SteamGCEvent) = {
    evt match {
      case GCMsgReceived(msg, appID, pkt) if appID == Game.steamID => {
        msg match {
          case EGCBaseClientMsg.k_EMsgGCClientWelcome_VALUE => {
            this.onGCClientWelcome(pkt)
          }
          case _ =>
        }
      }
      case _ =>
    }
  }

  /** Handle the event when login is a success
    *
    * Tell the client that you are Online, playing Dota 2,
    * and request a connection with the GC
    */
  private def onLoginSuccess = {
    val msg = ClientMsgProtoBuf.createSend[
      CMsgClientGamesPlayed.Builder,
      CMsgClientGamesPlayed](EMsg.ClientGamesPlayed)
    val builder = CMsgClientGamesPlayed.GamePlayed.newBuilder

    builder.setGameId(Game.steamID)

    msg.body.addGamesPlayed(builder.build)

    this.steamClient.send(msg)

    Thread.sleep(5000)

    val gcHelloMsg = ClientGCMsgProtoBuf.createSend[
      CMsgClientHello.Builder,
      CMsgClientHello](EGCBaseClientMsg.k_EMsgGCClientHello_VALUE)

    this.steamClient.steamGC.send(gcHelloMsg, Game.steamID)
  }
  
  /** GC Client welcome event handler
    * 
    * Send an event that says we're connected to the GC now.
    */
  private def onGCClientWelcome(pkt: GeneralPacketGCMsg) = {
    val msg = ClientGCMsgProtoBuf.createReceived[
      CMsgClientWelcome.Builder,
      CMsgClientWelcome](pkt)

    logger.debug(s"Received GC Welcome! Version: ${msg.body.getVersion}")

    this.steamClient.eventbus.post(new GCWelcomeReceived(msg))
  }
}
