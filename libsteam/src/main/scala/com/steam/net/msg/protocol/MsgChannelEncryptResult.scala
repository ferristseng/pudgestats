package com.steam.net.msg.protocol

import com.steam.net.SteamSerializableMsg
import com.steam.const.{EMsg, EResult}

import com.google.protobuf.{CodedInputStream, CodedOutputStream}

class MsgChannelEncryptResult extends SteamSerializableMsg {
  var msg             = EMsg.ChannelEncryptResult
  var result          = EResult.Invalid

  def serialize(stream: CodedOutputStream) = {
    stream.writeRawLittleEndian32(result.id)
    stream.flush()
  }

  def deserialize(stream: CodedInputStream) = {
    result = EResult(stream.readRawLittleEndian32)
  }

  override def toString = 
    s"<MsgChannelEncryptResult result='${this.result}'>"
}
