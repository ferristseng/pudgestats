package com.steam.net.msg

import com.steam.const.EMsg
import com.steam.net.ClientMsg
import com.steam.net.{MsgBaseWithHdr, SteamSerializableMsg}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.google.protobuf.{CodedInputStream, CodedOutputStream}

object Msg {
  /** Constructors for a Msg[T]
    * 
    * Use these constructors, which are varied depending 
    * on their duty within the application.
    */
  def createSend[T <: SteamSerializableMsg](
    payloadReserve: Int = 0)(
    implicit m: Manifest[T])
  : Msg[T] = {
    new Msg[T](payloadReserve)
  }

  def createReply[T <: SteamSerializableMsg](
    _msg: MsgBaseWithHdr[MsgHdr],
    payloadReserve: Int = 0)(
    implicit m: Manifest[T])
  : Msg[T] = {
    var msg = new Msg[T]()
    msg.targetJobID = _msg.header.targetJobID
    msg
  }

  def createReceived[T <: SteamSerializableMsg](
    pkt: com.steam.net.PacketMsg)(
    implicit m: Manifest[T])
  : Msg[T] = {
    var msg = new Msg[T]()
    msg.deserialize(pkt.data)
    msg
  }
}

class Msg[T <: SteamSerializableMsg : Manifest] private (
  payloadReserve: Int = 64)
  extends MsgBaseWithHdr[MsgHdr](payloadReserve)
  with ClientMsg
{
  var body: T = manifest[T].runtimeClass
                           .newInstance
                           .asInstanceOf[T]
  this.header.msg       = body.msg
  def msg: EMsg.EMsg    = this.header.msg
  var sessionID: Int    = 0
  var steamID: Long     = 0
  def targetJobID: Long = this.header.targetJobID
  def sourceJobID: Long = this.header.sourceJobID
 
  def msg_=(msg: EMsg.EMsg)   = this.header.msg = msg
  def targetJobID_=(id: Long) = this.header.targetJobID = id
  def sourceJobID_=(id: Long) = this.header.sourceJobID = id

  def isProto = false
  def serialize(): Array[Byte] = {
    val outstream = new ByteArrayOutputStream()
    val writer = CodedOutputStream.newInstance(outstream)

    this.header.serialize(writer)
    this.body.serialize(writer)
    outstream.write(this.outstream.toByteArray)

    outstream.toByteArray
  }

  def deserialize(data: Array[Byte]) = {
    val in = new ByteArrayInputStream(data)
    val reader = CodedInputStream.newInstance(in)

    this.header.deserialize(reader)
    this.body.deserialize(reader)

    val len = in.available
    val off = data.length - len

    this.payload.writeRawBytes(data, off, len)
    this.payload.flush
  }

  override def toString = body.toString
}
