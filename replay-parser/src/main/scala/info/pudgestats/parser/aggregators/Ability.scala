package info.pudgestats.parser

import scala.collection.mutable.HashSet

import info.pudgestats.core.replay.PlayerAbilityEvent

class PudgeAbilityAggregator
  extends Aggregator[ParsedMessage]
{
  val abilityEvents = new HashSet[PlayerAbilityEvent]

  def apply(evt: ParsedMessage) = evt match {
    case msg: PlayerMessage => {
      for (abilityEntity <- msg.abilities) {
        this.abilityEvents += new PlayerAbilityEvent(
          abilityEntity.getProperty[String]("m_iName"), 
          abilityEntity.getProperty[Int]("m_iLevel"),
          msg.timestamp,
          abilityEntity.getHandle)
      }
    }
    case _ =>
  }
}
