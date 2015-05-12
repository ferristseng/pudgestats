/** File generated with `generate.py` on 2014-09-11 05:37:43.970775 */

package com.dota.data

object Creep {
  private val innermap = Map(
    "ent_dota_halloffame" -> new DotaEntity("Aegis of Champions", "ent_dota_halloffame"),
    "npc_dota_creep" -> new DotaEntity("Creep", "npc_dota_creep"),
    "npc_dota_creep_lane" -> new DotaEntity("Lane Creep", "npc_dota_creep_lane"),
    "npc_dota_creep_siege" -> new DotaEntity("Siege Engine", "npc_dota_creep_siege"),
    "npc_dota_tower" -> new DotaEntity("Tower", "npc_dota_tower"),
    "npc_dota_barracks" -> new DotaEntity("Barracks", "npc_dota_barracks"),
    "npc_dota_fort" -> new DotaEntity("The Ancient", "npc_dota_fort"),
    "npc_dota_roshan" -> new DotaEntity("Roshan", "npc_dota_roshan"),
    "npc_dota_roshanboo" -> new DotaEntity("Roshanboo", "npc_dota_roshanboo"),
    "npc_dota_necronomicon_warrior_1" -> new DotaEntity("Necronomicon Warrior", "npc_dota_necronomicon_warrior_1"),
    "npc_dota_necronomicon_warrior_2" -> new DotaEntity("Necronomicon Warrior", "npc_dota_necronomicon_warrior_2"),
    "npc_dota_necronomicon_warrior_3" -> new DotaEntity("Necronomicon Warrior", "npc_dota_necronomicon_warrior_3"),
    "npc_dota_necronomicon_archer_1" -> new DotaEntity("Necronomicon Archer", "npc_dota_necronomicon_archer_1"),
    "npc_dota_necronomicon_archer_2" -> new DotaEntity("Necronomicon Archer", "npc_dota_necronomicon_archer_2"),
    "npc_dota_necronomicon_archer_3" -> new DotaEntity("Necronomicon Archer", "npc_dota_necronomicon_archer_3"),
    "npc_dota_scout_hawk" -> new DotaEntity("Scout Hawk", "npc_dota_scout_hawk"),
    "npc_dota_greater_hawk" -> new DotaEntity("Greater Hawk", "npc_dota_greater_hawk"),
    "npc_dota_beastmaster_boar" -> new DotaEntity("Boar", "npc_dota_beastmaster_boar"),
    "npc_dota_beastmaster_greater_boar" -> new DotaEntity("Greater Boar", "npc_dota_beastmaster_greater_boar"),
    "npc_dota_courier" -> new DotaEntity("Courier", "npc_dota_courier"),
    "npc_dota_flying_courier" -> new DotaEntity("Flying Courier", "npc_dota_flying_courier"),
    "npc_dota_furion_treant" -> new DotaEntity("Treant", "npc_dota_furion_treant"),
    "npc_dota_invoker_forged_spirit" -> new DotaEntity("Forged Spirit", "npc_dota_invoker_forged_spirit"),
    "npc_dota_dark_troll_warlord_skeleton_warrior" -> new DotaEntity("Skeleton Warrior", "npc_dota_dark_troll_warlord_skeleton_warrior"),
    "npc_dota_enraged_wildkin_tornado" -> new DotaEntity("Tornado", "npc_dota_enraged_wildkin_tornado"),
    "npc_dota_warlock_golem_1" -> new DotaEntity("Warlock Golem", "npc_dota_warlock_golem_1"),
    "npc_dota_warlock_golem_2" -> new DotaEntity("Warlock Golem", "npc_dota_warlock_golem_2"),
    "npc_dota_warlock_golem_3" -> new DotaEntity("Warlock Golem", "npc_dota_warlock_golem_3"),
    "npc_dota_warlock_golem_scepter_1" -> new DotaEntity("Warlock Golem", "npc_dota_warlock_golem_scepter_1"),
    "npc_dota_warlock_golem_scepter_2" -> new DotaEntity("Warlock Golem", "npc_dota_warlock_golem_scepter_2"),
    "npc_dota_warlock_golem_scepter_3" -> new DotaEntity("Warlock Golem", "npc_dota_warlock_golem_scepter_3"),
    "npc_dota_broodmother_spiderling" -> new DotaEntity("Spiderling", "npc_dota_broodmother_spiderling"),
    "npc_dota_broodmother_spiderite" -> new DotaEntity("Spiderite", "npc_dota_broodmother_spiderite"),
    "npc_dota_witch_doctor_death_ward" -> new DotaEntity("Death Ward", "npc_dota_witch_doctor_death_ward"),
    "npc_dota_shadow_shaman_ward_1" -> new DotaEntity("Serpent Ward", "npc_dota_shadow_shaman_ward_1"),
    "npc_dota_shadow_shaman_ward_2" -> new DotaEntity("Serpent Ward", "npc_dota_shadow_shaman_ward_2"),
    "npc_dota_shadow_shaman_ward_3" -> new DotaEntity("Serpent Ward", "npc_dota_shadow_shaman_ward_3"),
    "npc_dota_venomancer_plague_ward_1" -> new DotaEntity("Plague Ward", "npc_dota_venomancer_plague_ward_1"),
    "npc_dota_venomancer_plague_ward_2" -> new DotaEntity("Plague Ward", "npc_dota_venomancer_plague_ward_2"),
    "npc_dota_venomancer_plague_ward_3" -> new DotaEntity("Plague Ward", "npc_dota_venomancer_plague_ward_3"),
    "npc_dota_venomancer_plague_ward_4" -> new DotaEntity("Plague Ward", "npc_dota_venomancer_plague_ward_4"),
    "npc_dota_lesser_eidolon" -> new DotaEntity("Lesser Eidolon", "npc_dota_lesser_eidolon"),
    "npc_dota_eidolon" -> new DotaEntity("Eidolon", "npc_dota_eidolon"),
    "npc_dota_greater_eidolon" -> new DotaEntity("Greater Eidolon", "npc_dota_greater_eidolon"),
    "npc_dota_dire_eidolon" -> new DotaEntity("Dire Eidolon", "npc_dota_dire_eidolon"),
    "npc_dota_rattletrap_cog" -> new DotaEntity("Power Cog", "npc_dota_rattletrap_cog"),
    "npc_dota_juggernaut_healing_ward" -> new DotaEntity("Healing Ward", "npc_dota_juggernaut_healing_ward"),
    "npc_dota_templar_assassin_psionic_trap" -> new DotaEntity("Psionic Trap", "npc_dota_templar_assassin_psionic_trap"),
    "npc_dota_weaver_swarm" -> new DotaEntity("Beetle", "npc_dota_weaver_swarm"),
    "npc_dota_pugna_nether_ward_1" -> new DotaEntity("Nether Ward", "npc_dota_pugna_nether_ward_1"),
    "npc_dota_pugna_nether_ward_2" -> new DotaEntity("Nether Ward", "npc_dota_pugna_nether_ward_2"),
    "npc_dota_pugna_nether_ward_3" -> new DotaEntity("Nether Ward", "npc_dota_pugna_nether_ward_3"),
    "npc_dota_pugna_nether_ward_4" -> new DotaEntity("Nether Ward", "npc_dota_pugna_nether_ward_4"),
    "npc_dota_lycan_wolf1" -> new DotaEntity("Lycan Wolf", "npc_dota_lycan_wolf1"),
    "npc_dota_lycan_wolf2" -> new DotaEntity("Lycan Wolf", "npc_dota_lycan_wolf2"),
    "npc_dota_lycan_wolf3" -> new DotaEntity("Lycan Wolf", "npc_dota_lycan_wolf3"),
    "npc_dota_lycan_wolf4" -> new DotaEntity("Lycan Wolf", "npc_dota_lycan_wolf4"),
    "npc_dota_lone_druid_bear1" -> new DotaEntity("Spirit Bear", "npc_dota_lone_druid_bear1"),
    "npc_dota_lone_druid_bear2" -> new DotaEntity("Spirit Bear", "npc_dota_lone_druid_bear2"),
    "npc_dota_lone_druid_bear3" -> new DotaEntity("Spirit Bear", "npc_dota_lone_druid_bear3"),
    "npc_dota_lone_druid_bear4" -> new DotaEntity("Spirit Bear", "npc_dota_lone_druid_bear4"),
    "npc_dota_brewmaster_earth_1" -> new DotaEntity("Earth", "npc_dota_brewmaster_earth_1"),
    "npc_dota_brewmaster_earth_2" -> new DotaEntity("Earth", "npc_dota_brewmaster_earth_2"),
    "npc_dota_brewmaster_earth_3" -> new DotaEntity("Earth", "npc_dota_brewmaster_earth_3"),
    "npc_dota_brewmaster_earth_4" -> new DotaEntity("Earth", "npc_dota_brewmaster_earth_4"),
    "npc_dota_brewmaster_storm_1" -> new DotaEntity("Storm", "npc_dota_brewmaster_storm_1"),
    "npc_dota_brewmaster_storm_2" -> new DotaEntity("Storm", "npc_dota_brewmaster_storm_2"),
    "npc_dota_brewmaster_storm_3" -> new DotaEntity("Storm", "npc_dota_brewmaster_storm_3"),
    "npc_dota_brewmaster_storm_4" -> new DotaEntity("Storm", "npc_dota_brewmaster_storm_4"),
    "npc_dota_brewmaster_fire_1" -> new DotaEntity("Fire", "npc_dota_brewmaster_fire_1"),
    "npc_dota_brewmaster_fire_2" -> new DotaEntity("Fire", "npc_dota_brewmaster_fire_2"),
    "npc_dota_brewmaster_fire_3" -> new DotaEntity("Fire", "npc_dota_brewmaster_fire_3"),
    "npc_dota_brewmaster_fire_4" -> new DotaEntity("Fire", "npc_dota_brewmaster_fire_4"),
    "npc_dota_unit_tombstone1" -> new DotaEntity("Tombstone", "npc_dota_unit_tombstone1"),
    "npc_dota_unit_tombstone2" -> new DotaEntity("Tombstone", "npc_dota_unit_tombstone2"),
    "npc_dota_unit_tombstone3" -> new DotaEntity("Tombstone", "npc_dota_unit_tombstone3"),
    "npc_dota_unit_tombstone4" -> new DotaEntity("Tombstone", "npc_dota_unit_tombstone4"),
    "npc_dota_unit_undying_zombie" -> new DotaEntity("Undying Zombie", "npc_dota_unit_undying_zombie"),
    "npc_dota_unit_undying_zombie_torso" -> new DotaEntity("Undying Zombie", "npc_dota_unit_undying_zombie_torso"),
    "npc_dota_visage_familiar1" -> new DotaEntity("Familiar", "npc_dota_visage_familiar1"),
    "npc_dota_visage_familiar2" -> new DotaEntity("Familiar", "npc_dota_visage_familiar2"),
    "npc_dota_visage_familiar3" -> new DotaEntity("Familiar", "npc_dota_visage_familiar3"),
    "npc_dota_observer_wards" -> new DotaEntity("Observer Ward", "npc_dota_observer_wards"),
    "npc_dota_sentry_wards" -> new DotaEntity("Sentry Ward", "npc_dota_sentry_wards"),
    "npc_dota_gyrocopter_homing_missile" -> new DotaEntity("Homing Missile", "npc_dota_gyrocopter_homing_missile"),
    "npc_dota_tusk_frozen_sigil1" -> new DotaEntity("Frozen Sigil", "npc_dota_tusk_frozen_sigil1"),
    "npc_dota_tusk_frozen_sigil2" -> new DotaEntity("Frozen Sigil", "npc_dota_tusk_frozen_sigil2"),
    "npc_dota_tusk_frozen_sigil3" -> new DotaEntity("Frozen Sigil", "npc_dota_tusk_frozen_sigil3"),
    "npc_dota_tusk_frozen_sigil4" -> new DotaEntity("Frozen Sigil", "npc_dota_tusk_frozen_sigil4"),
    "npc_dota_elder_titan_ancestral_spirit" -> new DotaEntity("Astral Spirit", "npc_dota_elder_titan_ancestral_spirit"),
    "npc_dota_phoenix_sun" -> new DotaEntity("Phoenix Sun", "npc_dota_phoenix_sun"),
    "npc_dota_techies_land_mine" -> new DotaEntity("Land Mine", "npc_dota_techies_land_mine"),
    "npc_dota_techies_stasis_trap" -> new DotaEntity("Stasis Trap", "npc_dota_techies_stasis_trap"),
    "npc_dota_techies_remote_mine" -> new DotaEntity("Remote Mine", "npc_dota_techies_remote_mine"),
    "npc_dota_techies_minefield_sign" -> new DotaEntity("Minefield Sign", "npc_dota_techies_minefield_sign"),
    "npc_dota_creep_badguys_melee" -> new DotaEntity("Melee Creep", "npc_dota_creep_badguys_melee"),
    "npc_dota_creep_badguys_melee_upgraded" -> new DotaEntity("Mega Melee Creep", "npc_dota_creep_badguys_melee_upgraded"),
    "npc_dota_creep_badguys_ranged" -> new DotaEntity("Ranged Creep", "npc_dota_creep_badguys_ranged"),
    "npc_dota_creep_badguys_ranged_upgraded" -> new DotaEntity("Mega Ranged Creep", "npc_dota_creep_badguys_ranged_upgraded"),
    "npc_dota_badguys_siege" -> new DotaEntity("Siege Creep", "npc_dota_badguys_siege"),
    "npc_dota_badguys_siege_upgraded" -> new DotaEntity("Mega Siege Creep", "npc_dota_badguys_siege_upgraded"),
    "npc_dota_creep_goodguys_melee" -> new DotaEntity("Melee Creep", "npc_dota_creep_goodguys_melee"),
    "npc_dota_creep_goodguys_melee_upgraded" -> new DotaEntity("Mega Melee Creep", "npc_dota_creep_goodguys_melee_upgraded"),
    "npc_dota_creep_goodguys_ranged" -> new DotaEntity("Ranged Creep", "npc_dota_creep_goodguys_ranged"),
    "npc_dota_creep_goodguys_ranged_upgraded" -> new DotaEntity("Mega Ranged Creep", "npc_dota_creep_goodguys_ranged_upgraded"),
    "npc_dota_goodguys_siege" -> new DotaEntity("Siege Creep", "npc_dota_goodguys_siege"),
    "npc_dota_goodguys_siege_upgraded" -> new DotaEntity("Mega Siege Creep", "npc_dota_goodguys_siege_upgraded")
  )

  def fromString(s: String): Option[DotaEntity] = { 
    this.innermap.get(s)
  }
}