package com.steam.net.service

import org.slf4j.LoggerFactory
import com.google.common.eventbus.Subscribe

import com.steam.event._
import com.steam.net.msg.ClientMsgProtoBuf
import com.steam.{ClientPacketReceived, CMClient}
import com.steam.net.{PacketMsg => GeneralPacketMsg}
import com.steam.const.{EMsg, EPersonaState, EResult}
import com.steam.proto.SteammessagesClientserver.{CMsgClientChangeStatus,
  CMsgClientAccountInfo}

class SteamFriends(client: CMClient) extends SteamService(client) {
  private var logger = LoggerFactory.getLogger("com.steam")
  private var localName: Option[String] = None
  private var state = EPersonaState.Offline

  /** Getter / Setter for the local user's EPersonState
    * 
    * Sends a message to the server to update the 
    * client's status upon value reassignment.
    */
  def epersona = this.state
  def epersona_=(state: EPersonaState.EPersonaState) = this.localName match {
    case Some(name) => {
      var msg = ClientMsgProtoBuf.createSend[
        CMsgClientChangeStatus.Builder,
        CMsgClientChangeStatus](EMsg.ClientChangeStatus)

      msg.body.setPersonaState(state.id)
      msg.body.setPlayerName(name)

      this.state = state
      this.client.send(msg)
    }
    case None => this.logger.error("not logged in! can't change persona state!")
  }

  /** Getter / Setter for the local user's name
    *
    * Sends a message to the server to update the client's name
    */
  def name = this.localName
  def name_=(name: String) = this.localName match {
    case Some(_) => {
      var msg = ClientMsgProtoBuf.createSend[
        CMsgClientChangeStatus.Builder,
        CMsgClientChangeStatus](EMsg.ClientChangeStatus)

      msg.body.setPersonaState(this.epersona.id)
      msg.body.setPlayerName(name)

      this.localName = Some(name)
      this.client.send(msg)
    }
    case None => this.logger.error("not logged in! can't change name!")
  }

  @Subscribe
  def onMsgReceived(evt: ClientPacketReceived) = {
    evt.pkt.msg match {
      case EMsg.ClientAccountInfo => this.handleAccountInfo(evt.pkt)
      case EMsg.ClientLoggedOff => this.handleLoggedOff
      case EMsg.ClientNewLoginKey => this.handleLoginSuccess
      case _ =>
    }
  }

  /** Sets the local username */
  private def handleAccountInfo(pkt: GeneralPacketMsg) = {
    val inf = ClientMsgProtoBuf.createReceived[
      CMsgClientAccountInfo.Builder,
      CMsgClientAccountInfo](pkt)

    this.localName = Some(inf.body.getPersonaName)
  }

  /** Resets the local user information */
  private def handleLoggedOff = {
    this.localName = None
    this.state = EPersonaState.Offline
  }

  /** Emit a login success */
  private def handleLoginSuccess = this.client.eventbus.post(LoginSuccess())
}
