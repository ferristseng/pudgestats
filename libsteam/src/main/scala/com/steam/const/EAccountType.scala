package com.steam.const

object EAccountType extends Enumeration{
  type EAccountType = Value
  val Invalid = Value(0)
  val Individual = Value(1)
  val Multiseat = Value(2)
  val GameServer = Value(3)
  val AnonGameServer = Value(4)
  val Pending = Value(5)
  val ContentServer = Value(6)
  val Clan = Value(7)
  val Chat = Value(8)
  val ConsoleUser = Value(9)
  val AnonUser = Value(10)
  val Max = Value(11)
}
