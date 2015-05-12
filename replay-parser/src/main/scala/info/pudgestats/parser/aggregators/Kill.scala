package info.pudgestats.parser

import com.dota.helper.NameHelper

import info.pudgestats.core.replay.KillEvent

import scala.collection.mutable.MutableList

// TODO: Account for aegis / WK ultimate
/** Gets a list of kills that pudge is involved in */
class PudgeKillAggregator
  extends Aggregator[ParsedMessage] 
{ 
  val killEvents = new MutableList[KillEvent]

  /** This variable is used to determine when / if a 
    * hero actually dies or not.
    */
  private var lastHeroDeath: Option[CombatLogMessage] = None
  
  private def isValid(msg: CombatLogMessage) = 
    msg.attackerName == Const.PudgeNPCString && 
    msg.kind         == CombatMessageType.Death &&
    msg.targetName   != Const.PudgeNPCString && 
    !(msg.isAttackerIllusion || msg.isTargetIllusion) &&
    NameHelper.isHero(msg.targetName)

  private def isHeroDeath(msg: CombatLogMessage) = 
    msg.targetName != Const.PudgeNPCString &&
    msg.kind == CombatMessageType.Gold &&
    msg.value < 0

  def apply(evt: ParsedMessage) = evt match {
    case msg: CombatLogMessage if (this.isHeroDeath(msg)) =>
      this.lastHeroDeath = Some(msg)
    case msg: CombatLogMessage if (this.isValid(msg)) =>
      this.lastHeroDeath match {
        case Some(dth) if (msg.timestamp - dth.timestamp < 2) =>
          this.killEvents += new KillEvent(msg.targetName, msg.timestamp)
        case _ => // Not a valid kill
      }
    case _ =>
  }
}
