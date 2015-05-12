package com.dota.event

import com.dota.proto.GcsdkGcmessages.CMsgClientWelcome

import com.steam.net.gc.msg.ClientGCMsgProtoBuf

sealed abstract class DotaClientEvent
case class GCWelcomeReceived(
    val msg: ClientGCMsgProtoBuf[CMsgClientWelcome.Builder, CMsgClientWelcome]) 
  extends DotaClientEvent
