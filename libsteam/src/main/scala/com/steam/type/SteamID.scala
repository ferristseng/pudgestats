package com.steam.types

import com.steam.const.{EAccountType, EUniverse}

object SteamID {
  val accountIDMask       = 0xFFFFFFFF
  val accountInstanceMask = 0x000FFFFF
  val allInstances        = 0
  val desktopInstance     = 1
  val consoleInstance     = 2
  val webInstance         = 4
}

class BitVector64(var data: Long) {
  def getData(bitOffset: Short, valueMask: Int) = {
    data >> bitOffset & valueMask 
  }

  def setData(bitOffset: Short, valueMask: Int, value: Long) = {
    data = (data & ~(valueMask << bitOffset)) | ((value & valueMask) << bitOffset) 
  }
}

/** Class representing a SteamID
  *
  * A lot of the functionality available in the 
  * original lib SteamKit2, is not available in 
  * this class.
  */
class SteamID(
  _accountId: Int,
  _instanceType: Int,
  _euniverse: EUniverse.EUniverse,
  _accountType: EAccountType.EAccountType) 
{
  private var steamid = new BitVector64(_accountId.toLong)

  steamid.setData(0, 0xFFFFFFFF, _accountId.toLong)
  steamid.setData(56, 0xFF, _euniverse.id.toLong)  
  steamid.setData(52, 0xF, _accountType.id.toLong) 
  steamid.setData(32, 0xFFFFF, _instanceType.toLong)

  def toLong: Long = steamid.data
}
