/** File generated with `generate.py` on 2014-09-11 05:37:43.970775 */

package com.dota.data

object Neutral {
  private val innermap = Map(
    "npc_dota_neutral_kobold" -> new DotaEntity("Kobold", "npc_dota_neutral_kobold"),
    "npc_dota_neutral_kobold_tunneler" -> new DotaEntity("Kobold Soldier", "npc_dota_neutral_kobold_tunneler"),
    "npc_dota_neutral_kobold_taskmaster" -> new DotaEntity("Kobold Foreman", "npc_dota_neutral_kobold_taskmaster"),
    "npc_dota_neutral_centaur_outrunner" -> new DotaEntity("Centaur Courser", "npc_dota_neutral_centaur_outrunner"),
    "npc_dota_neutral_centaur_khan" -> new DotaEntity("Centaur Conqueror", "npc_dota_neutral_centaur_khan"),
    "npc_dota_neutral_fel_beast" -> new DotaEntity("Fell Spirit", "npc_dota_neutral_fel_beast"),
    "npc_dota_neutral_polar_furbolg_champion" -> new DotaEntity("Hellbear", "npc_dota_neutral_polar_furbolg_champion"),
    "npc_dota_neutral_polar_furbolg_ursa_warrior" -> new DotaEntity("Hellbear Smasher", "npc_dota_neutral_polar_furbolg_ursa_warrior"),
    "npc_dota_neutral_mud_golem" -> new DotaEntity("Mud Golem", "npc_dota_neutral_mud_golem"),
    "npc_dota_neutral_ogre_mauler" -> new DotaEntity("Ogre Bruiser", "npc_dota_neutral_ogre_mauler"),
    "npc_dota_neutral_ogre_magi" -> new DotaEntity("Ogre Frostmage", "npc_dota_neutral_ogre_magi"),
    "npc_dota_neutral_giant_wolf" -> new DotaEntity("Giant Wolf", "npc_dota_neutral_giant_wolf"),
    "npc_dota_neutral_alpha_wolf" -> new DotaEntity("Alpha Wolf", "npc_dota_neutral_alpha_wolf"),
    "npc_dota_neutral_wildkin" -> new DotaEntity("Wildwing", "npc_dota_neutral_wildkin"),
    "npc_dota_neutral_enraged_wildkin" -> new DotaEntity("Wildwing Ripper", "npc_dota_neutral_enraged_wildkin"),
    "npc_dota_neutral_satyr_soulstealer" -> new DotaEntity("Satyr Mindstealer", "npc_dota_neutral_satyr_soulstealer"),
    "npc_dota_neutral_satyr_hellcaller" -> new DotaEntity("Satyr Tormenter", "npc_dota_neutral_satyr_hellcaller"),
    "npc_dota_neutral_satyr_trickster" -> new DotaEntity("Satyr Banisher", "npc_dota_neutral_satyr_trickster"),
    "npc_dota_neutral_jungle_stalker" -> new DotaEntity("Ancient Stalker", "npc_dota_neutral_jungle_stalker"),
    "npc_dota_neutral_elder_jungle_stalker" -> new DotaEntity("Ancient Primal Stalker", "npc_dota_neutral_elder_jungle_stalker"),
    "npc_dota_neutral_blue_dragonspawn_sorcerer" -> new DotaEntity("Ancient Drakken Sentinel", "npc_dota_neutral_blue_dragonspawn_sorcerer"),
    "npc_dota_neutral_blue_dragonspawn_overseer" -> new DotaEntity("Ancient Drakken Armorer", "npc_dota_neutral_blue_dragonspawn_overseer"),
    "npc_dota_neutral_rock_golem" -> new DotaEntity("Ancient Rock Golem", "npc_dota_neutral_rock_golem"),
    "npc_dota_neutral_granite_golem" -> new DotaEntity("Ancient Granite Golem", "npc_dota_neutral_granite_golem"),
    "npc_dota_neutral_big_thunder_lizard" -> new DotaEntity("Ancient Thunderhide", "npc_dota_neutral_big_thunder_lizard"),
    "npc_dota_neutral_small_thunder_lizard" -> new DotaEntity("Ancient Rumblehide", "npc_dota_neutral_small_thunder_lizard"),
    "npc_dota_neutral_black_drake" -> new DotaEntity("Ancient Black Drake", "npc_dota_neutral_black_drake"),
    "npc_dota_neutral_black_dragon" -> new DotaEntity("Ancient Black Dragon", "npc_dota_neutral_black_dragon"),
    "npc_dota_neutral_gnoll_assassin" -> new DotaEntity("Vhoul Assassin", "npc_dota_neutral_gnoll_assassin"),
    "npc_dota_neutral_ghost" -> new DotaEntity("Ghost", "npc_dota_neutral_ghost"),
    "npc_dota_neutral_dark_troll" -> new DotaEntity("Hill Troll", "npc_dota_neutral_dark_troll"),
    "npc_dota_neutral_dark_troll_warlord" -> new DotaEntity("Dark Troll Summoner", "npc_dota_neutral_dark_troll_warlord"),
    "npc_dota_neutral_forest_troll_berserker" -> new DotaEntity("Hill Troll Berserker", "npc_dota_neutral_forest_troll_berserker"),
    "npc_dota_neutral_forest_troll_high_priest" -> new DotaEntity("Hill Troll Priest", "npc_dota_neutral_forest_troll_high_priest"),
    "npc_dota_neutral_harpy_scout" -> new DotaEntity("Harpy Scout", "npc_dota_neutral_harpy_scout"),
    "npc_dota_neutral_harpy_storm" -> new DotaEntity("Harpy Stormcrafter", "npc_dota_neutral_harpy_storm")
  )

  def fromString(s: String): Option[DotaEntity] = { 
    this.innermap.get(s)
  }
}
