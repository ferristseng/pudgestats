package info.pudgestats.core.replay

import info.pudgestats.core.json._

import com.dota.helper.NameHelper

import com.google.gson.annotations.Expose

/** A hook event...can hit or not */
class HookEvent(
    _target: String, 
    _hit: Boolean, 
    _illusion: Boolean,
    _timestamp: Float) 
  extends JsonSerializable
  with JsonDeserializable[HookEvent]
  with ReplayEvent
{
  def this() = this("", false, false, 0.0f)

  @Expose val target    = _target
  @Expose val hit       = _hit
  @Expose val illusion  = _illusion
  @Expose val timestamp = _timestamp

  override def toString = 
    s"<HookEvent target=$target hit=$hit timestamp=$timestamp>"
}
