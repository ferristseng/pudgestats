package info.pudgestats.core.replay

import info.pudgestats.core.json._

import com.google.gson.annotations.Expose

class DeathEvent(
    _attacker: String,
    _illusion: Boolean,
    _timestamp: Float)
  extends JsonSerializable
  with JsonDeserializable[DeathEvent]
  with ReplayEvent
{
  def this() = this("", false, 0.0f)

  @Expose val attacker  = _attacker
  @Expose val illusion  = _illusion
  @Expose val timestamp = _timestamp
}
