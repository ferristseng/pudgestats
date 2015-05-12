package info.pudgestats.parser

import info.pudgestats.core.{Const => CoreConst} 

object Const 
{
  val PudgeNPCString                  = CoreConst.PudgeNPCString
  val PudgeDismemberModifierString    = "modifier_pudge_dismember"
  val PudgeHookModifierString         = "modifier_pudge_meat_hook"
  val PudgeDismemberString            = "pudge_dismember"
  val PudgeHookString                 = "pudge_meat_hook"
  val PudgeRotString                  = "pudge_rot"
  
  val WraithKingNPCString             = "npc_dota_hero_skeleton_king"

  val UnknownString                   = "dota_unknown"
  
  val CombatLogKey                    = "dota_combatlog"
  val CombatLogNamesKey               = "CombatLogNames"
  
  val ItemPropertyNames               = List(
                                          "m_Inventory.m_hItems.0000",
                                          "m_Inventory.m_hItems.0001",
                                          "m_Inventory.m_hItems.0002",
                                          "m_Inventory.m_hItems.0003",
                                          "m_Inventory.m_hItems.0004",
                                          "m_Inventory.m_hItems.0005",
                                          "m_Inventory.m_hItems.0006",
                                          "m_Inventory.m_hItems.0007",
                                          "m_Inventory.m_hItems.0008",
                                          "m_Inventory.m_hItems.0009",
                                          "m_Inventory.m_hItems.0010",
                                          "m_Inventory.m_hItems.0011",
                                          "m_Inventory.m_hItems.0012",
                                          "m_Inventory.m_hItems.0013")
  val AbilityPropertyNames            = List(
                                          "m_hAbilities.0000",
                                          "m_hAbilities.0001",
                                          "m_hAbilities.0002",
                                          "m_hAbilities.0003",
                                          "m_hAbilities.0004",
                                          "m_hAbilities.0005",
                                          "m_hAbilities.0006",
                                          "m_hAbilities.0007",
                                          "m_hAbilities.0008",
                                          "m_hAbilities.0009",
                                          "m_hAbilities.0010",
                                          "m_hAbilities.0011",
                                          "m_hAbilities.0012",
                                          "m_hAbilities.0013",
                                          "m_hAbilities.0014",
                                          "m_hAbilities.0015")
}
