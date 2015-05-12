package info.pudgestats.web.model

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Transient

import info.pudgestats.web.util.NameCompiler
import info.pudgestats.core.replay.{HookEvent => HookEventBase}

class HookEvent(
    val matchId: Int,
    _target: String,
    _hit: Boolean,
    _illusion: Boolean,
    _timestamp: Float)
  extends HookEventBase(
    _target,
    _hit,
    _illusion,
    _timestamp)
  with KeyedEntity[Int]
{
  val id = 0

  def this(target: String, hit: Boolean, illusion: Boolean, timestamp: Float) = 
    this(0, target, hit, illusion, timestamp)

  @Transient
  lazy val targetCompiled = NameCompiler.compileUnitEntityName(this.target)

  override def toString = 
    s"<HookEvent target=$target hit=$hit illusion=$illusion timestamp=$timestamp>"
}
