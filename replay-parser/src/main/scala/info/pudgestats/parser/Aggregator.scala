package info.pudgestats.parser

/** The main unit to consume a match
  *
  * Aggregates events that occur in a replay.
  * 
  * These should act like filters too, filtering 
  * out the messages the aggregator does not actually need.
  */
trait Aggregator[T] {
  def apply(evt: T)
}
