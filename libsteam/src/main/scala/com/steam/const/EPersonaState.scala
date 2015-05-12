package com.steam.const

object EPersonaState extends Enumeration {
  type EPersonaState = Value
  val Offline = Value(0)
  val Online = Value(1)
  val Busy = Value(2)
  val Away = Value(3)
  val Snooze = Value(4)
  val LookingToTrade = Value(5)
  val LookingToPlay = Value(6)
  val Max = Value(7)
}
