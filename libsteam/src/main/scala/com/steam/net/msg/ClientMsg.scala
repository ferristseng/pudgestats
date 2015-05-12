package com.steam.net

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, IOException}

import com.steam.const.EMsg
import com.steam.net.msg.MsgHdrProtoBuf

import com.google.protobuf.{GeneratedMessage, AbstractMessage}
import com.google.protobuf.{CodedInputStream, CodedOutputStream}

abstract class MsgBase(payloadReserve: Int = 64) {
  var outstream = new ByteArrayOutputStream(payloadReserve)
  var payload = CodedOutputStream.newInstance(outstream)
}

abstract class MsgBaseWithHdr[T <: SteamSerializableHeader : Manifest](
    payloadReserve: Int = 64)
  extends MsgBase(payloadReserve) 
{
  var header: T = manifest[T].runtimeClass.newInstance.asInstanceOf[T] 
}

trait ClientMsg {
  def msg: EMsg.EMsg
  def sessionID: Int
  def steamID: Long
  def targetJobID: Long
  def sourceJobID: Long

  def msg_=(msg: EMsg.EMsg)
  def sessionID_=(sessionID: Int)
  def steamID_=(steamID: Long)
  def sourceJobID_=(sourceJobID: Long)
  def targetJobID_=(targetJobID: Long)

  def isProto: Boolean
  def serialize: Array[Byte]
  def deserialize(data: Array[Byte])
}

package msg {
  /** ClientMsgProtoBuf helper object
    *
    * Provides public constructors for a ClientMsgProtoBuf, depending on 
    * how it will be used.
    *
    * {{{
    * var msg = ClientMsgProtoBuf.createSend[
    *   CMsgClientHeartBeat.Builder, 
    *   CMsgClientHeartBeat](EMsg.ClientHeartBeat)
    * }}}
    */
  object ClientMsgProtoBuf {
    /** Creates a ClientMsgProtoBuf to be sent */
    def createSend[T <: GeneratedMessage.Builder[T], U <: AbstractMessage](
        msg: EMsg.EMsg, 
        payloadReserve: Int = 0)(
        implicit m: Manifest[T],
        u: Manifest[U])
    : ClientMsgProtoBuf[T, U] = {
      new ClientMsgProtoBuf[T, U](msg, payloadReserve)
    }

    /** Creates a ClientMsgProtoBuf to respond with */
    def createReply[T <: GeneratedMessage.Builder[T], U <: AbstractMessage](
        msg: EMsg.EMsg,
        base: MsgBaseWithHdr[MsgHdrProtoBuf],
        payloadReserve: Int = 0)(
        implicit m: Manifest[T],
        u: Manifest[U])
    : ClientMsgProtoBuf[T, U] = {
      var clientMsg = new ClientMsgProtoBuf[T, U](msg, payloadReserve)
      clientMsg.sourceJobID = base.header.proto.getJobidSource
      clientMsg
    }

    /** Creates a ClientMsgProtoBuf that was received */
    def createReceived[T <: GeneratedMessage.Builder[T], U <: AbstractMessage](
        msg: com.steam.net.PacketMsg)(
        implicit m: Manifest[T], 
        u: Manifest[U])
    : ClientMsgProtoBuf[T, U] = {
      var clientMsg = new ClientMsgProtoBuf[T, U](msg.msg)
      clientMsg.deserialize(msg.data)
      clientMsg
    }
  }

  /** Instance of a ClientMsg with ProtoBuf body
    *
    * Only use the helper object ClientMsgProtoBuf to create.
    */
  class ClientMsgProtoBuf[
      T <: GeneratedMessage.Builder[T] : Manifest, 
      U <: AbstractMessage : Manifest] private
    (
      _msg: EMsg.EMsg,
      payloadReserve: Int = 64
    ) extends MsgBaseWithHdr[MsgHdrProtoBuf](payloadReserve) 
      with ClientMsg 
    {
    this.msg            = _msg

    def msg             = header.msg
    def sessionID       = header.proto.getClientSessionid
    def steamID         = header.proto.getSteamid
    def targetJobID     = header.proto.getJobidTarget
    def sourceJobID     = header.proto.getJobidSource

    def msg_=(msg: EMsg.EMsg)   = header.msg = msg
    def sessionID_=(id: Int)    = header.proto.setClientSessionid(id)
    def steamID_=(id: Long)     = header.proto.setSteamid(id)
    def sourceJobID_=(id: Long) = header.proto.setJobidSource(id)
    def targetJobID_=(id: Long) = header.proto.setJobidTarget(id)

    var body: T = manifest[U].runtimeClass
                             .getMethod("newBuilder")
                             .invoke(null)
                             .asInstanceOf[T]
    
    def isProto: Boolean = true
    def serialize: Array[Byte] = {
      var stream = new ByteArrayOutputStream

      this.header.serialize(CodedOutputStream.newInstance(stream))
      stream.write(body.build.toByteArray)
      this.outstream.writeTo(stream)

      stream.toByteArray
    }

    def deserialize(data: Array[Byte]) = {
      val in = new ByteArrayInputStream(data)
      val reader = CodedInputStream.newInstance(in)

      this.header.deserialize(reader)
      this.body.mergeFrom(reader)

      val off = reader.getTotalBytesRead
      val len = data.length - off

      this.payload.writeRawBytes(data, off, len)
      this.payload.flush
    }
  }
}
