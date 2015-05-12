package com.steam.util

import com.steam.const.EMsg

object MsgUtil {
  private val ProtoMask = 0x80000000
  private val EMsgMask = ~MsgUtil.ProtoMask

  def getMsg(msg: Integer): EMsg.EMsg     = EMsg(msg & MsgUtil.EMsgMask)
  def getGCMsg(msg: Integer): Integer     = msg & MsgUtil.EMsgMask
  def isProtoBuf(msg: Integer): Boolean   = (msg & 0xffffffffL & MsgUtil.ProtoMask) > 0
  def makeMsg(msg: Integer, 
              proto: Boolean): Integer    = if (proto) { msg | ProtoMask } else { msg }
  def makeMsg(msg: Integer): Integer      = MsgUtil.makeMsg(msg, false)
  def makeGCMsg(msg: Integer,
                proto: Boolean): Integer  = if (proto) { msg | ProtoMask } else { msg }
}
