package com.steam.net

import java.lang.IllegalArgumentException

import com.steam.util.CryptoHelper

/** Encrypts and decrypts bytes */
class NetFilter(private val sessionKey: Array[Byte]) {
  if (sessionKey.length != 32) {
    throw new IllegalArgumentException("session key is not proper length")
  }

  def encrypt(in: Array[Byte]): Array[Byte] = {
    CryptoHelper.symmetricEncrypt(in, sessionKey)
  }

  def decrypt(in: Array[Byte]): Array[Byte] = {
    CryptoHelper.symmetricDecrypt(in, sessionKey)
  }
}
