package info.pudgestats.core.replay

import info.pudgestats.core.json._

import com.google.gson.annotations.Expose

class PlayerItemEvent(
    _name: String,
    _timestamp: Float,
    val entityHandle: Int)
  extends JsonSerializable
  with JsonDeserializable[PlayerItemEvent]
  with ReplayEvent
{
  def this() = this("", 0.0f, 0)

  @Expose val name      = _name
  @Expose val timestamp = _timestamp
  
  override def equals(other: Any) =
    other.isInstanceOf[PlayerItemEvent] && 
    other.asInstanceOf[PlayerItemEvent].entityHandle == this.entityHandle
  
  override def hashCode = this.entityHandle.hashCode
  
  override def toString = 
    s"<PlayerItemEvent name=$name>"
}
