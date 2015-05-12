package com.steam.util

import java.nio.ByteBuffer
import java.net.InetAddress

object NetHelper {
  /** Returns a Long representation of an IP address */
  def getIPAddress(addr: InetAddress): Long = {
    val buf = ByteBuffer.wrap(addr.getAddress)
    buf.getInt & 0xFFFFFFFFL
  }
}
