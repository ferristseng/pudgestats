package info.pudgestats.web.model

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Transient

import info.pudgestats.web.util.NameCompiler
import info.pudgestats.core.replay.{KillEvent => KillEventBase}

class KillEvent(
    val matchId: Int,
    _target: String,
    _timestamp: Float)
  extends KillEventBase(
    _target,
    _timestamp)
  with KeyedEntity[Int]
{
  val id = 0

  def this(target: String, timestamp: Float) = this(0, target, timestamp)

  @Transient
  lazy val targetCompiled = NameCompiler.compileUnitEntityName(this.target)

  override def toString = 
    s"<KillEvent target=$target timestamp=$timestamp>"
}
