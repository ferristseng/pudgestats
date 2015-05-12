package com.dota.helper

import com.dota.data.{Hero, Creep, Neutral, Building}

object NameHelper {
  private val runeModifiers = Set(
    "modifier_rune_doubledamage",
    "modifier_rune_regen",
    "modifier_rune_haste",
    "modifier_rune_invis",
    "modifier_illusion"
  )

  def isRune(name: String)      = runeModifiers.contains(name)
  def isNeutral(name: String)   = !Neutral.fromString(name).isEmpty
  def isHero(name: String)      = !Hero.fromString(name).isEmpty
  def isCreep(name: String)     = !Creep.fromString(name).isEmpty 
  def isBuilding(name: String)  = !Building.fromString(name).isEmpty
}
