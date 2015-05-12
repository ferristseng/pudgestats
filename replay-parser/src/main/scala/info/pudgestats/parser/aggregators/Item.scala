package info.pudgestats.parser

import scala.collection.mutable.HashSet

import info.pudgestats.core.replay.PlayerItemEvent

class PudgePurchaseAggregator
  extends Aggregator[ParsedMessage]
{
  val purchaseEvents = new HashSet[PlayerItemEvent]
  
  def apply(evt: ParsedMessage) = evt match {
    case msg: PlayerMessage => {
      for (itemEntity <- msg.inventory) {
        if (msg.playerEntity.getProperty[Int]("m_hReplicatingOtherHeroModel") ==
            2097151) // Essentially 2097151 is a null
        {
          this.purchaseEvents +=
            new PlayerItemEvent(
              itemEntity.getProperty[String]("m_iName"), 
              msg.timestamp,
              itemEntity.getHandle)
        }
      }
    }
    case _ =>
  }
}
