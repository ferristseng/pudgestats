package info.pudgestats.web.model

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Transient

import info.pudgestats.core.replay.{RuneEvent => RuneEventBase}

private object RuneEvent {
  private val innermap = Map(
    "modifier_rune_doubledamage" -> "Double Damage",
    "modifier_rune_regen" -> "Regeneration",
    "modifier_rune_haste" -> "Haste",
    "modifier_rune_invis" -> "Invisibility",
    "modifier_illusion" -> "Illusion"
  )

  def compileRuneName(s: String) = this.innermap.get(s)
}

class RuneEvent(
    val matchId: Int,
    _rune: String,
    _timestamp: Float)
  extends RuneEventBase(
    _rune,
    _timestamp)
  with KeyedEntity[Int]
{
  val id = 0

  def this(rune: String, timestamp: Float) = this(0, rune, timestamp)

  @Transient
  lazy val runeCompiled = RuneEvent.compileRuneName(this.rune)

  override def toString = 
    s"<RuneEvent rune=$rune timestamp=$timestamp>"
}
