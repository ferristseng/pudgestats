package com.steam.net.msg.protocol

object MsgClientLogon {
  val obfuscationMask = 0xBAADF00D
  val currentProtocol = 65579
  val protocolVerMajorMask = 0xFFFF0000
  val protocolVerMinorMask = 0xFFFF
  val protocolVerMinorMinGameServers: Short = 4
  val protocolVerMinorMinForSupportingEMsgMulti: Short = 12
  val protocolVerMinorMinForSupportingEMsgClientEncryptPct: Short = 14
  val protocolVerMinorMinForCellId: Short = 18
  val protocolVerMinorMinForSessionIDLast: Short = 19
  val protocolVerMinorMinForServerAvailablityMsgs: Short = 24
  val protocolVerMinorMinClients: Short = 25
  val protocolVerMinorMinForOSType: Short = 26
  val protocolVerMinorMinForCegApplyPESig: Short = 27
  val protocolVerMinorMinForMarketingMessages2: Short = 27
  val protocolVerMinorMinForAnyProtoBufMessages: Short = 28
  val protocolVerMinorMinForProtoBufLoggedOffMessage: Short = 28
  val protocolVerMinorMinForProtoBufMultiMessages: Short = 28
  val protocolVerMinorMinForSendingProtocolToUFS: Short = 30
  val protocolVerMinorMinForMachineAuth: Short = 33
  val protocolVerMinorMinForSessionIDLastAnon: Short = 36
  val protocolVerMinorMinForEnhancedAppList: Short = 40
  val protocolVerMinorMinForGzipMultiMessages: Short = 43
}
