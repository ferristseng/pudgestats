package info.pudgestats.core.replay

import info.pudgestats.core.json._

import com.google.gson.annotations.Expose

/** Rune Events 
  *
  * The equals method assumes that there should never be a case
  * where two runes of differing type are activated 
  * at the exact same time
  */
class RuneEvent(
    _rune: String,
    _timestamp: Float)
  extends JsonSerializable
  with JsonDeserializable[RuneEvent]
  with ReplayEvent
{
  def this() = this("", 0.0f)
  
  @Expose val rune      = _rune
  @Expose val timestamp = _timestamp

  override def equals(other: Any) = 
    other.isInstanceOf[RuneEvent] && 
    other.asInstanceOf[RuneEvent].timestamp == this.timestamp

  override def hashCode = this.timestamp.hashCode
}
