package com.steam.net

import com.google.protobuf.{CodedOutputStream, CodedInputStream}

import com.steam.const.EMsg

trait SteamSerializable {
  def serialize(stream: CodedOutputStream)
  def deserialize(stream: CodedInputStream)
}

trait SteamSerializableHeader extends SteamSerializable {
  def msg_=(msg: EMsg.EMsg)
}

trait SteamSerializableMsg extends SteamSerializable {
  def msg: EMsg.EMsg
}
