package info.pudgestats.core.replay

import info.pudgestats.core.json._

import com.google.gson.annotations.Expose

class KillEvent(
    _target: String, 
    _timestamp: Float)
  extends JsonSerializable
  with JsonDeserializable[KillEvent]
  with ReplayEvent
{
  def this() = this("", 0.0f)

  @Expose val target    = _target
  @Expose val timestamp = _timestamp
}
