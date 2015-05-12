package com.steam.util

import java.util.UUID
import java.security.MessageDigest

object MachineID {
  def generate(): Array[Byte] = {
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(UUID.randomUUID.toString.getBytes)
    digest.digest
  }
}
