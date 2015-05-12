package com.steam.net

import com.steam.const.EMsg

trait GCSteamSerializableHeader extends SteamSerializable {
  def msg_=(msg: Int)
}

trait GCSteamSerializableMsg extends SteamSerializable {
  def msg: Int
}
