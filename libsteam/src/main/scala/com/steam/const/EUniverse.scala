package com.steam.const

object EUniverse extends Enumeration {
  type EUniverse = Value
  val Invalid = Value(0)
  val Public = Value(1)
  val Beta = Value(2)
  val Internal = Value(3)
  val Dev = Value(4)
  val Max = Value(5)
}
