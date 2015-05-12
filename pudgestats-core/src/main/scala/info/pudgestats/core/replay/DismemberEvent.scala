package info.pudgestats.core.replay

import info.pudgestats.core.json._

import com.google.gson.annotations.Expose

/** Dismember Events */
class DismemberEvent(
    _target: String, 
    _illusion: Boolean, 
    _damage: Int,
    _timestamp: Float) 
  extends JsonSerializable
  with JsonDeserializable[DismemberEvent]
  with ReplayEvent
{
  def this() = this("", false, 0, 0.0f)

  @Expose val target    = _target
  @Expose val illusion  = _illusion
  @Expose val damage    = _damage
  @Expose val timestamp = _timestamp

  override def compare(that: ReplayEvent) = 
    if (that.timestamp == this.timestamp && 
        that.isInstanceOf[DismemberEvent]) {
      this.damage.compare(that.asInstanceOf[DismemberEvent].damage)
    } else {
      this.timestamp.compare(that.timestamp)
    }
}
