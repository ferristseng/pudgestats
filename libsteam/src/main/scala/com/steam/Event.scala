package com.steam.event

import com.steam.net.gc.PacketGCMsg

sealed abstract class SteamUserEvent
case class LogOnEvent() extends SteamUserEvent
case class LoginKeyReceived() extends SteamUserEvent

sealed abstract class SteamFriendsEvent
case class LoginSuccess() extends SteamFriendsEvent

sealed abstract class SteamGCEvent
case class GCMsgReceived(
    val msg: Int, 
    val appID: Int, 
    val pkt: PacketGCMsg) 
  extends SteamGCEvent
