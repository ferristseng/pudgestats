package com.steam.net.gc

import java.io.ByteArrayInputStream

import com.google.protobuf.CodedInputStream

trait PacketGCMsg {
  def msg: Int
  val targetJobID: Long
  val sourceJobID: Long
  def data: Array[Byte]

  def msg_=(msg: Int)

  def isProto: Boolean
  override def toString = 
    s"<PacketGCMsg msg='${msg}' isProto='${this.isProto}'>"
}

package object msg {
  class PacketClientGCMsgProtoBuf(
      var msg: Int, 
      val data: Array[Byte]) 
    extends com.steam.net.gc.PacketGCMsg 
  {
    var hdr = new MsgGCHdrProtoBuf

    hdr.deserialize(CodedInputStream.newInstance(
      new ByteArrayInputStream(data)))

    val targetJobID: Long = hdr.proto.getJobidTarget
    val sourceJobID: Long = hdr.proto.getJobidSource

    def isProto: Boolean = true
  }

  class PacketClientGCMsg(
      var msg: Int,
      val data: Array[Byte])
    extends com.steam.net.gc.PacketGCMsg 
  {
    var hdr = new MsgGCHdr

    hdr.deserialize(CodedInputStream.newInstance(
      new ByteArrayInputStream(data)))

    val targetJobID: Long = hdr.targetJobID
    val sourceJobID: Long = hdr.sourceJobID

    def isProto: Boolean = false
  }
}
