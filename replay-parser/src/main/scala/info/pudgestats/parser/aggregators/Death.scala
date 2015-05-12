package info.pudgestats.parser

import scala.collection.mutable.HashSet

import info.pudgestats.core.replay.DeathEvent

class PudgeDeathAggregator
  extends Aggregator[ParsedMessage]
{
  val deathEvents = new HashSet[DeathEvent]

  /** This variable is used to determine 
    * whether or not a Pudge death was indeed a 
    * Pudge death. Using this variable should throw out 
    * deaths where Pudge has aegis and respawns.
    */
  private var lastLostGoldEvent: Option[CombatLogMessage] = None

  private def isValid(msg: CombatLogMessage) = 
    msg.kind        == CombatMessageType.Death &&
    msg.targetName  == Const.PudgeNPCString &&
    !msg.isTargetIllusion

  private def isGoldLostEvent(msg: CombatLogMessage) = 
    msg.kind        == CombatMessageType.Gold &&
    msg.targetName  == Const.PudgeNPCString &&
    msg.value < 0

  def apply(evt: ParsedMessage) = evt match {
    case msg: CombatLogMessage if this.isGoldLostEvent(msg) => 
      this.lastLostGoldEvent = Some(msg)
    case msg: CombatLogMessage if this.isValid(msg) =>
      this.lastLostGoldEvent match {
        case Some(evt) if (msg.timestamp - evt.timestamp < 2) => 
          this.deathEvents += new DeathEvent(msg.attackerName, 
                                             msg.isAttackerIllusion, 
                                             msg.timestamp)
        case _ => // Ignore...had aegis?
      }
    case _ =>
  }
}
