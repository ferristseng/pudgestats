package info.pudgestats.web.model

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Transient

import info.pudgestats.web.util.NameCompiler
import info.pudgestats.core.replay.{DeathEvent => DeathEventBase}

class DeathEvent(
    val matchId: Int,
    _attacker: String,
    _illusion: Boolean,
    _timestamp: Float)
  extends DeathEventBase(
    _attacker,
    _illusion,
    _timestamp)
  with KeyedEntity[Int]
{
  val id = 0

  def this(attacker: String, illusion: Boolean, timestamp: Float) = 
    this(0, attacker, illusion, timestamp)

  @Transient
  lazy val attackerCompiled = NameCompiler.compileUnitEntityName(this.attacker)

  override def toString = 
    s"<DeathEvent attacker=$attacker timestamp=$timestamp>"
}
