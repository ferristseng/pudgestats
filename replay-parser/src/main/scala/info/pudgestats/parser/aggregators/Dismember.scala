package info.pudgestats.parser

import info.pudgestats.core.replay.DismemberEvent

import scala.collection.mutable.MutableList

/** Dismember Aggregator */
class PudgeDismemberAggregator 
  extends Aggregator[ParsedMessage] 
{
  val dismemberEvents = new MutableList[DismemberEvent]

  private def isValid(msg: CombatLogMessage) = 
    (msg.kind          == CombatMessageType.BuffDebuffApplied &&
     msg.inflictorName == Const.PudgeDismemberModifierString) ||
    (msg.kind          == CombatMessageType.Damage &&
     msg.inflictorName == Const.PudgeDismemberString)

  def apply(evt: ParsedMessage) = evt match {
    case msg: CombatLogMessage if this.isValid(msg) => {
      this.dismemberEvents += 
        new DismemberEvent(msg.targetName,
                           msg.isTargetIllusion,
                           if (msg.kind == CombatMessageType.Damage) {
                             msg.value
                           } else { -1 },
                           msg.timestamp)
    }
    case _ =>
  }
}
