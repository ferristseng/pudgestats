package com.steam.net.msg.protocol

import com.steam.net.SteamSerializableMsg
import com.steam.const.{Protocol, EMsg}

import com.google.protobuf.{CodedInputStream, CodedOutputStream}

class MsgChannelEncryptResponse extends SteamSerializableMsg {
  var msg             = EMsg.ChannelEncryptResponse
  var protocolVersion = Protocol.version
  var keysize         = 128

  def serialize(stream: CodedOutputStream) = {
    stream.writeRawLittleEndian32(protocolVersion)
    stream.writeRawLittleEndian32(keysize)
    stream.flush()
  }

  def deserialize(stream: CodedInputStream) = {
    protocolVersion = stream.readRawLittleEndian32
    keysize = stream.readRawLittleEndian32
  }

  override def toString = 
    s"<MsgChannelEncryptResponse ver='${this.protocolVersion}'>"
}
