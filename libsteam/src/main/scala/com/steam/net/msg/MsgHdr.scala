package com.steam.net.msg

import java.nio.{ByteBuffer, ByteOrder}

import com.steam.const.EMsg
import com.steam.util.MsgUtil
import com.steam.net.SteamSerializableHeader
import com.steam.proto.SteammessagesBase.CMsgProtoBufHeader

import com.google.protobuf.{CodedInputStream, CodedOutputStream}

class MsgHdr extends SteamSerializableHeader {
  var msg: EMsg.EMsg        = EMsg.Invalid
  var targetJobID           = Long.MaxValue
  var sourceJobID           = Long.MaxValue

  def serialize(stream: CodedOutputStream) = {
    stream.writeRawLittleEndian32(msg.id)
    stream.writeRawLittleEndian64(targetJobID)
    stream.writeRawLittleEndian64(sourceJobID)
    stream.flush
  }

  def deserialize(stream: CodedInputStream) = {
    msg         = EMsg(stream.readRawLittleEndian32)
    targetJobID = stream.readRawLittleEndian64
    sourceJobID = stream.readRawLittleEndian64
  }
}

class MsgHdrProtoBuf extends SteamSerializableHeader {
  var msg: EMsg.EMsg        = EMsg.Invalid
  var headerLength: Integer = 0
  var proto                 = CMsgProtoBufHeader.newBuilder

  def serialize(stream: CodedOutputStream) = {
    val arr       = proto.build.toByteArray
    headerLength  = arr.length

    stream.writeRawLittleEndian32(MsgUtil.makeMsg(msg.id, true))
    stream.writeRawLittleEndian32(headerLength)
    stream.writeRawBytes(arr, 0, headerLength)
    stream.flush
  }

  def deserialize(stream: CodedInputStream) = {
    msg           = MsgUtil.getMsg(stream.readRawLittleEndian32)
    headerLength  = stream.readRawLittleEndian32
    proto         = CMsgProtoBufHeader.newBuilder
    var arr       = stream.readRawBytes(headerLength)
    proto.mergeFrom(arr)
  }
}

class ExtendedClientMsgHdr extends SteamSerializableHeader {
  var msg: EMsg.EMsg        = EMsg.Invalid
  var headerSize: Byte      = 36.toByte
  var headerVersion: Short  = 2
  var targetJobID           = Long.MaxValue
  var sourceJobID           = Long.MaxValue
  var headerCanary: Byte    = 239.toByte
  var steamID: Long         = 0.toLong
  var sessionID: Int        = 0

  def serialize(stream: CodedOutputStream) = {
    stream.writeRawLittleEndian32(msg.id)
    stream.writeRawLittleEndian32(headerSize.toInt)
    stream.writeRawBytes(ByteBuffer.allocate(2)
                                   .putShort(headerVersion)
                                   .order(ByteOrder.LITTLE_ENDIAN)
                                   .array)
    stream.writeRawLittleEndian64(targetJobID)
    stream.writeRawLittleEndian64(sourceJobID)
    stream.writeRawByte(headerCanary.toInt)
    stream.writeRawLittleEndian64(steamID)
    stream.writeRawLittleEndian32(sessionID)
    stream.flush
  }

  def deserialize(stream: CodedInputStream) = {
    msg           = EMsg(stream.readRawLittleEndian32)
    headerSize    = stream.readRawByte
    headerVersion = ByteBuffer.wrap(stream.readRawBytes(2))
                              .order(ByteOrder.LITTLE_ENDIAN)
                              .getShort
    targetJobID   = stream.readRawLittleEndian64
    sourceJobID   = stream.readRawLittleEndian64
    headerCanary  = stream.readRawByte
    steamID       = stream.readRawLittleEndian64
    sessionID     = stream.readRawLittleEndian32
  }
}
