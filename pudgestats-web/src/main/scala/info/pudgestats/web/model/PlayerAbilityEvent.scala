package info.pudgestats.web.model

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Transient

import info.pudgestats.core.replay.{
  PlayerAbilityEvent => PlayerAbilityEventBase}

import com.dota.data.Ability

class PlayerAbilityEvent(
    val matchId: Int,
    _name: String,
    _level: Int,
    _timestamp: Float)
  extends PlayerAbilityEventBase(
    _name,
    _level,
    _timestamp,
    0)
  with KeyedEntity[Int]
{
  val id = 0

  def this(name: String, level: Int, timestamp: Float) = 
    this(0, name, level, timestamp)

  /** Compiled ability from ability name identifier */
  @Transient
  lazy val abilityCompiled = Ability.fromString(this.name)

  override def toString = 
    s"<PlayerAbilityEvent name=$name level=$level timestamp=$timestamp>"
}
