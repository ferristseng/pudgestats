package com.dota.helper

import org.scalatest.FunSuite

class NameHelperSpec 
  extends FunSuite
{
  test("Hero (Night Stalker) is a hero") {
    assert(NameHelper.isHero("npc_dota_hero_night_stalker"))
  }

  test("Dire Creep is not a hero") {
    assert(!NameHelper.isHero("npc_dota_creep_badguys_melee"))
  }

  test("Radiant Creep is a creep") {
    assert(NameHelper.isCreep("npc_dota_creep_goodguys_melee"))
  }

  test("Prophet's Treants are a creep") {
    assert(NameHelper.isCreep("npc_dota_furion_treant"))
  }

  test("Radiant Siege Creep is a creep") {
    assert(NameHelper.isCreep("npc_dota_goodguys_siege"))
  }

  test("Upgraded Dire Creep is a creep") {
    assert(NameHelper.isCreep("npc_dota_creep_badguys_ranged_upgraded"))
  }

  test("Neutral Wildkin is not a creep") {
    assert(!NameHelper.isCreep("npc_dota_neutral_wildkin"))
  }
}
