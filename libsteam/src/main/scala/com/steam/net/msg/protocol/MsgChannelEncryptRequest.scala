package com.steam.net.msg.protocol

import com.steam.net.SteamSerializableMsg
import com.steam.const.{Protocol, EMsg, EUniverse}

import com.google.protobuf.{CodedInputStream, CodedOutputStream}

class MsgChannelEncryptRequest extends SteamSerializableMsg {
  var msg             = EMsg.ChannelEncryptRequest
  var protocolVersion = Protocol.version
  var euniverse       = EUniverse.Invalid

  def serialize(stream: CodedOutputStream) = {
    stream.writeRawLittleEndian32(protocolVersion)
    stream.writeRawLittleEndian32(euniverse.id)
    stream.flush()
  }

  def deserialize(stream: CodedInputStream) = {
    protocolVersion = stream.readRawLittleEndian32
    euniverse = EUniverse(stream.readRawLittleEndian32)
  }

  override def toString = 
    s"<MsgChannelEncryptRequest univ='${this.euniverse}' ver='${this.protocolVersion}'>"
}
