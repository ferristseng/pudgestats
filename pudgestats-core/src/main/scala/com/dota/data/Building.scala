/** File generated with `generate.py` on 2014-09-11 05:37:43.970775 */

package com.dota.data

object Building {
  private val innermap = Map(
    "dota_fountain" -> new DotaEntity("Fountain", "dota_fountain"),
    "npc_dota_goodguys_tower1_top" -> new DotaEntity("Tower", "npc_dota_goodguys_tower1_top"),
    "npc_dota_goodguys_tower1_mid" -> new DotaEntity("Tower", "npc_dota_goodguys_tower1_mid"),
    "npc_dota_goodguys_tower1_bot" -> new DotaEntity("Tower", "npc_dota_goodguys_tower1_bot"),
    "npc_dota_goodguys_tower2_top" -> new DotaEntity("Tower", "npc_dota_goodguys_tower2_top"),
    "npc_dota_goodguys_tower2_mid" -> new DotaEntity("Tower", "npc_dota_goodguys_tower2_mid"),
    "npc_dota_goodguys_tower2_bot" -> new DotaEntity("Tower", "npc_dota_goodguys_tower2_bot"),
    "npc_dota_goodguys_tower3_top" -> new DotaEntity("Tower", "npc_dota_goodguys_tower3_top"),
    "npc_dota_goodguys_tower3_mid" -> new DotaEntity("Tower", "npc_dota_goodguys_tower3_mid"),
    "npc_dota_goodguys_tower3_bot" -> new DotaEntity("Tower", "npc_dota_goodguys_tower3_bot"),
    "npc_dota_goodguys_tower4" -> new DotaEntity("Tower", "npc_dota_goodguys_tower4"),
    "npc_dota_badguys_tower1_top" -> new DotaEntity("Tower", "npc_dota_badguys_tower1_top"),
    "npc_dota_badguys_tower1_mid" -> new DotaEntity("Tower", "npc_dota_badguys_tower1_mid"),
    "npc_dota_badguys_tower1_bot" -> new DotaEntity("Tower", "npc_dota_badguys_tower1_bot"),
    "npc_dota_badguys_tower2_top" -> new DotaEntity("Tower", "npc_dota_badguys_tower2_top"),
    "npc_dota_badguys_tower2_mid" -> new DotaEntity("Tower", "npc_dota_badguys_tower2_mid"),
    "npc_dota_badguys_tower2_bot" -> new DotaEntity("Tower", "npc_dota_badguys_tower2_bot"),
    "npc_dota_badguys_tower3_top" -> new DotaEntity("Tower", "npc_dota_badguys_tower3_top"),
    "npc_dota_badguys_tower3_mid" -> new DotaEntity("Tower", "npc_dota_badguys_tower3_mid"),
    "npc_dota_badguys_tower3_bot" -> new DotaEntity("Tower", "npc_dota_badguys_tower3_bot"),
    "npc_dota_badguys_tower4" -> new DotaEntity("Tower", "npc_dota_badguys_tower4"),
    "npc_dota_goodguys_melee_rax_top" -> new DotaEntity("Melee Barracks", "npc_dota_goodguys_melee_rax_top"),
    "npc_dota_goodguys_melee_rax_mid" -> new DotaEntity("Melee Barracks", "npc_dota_goodguys_melee_rax_mid"),
    "npc_dota_goodguys_melee_rax_bot" -> new DotaEntity("Melee Barracks", "npc_dota_goodguys_melee_rax_bot"),
    "npc_dota_badguys_melee_rax_top" -> new DotaEntity("Melee Barracks", "npc_dota_badguys_melee_rax_top"),
    "npc_dota_badguys_melee_rax_mid" -> new DotaEntity("Melee Barracks", "npc_dota_badguys_melee_rax_mid"),
    "npc_dota_badguys_melee_rax_bot" -> new DotaEntity("Melee Barracks", "npc_dota_badguys_melee_rax_bot"),
    "npc_dota_goodguys_range_rax_top" -> new DotaEntity("Ranged Barracks", "npc_dota_goodguys_range_rax_top"),
    "npc_dota_goodguys_range_rax_mid" -> new DotaEntity("Ranged Barracks", "npc_dota_goodguys_range_rax_mid"),
    "npc_dota_goodguys_range_rax_bot" -> new DotaEntity("Ranged Barracks", "npc_dota_goodguys_range_rax_bot"),
    "npc_dota_badguys_range_rax_top" -> new DotaEntity("Ranged Barracks", "npc_dota_badguys_range_rax_top"),
    "npc_dota_badguys_range_rax_mid" -> new DotaEntity("Ranged Barracks", "npc_dota_badguys_range_rax_mid"),
    "npc_dota_badguys_range_rax_bot" -> new DotaEntity("Ranged Barracks", "npc_dota_badguys_range_rax_bot"),
    "npc_dota_goodguys_fort" -> new DotaEntity("Radiant's Ancient", "npc_dota_goodguys_fort"),
    "npc_dota_badguys_fort" -> new DotaEntity("Dire's Ancient", "npc_dota_badguys_fort"),
    "npc_dota_goodguys_fillers" -> new DotaEntity("Building", "npc_dota_goodguys_fillers"),
    "npc_dota_badguys_fillers" -> new DotaEntity("Building", "npc_dota_badguys_fillers"),
    "npc_dota_healing_campfire" -> new DotaEntity("Healing Campfire", "npc_dota_healing_campfire"),
    "npc_dota_roquelaire" -> new DotaEntity("Roquelaire", "npc_dota_roquelaire")
  )

  def fromString(s: String): Option[DotaEntity] = { 
    this.innermap.get(s)
  }
}
