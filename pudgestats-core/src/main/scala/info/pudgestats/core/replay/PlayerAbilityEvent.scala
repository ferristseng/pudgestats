package info.pudgestats.core.replay

import info.pudgestats.core.json._

import com.google.gson.annotations.Expose

class PlayerAbilityEvent(
    _name: String,
    _level: Int,
    _timestamp: Float,
    val entityHandle: Int)
  extends JsonSerializable
  with JsonDeserializable[PlayerAbilityEvent]
  with ReplayEvent
{
  def this() = this("", 0, 0.0f, 0)

  @Expose val name      = _name
  @Expose val level     = _level
  @Expose val timestamp = _timestamp

  override def equals(other: Any) = 
    other.isInstanceOf[PlayerAbilityEvent] &&
    other.asInstanceOf[PlayerAbilityEvent].level == this.level &&
    other.asInstanceOf[PlayerAbilityEvent].name == this.name

  override def hashCode = (this.name, this.level).hashCode
    
  override def toString = 
    s"<PlayerAbilityEvent name=$name level=$level>"
}
