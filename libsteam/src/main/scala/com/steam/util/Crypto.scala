package com.steam.util

import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import java.nio.ByteBuffer
import java.util.zip.CRC32
import java.security.spec.X509EncodedKeySpec
import java.security.{SecureRandom, KeyFactory, Security}
                      

import org.bouncycastle.jce.provider.BouncyCastleProvider

class RSACrypto(key: Array[Byte]) {
  Security.addProvider(new BouncyCastleProvider)

  private val factory = KeyFactory.getInstance("RSA")
  private val rsa     = new X509EncodedKeySpec(key) 
  private var pubkey  = factory.generatePublic(rsa) 

  def encrypt(input: Array[Byte]): Array[Byte] = {
    val cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC")

    cipher.init(Cipher.ENCRYPT_MODE, this.pubkey)
    cipher.doFinal(input)
  }
}

object CryptoHelper {
  Security.addProvider(new BouncyCastleProvider)

  /** CRC Hash wrapper */
  def CRCHash(input: Array[Byte]): Array[Byte] = {
    var crc = new CRC32()
    var buf = ByteBuffer.allocate(4)
    crc.update(input)
    buf.putInt(crc.getValue.toInt)
    buf.array().reverse
  }

  /** Generates a block of random bytes, with a given size */
  def generateRandomBlock(size: Int): Array[Byte] = {
    var random = new SecureRandom()
    var block = new Array[Byte](size)
    random.nextBytes(block)
    block
  }

  /** Decrypt with AES */
  def symmetricDecrypt(in: Array[Byte], key: Array[Byte]): Array[Byte] = {
    var cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC")
    var iv = in.slice(0, 16)
    var text = in.slice(16, in.length)
    val aesKey = new SecretKeySpec(key, "AES")

    cipher.init(Cipher.DECRYPT_MODE, aesKey) 
    iv = cipher.doFinal(iv)

    cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC")
    cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv))

    cipher.doFinal(text)
  }

  /** Encrypt with AES */
  def symmetricEncrypt(in: Array[Byte], key: Array[Byte]): Array[Byte] = {
    var cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC")
    val aesKey = new SecretKeySpec(key, "AES")

    cipher.init(Cipher.ENCRYPT_MODE, aesKey)

    var iv = CryptoHelper.generateRandomBlock(16)
    var cryptoIv = cipher.doFinal(iv)
    cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC")
    cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv))

    cryptoIv ++ cipher.doFinal(in)
  }
}
