package com.steam.net

import java.io.ByteArrayInputStream

import com.steam.const.EMsg
import com.steam.net.msg.MsgHdr

import com.google.protobuf.CodedInputStream

trait PacketMsg {
  def msg: EMsg.EMsg
  def targetJobID: Long
  def sourceJobID: Long
  def data: Array[Byte]

  def msg_=(msg: EMsg.EMsg)
  def targetJobID_=(id: Long)
  def sourceJobID_=(id: Long)

  def isProto: Boolean
  override def toString: String = 
    s"<PacketMsg(T) msg='${this.msg}' proto='${this.isProto}'>"
}

package object msg {
  class PacketMsg(
      var msg: EMsg.EMsg, 
      var data: Array[Byte]) 
    extends com.steam.net.PacketMsg {
    var hdr = new MsgHdr

    hdr.deserialize(CodedInputStream.newInstance(
      new ByteArrayInputStream(data)))

    var targetJobID = hdr.targetJobID
    var sourceJobID = hdr.sourceJobID

    def isProto = false
  }

  class PacketClientMsg(
      var msg: EMsg.EMsg,
      var data: Array[Byte])
    extends com.steam.net.PacketMsg {
    var hdr = new ExtendedClientMsgHdr
    
    hdr.deserialize(CodedInputStream.newInstance(
      new ByteArrayInputStream(data)))

    var targetJobID = hdr.targetJobID
    var sourceJobID = hdr.sourceJobID

    def isProto = false
  }

  class PacketClientMsgProtoBuf(
      var msg: EMsg.EMsg,
      var data: Array[Byte])
    extends com.steam.net.PacketMsg {
    var hdr = new MsgHdrProtoBuf
    
    hdr.deserialize(CodedInputStream.newInstance(
      new ByteArrayInputStream(data)))

    var targetJobID = hdr.proto.getJobidTarget
    var sourceJobID = hdr.proto.getJobidSource
    def isProto = true
  }
}
