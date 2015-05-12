package com.steam.util

import com.steam.const.EOSType

object OSHelper {
  /** Returns the EOSType from the system property */
  def getOSType: EOSType.EOSType = {
    System.getProperty("os.name") match {
      case "Windows 7" => EOSType.Windows7
      case "Windows 2003" => EOSType.Win2003
      case "Windows XP" => EOSType.WinXP
      case "Windows 2000" => EOSType.Win200
      case "Windows NT" => EOSType.WinNT
      case "Windows ME" => EOSType.WinME
      case "Windows 98" => EOSType.Win98
      case "Windows 95" => EOSType.Win95
      case os if os.startsWith("Win") => EOSType.WinUnknown
      case os if os.startsWith("Mac") => EOSType.MacOSUnknown
      case os if os.startsWith("nix") => EOSType.LinuxUnknown
      case _ => EOSType.Unknown
    }
  }
}
