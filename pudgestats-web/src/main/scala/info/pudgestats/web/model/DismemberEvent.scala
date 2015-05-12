package info.pudgestats.web.model

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Transient

import info.pudgestats.web.util.NameCompiler
import info.pudgestats.core.replay.{DismemberEvent => DismemberEventBase}

class DismemberEvent(
    val matchId: Int,
    _target: String,
    _illusion: Boolean,
    _damage: Int,
    _timestamp: Float)
  extends DismemberEventBase(
    _target,
    _illusion,
    _damage,
    _timestamp)
  with KeyedEntity[Int] 
{
  val id = 0

  def this(target: String, illusion: Boolean, damage: Int, timestamp: Float) = 
    this(0, target, illusion, damage, timestamp)

  @Transient
  lazy val targetCompiled = NameCompiler.compileUnitEntityName(this.target)

  override def toString = 
    s"<DismemberEvent target=$target timestamp=$timestamp>"
}
