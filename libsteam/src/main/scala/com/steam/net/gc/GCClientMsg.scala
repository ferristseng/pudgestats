package com.steam.net.gc

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, IOException}

import com.google.protobuf.{GeneratedMessage, AbstractMessage}
import com.google.protobuf.{CodedInputStream, CodedOutputStream}

import com.steam.net.gc.msg.MsgGCHdrProtoBuf
import com.steam.net.{GCSteamSerializableHeader, MsgBase}

trait ClientGCMsg {
  def msg: Int 
  def targetJobID: Long
  def sourceJobID: Long

  def msg_=(msg: Int)
  def targetJobID_=(id: Long)
  def sourceJobID_=(id: Long)

  def isProto: Boolean
  def serialize: Array[Byte]
  def deserialize(data: Array[Byte])
}

abstract class GCMsgBase[T <: GCSteamSerializableHeader : Manifest](
    payloadReserve: Int = 0) 
  extends MsgBase(payloadReserve)
  with ClientGCMsg 
{
  var header: T = manifest[T].runtimeClass.newInstance.asInstanceOf[T] 
}

package msg {
  /** ClientGCMsgProtoBuf helper object
    * 
    * Provides public instance constructors for a ClientMsgProtoBuf, 
    * depending on how it will be used
    *
    * {{{
    * var msg = ClientGCMsgProtoBuf.createSend[
    *   CMsgClientHeartBeat.Builder,
    *   CMsgClientHeartBeat](EMsg.ClientHeartBeat)
    * }}}
    */
  object ClientGCMsgProtoBuf {
    /** Creates a ClientGCMsgProtoBuf to be sent */
    def createSend[T <: GeneratedMessage.Builder[T], U <: AbstractMessage](
        msg: Int,
        payloadReserve: Int = 64)(
        implicit m: Manifest[T],
        u: Manifest[U])
    : ClientGCMsgProtoBuf[T, U] = {
      new ClientGCMsgProtoBuf[T, U](msg, payloadReserve)
    }

    /** Creates a ClientGCMsgProtoBuf to respond with */
    def createReply[T <: GeneratedMessage.Builder[T], U <: AbstractMessage](
        msg: Int,
        base: GCMsgBase[MsgGCHdrProtoBuf],
        payloadReserve: Int = 64)(
        implicit m: Manifest[T],
        u: Manifest[U])
    : ClientGCMsgProtoBuf[T, U] = {
      var clientMsg = new ClientGCMsgProtoBuf[T, U](msg, payloadReserve)
      clientMsg.sourceJobID = base.header.proto.getJobidSource
      clientMsg
    }

    /** Creates a ClientGCMsgProtoBuf that was received */
    def createReceived[T <: GeneratedMessage.Builder[T], U <: AbstractMessage](
        pkt: com.steam.net.gc.PacketGCMsg)(
        implicit m: Manifest[T],
        u: Manifest[U])
    : ClientGCMsgProtoBuf[T, U] = {
      var clientMsg = new ClientGCMsgProtoBuf[T, U](pkt.msg)
      clientMsg.deserialize(pkt.data)
      clientMsg
    }
  }

  /** Instance of a ClientGCMsg with a ProtoBuf body */
  class ClientGCMsgProtoBuf[
      T <: GeneratedMessage.Builder[T] : Manifest,
      U <: AbstractMessage : Manifest] private
    (
      _msg: Int,
      payloadReserve: Int = 64
    ) extends GCMsgBase[MsgGCHdrProtoBuf](payloadReserve) 
  {
    this.msg              = _msg

    def msg: Int          = this.header.msg
    def targetJobID: Long = this.header.proto.getJobidTarget
    def sourceJobID: Long = this.header.proto.getJobidSource
  
    def msg_=(msg: Int)         = this.header.msg = msg
    def targetJobID_=(id: Long) = this.header.proto.setJobidTarget(id)
    def sourceJobID_=(id: Long) = this.header.proto.setJobidSource(id)

    var body: T = manifest[U].runtimeClass
                             .getMethod("newBuilder")
                             .invoke(null)
                             .asInstanceOf[T]
    

    def isProto: Boolean = true
    def serialize: Array[Byte] = {
      var stream = new ByteArrayOutputStream

      this.header.serialize(CodedOutputStream.newInstance(stream))
      stream.write(this.body.build.toByteArray)
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
