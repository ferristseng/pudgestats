package info.pudgestats.parser

import scala.collection.mutable.HashSet

import info.pudgestats.core.replay.HookEvent

/** Gets a list of hooks that pudge is involved in */
class PudgeHookAggregator
  extends Aggregator[ParsedMessage]
{
  val hookEvents = new HashSet[HookEvent]

  private def isValid(msg: CombatLogMessage) = 
    (msg.inflictorName == Const.PudgeHookString && 
     msg.kind == CombatMessageType.UsesSkill) ||
    (msg.inflictorName == Const.PudgeHookModifierString &&
     msg.kind == CombatMessageType.BuffDebuffApplied)
     
  def apply(evt: ParsedMessage) = evt match {
    case msg: CombatLogMessage if (this.isValid(msg)) => {
      this.hookEvents += new HookEvent(msg.targetName, 
                                       msg.kind == CombatMessageType.BuffDebuffApplied, 
                                       msg.isTargetIllusion,
                                       msg.timestamp)                               
    }
    case _ =>
  }
}
