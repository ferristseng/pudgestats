package info.pudgestats.core.replay

import org.joda.time.Duration
import org.joda.time.format.{PeriodFormatterBuilder, PeriodFormatter}

/** This formatter is taken from skadistats, clarity */
private object ReplayEvent {
  private val DurationFormatter = 
    (new PeriodFormatterBuilder)
      .minimumPrintedDigits(2)
      .printZeroAlways()
      .appendHours()
      .appendLiteral(":")
      .appendMinutes()
      .appendLiteral(":")
      .appendSeconds()
      .appendLiteral(".")
      .appendMillis3Digit()
      .toFormatter();
}

trait ReplayEvent
  extends Ordered[ReplayEvent]
{
  val timestamp: Float
  
  def timestampString = 
    ReplayEvent.DurationFormatter.print(
      Duration.millis((1000 * this.timestamp).toInt).toPeriod)

  def timestampWithOffset(offset: Float) = this.timestamp - offset

  def timestampWithOffsetString(offset: Float) = 
    ReplayEvent.DurationFormatter.print(
      Duration.millis((1000 * this.timestampWithOffset(offset)).toInt).toPeriod)

  def compare(that: ReplayEvent) = this.timestamp.compare(that.timestamp)
}
