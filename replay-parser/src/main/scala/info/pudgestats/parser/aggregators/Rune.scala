package info.pudgestats.parser

import scala.collection.mutable.HashSet

import com.dota.helper.NameHelper

import info.pudgestats.core.replay.RuneEvent

/** Rune Pickup Aggregator */
class PudgeRunePickupAggregator
  extends Aggregator[ParsedMessage]
{
  val runeEvents = new HashSet[RuneEvent]

  private def isValid(msg: CombatLogMessage) = 
    msg.kind        == CombatMessageType.BuffDebuffApplied &&
    msg.sourceName  == Const.UnknownString &&
    msg.targetName  == Const.PudgeNPCString &&
    NameHelper.isRune(msg.inflictorName) && !msg.isAttackerIllusion

  def apply(evt: ParsedMessage) = evt match {
    case msg: CombatLogMessage if this.isValid(msg) => {
      this.runeEvents += new RuneEvent(msg.inflictorName, msg.timestamp)
    }
    case _ =>
  }
}