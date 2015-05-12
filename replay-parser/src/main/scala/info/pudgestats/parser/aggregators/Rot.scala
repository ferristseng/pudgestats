package info.pudgestats.parser

import com.dota.helper.NameHelper

import scala.collection.mutable.HashSet

/** Aggregates damage dealt by Pudge's Rot skill */
class PudgeRotAggregator
  extends Aggregator[ParsedMessage]
{
  private var _selfRotDamageDealt    = 0
  private var _creepRotDamageDealt   = 0
  private var _neutralRotDamageDealt = 0
  private var _heroRotDamageDealt    = 0
  
  def selfRotDamageDealt    = this._selfRotDamageDealt
  def creepRotDamageDealt   = this._creepRotDamageDealt
  def neutralRotDamageDealt = this._neutralRotDamageDealt
  def heroRotDamageDealt    = this._heroRotDamageDealt
  
  private object MessageTypeHelper {
    def isValidMsg(msg: CombatLogMessage) =
      msg.kind          == CombatMessageType.Damage && 
      msg.inflictorName == Const.PudgeRotString &&
      msg.attackerName  == Const.PudgeNPCString &&
      msg.sourceName    == Const.PudgeNPCString &&
      !(msg.isAttackerIllusion || msg.isTargetIllusion)
    
    def isSelfDamage(msg: CombatLogMessage) = 
      msg.attacker == msg.target
      
    def isCreepDamage(msg: CombatLogMessage) = 
      NameHelper.isCreep(msg.targetName)
      
    def isNeutralDamage(msg: CombatLogMessage) = 
      NameHelper.isNeutral(msg.targetName)

    def isHeroDamage(msg: CombatLogMessage) = 
      NameHelper.isHero(msg.targetName)
  }

  /** This method should produce correct values...Valve reports them damage 
    * weirdly on the Web API
    */
  def apply(evt: ParsedMessage) = evt match { 
    case msg: CombatLogMessage if (MessageTypeHelper.isValidMsg(msg)) => {
      if (MessageTypeHelper.isSelfDamage(msg)) {
        this._selfRotDamageDealt += msg.value
      } else if (MessageTypeHelper.isCreepDamage(msg)) {
        this._creepRotDamageDealt += msg.value
      } else if (MessageTypeHelper.isNeutralDamage(msg)) {
        this._neutralRotDamageDealt += msg.value
      } else if (MessageTypeHelper.isHeroDamage(msg)) {
        this._heroRotDamageDealt += msg.value
      } else { /* Buildings, player controlled units, illusions */ }
    }
    case _ =>
  }
}