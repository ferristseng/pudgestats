package com.steam.net.gc.msg

import java.nio.{ByteBuffer, ByteOrder}

import com.google.protobuf.{CodedOutputStream, CodedInputStream}

import com.steam.util.MsgUtil
import com.steam.net.GCSteamSerializableHeader
import com.steam.proto.SteammessagesBase.CMsgProtoBufHeader

class MsgGCHdr extends GCSteamSerializableHeader {
  var msg: Int              = 0 // Unused
  var headerVersion: Short  = 1
  var targetJobID           = Long.MaxValue
  var sourceJobID           = Long.MaxValue

  def serialize(stream: CodedOutputStream) = {
    stream.writeRawBytes(ByteBuffer.allocate(2)
                                   .putShort(headerVersion)
                                   .order(ByteOrder.LITTLE_ENDIAN)
                                   .array)
    stream.writeRawLittleEndian64(targetJobID)
    stream.writeRawLittleEndian64(sourceJobID)
    stream.flush
  }

  def deserialize(stream: CodedInputStream) = {
    headerVersion = ByteBuffer.wrap(stream.readRawBytes(2))
                                          .order(ByteOrder.LITTLE_ENDIAN)
                                          .getShort
    targetJobID = stream.readRawLittleEndian64
    sourceJobID = stream.readRawLittleEndian64
  }
}

class MsgGCHdrProtoBuf extends GCSteamSerializableHeader {
  var msg: Int              = 0  
  var headerLength: Int     = 0
  var proto                 = CMsgProtoBufHeader.newBuilder

  def serialize(stream: CodedOutputStream) = {
    val arr = proto.build.toByteArray
    headerLength = arr.length

    stream.writeRawLittleEndian32(MsgUtil.makeGCMsg(msg, true))
    stream.writeRawLittleEndian32(headerLength)
    stream.writeRawBytes(arr)
    stream.flush
  }

  def deserialize(stream: CodedInputStream) = {
    msg           = MsgUtil.getGCMsg(stream.readRawLittleEndian32)
    headerLength  = stream.readRawLittleEndian32
    proto         = CMsgProtoBufHeader.newBuilder
    var arr       = stream.readRawBytes(headerLength)
    proto.mergeFrom(arr)
  }
}
