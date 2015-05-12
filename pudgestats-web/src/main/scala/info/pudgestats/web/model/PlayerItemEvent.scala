package info.pudgestats.web.model

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Transient

import com.dota.data.Item 

import info.pudgestats.core.replay.{
  PlayerItemEvent => PlayerItemEventBase}

class PlayerItemEvent(
    val matchId: Int,
    _name: String,
    _timestamp: Float)
  extends PlayerItemEventBase(
    _name,
    _timestamp,
    0)
  with KeyedEntity[Int]
{
  val id = 0

  def this(name: String, timestamp: Float) = this(0, name, timestamp)

  @Transient
  lazy val itemCompiled = Item.fromString(this.name)

  override def toString = 
    s"<PlayerItemEvent name=$name timestamp=$timestamp>"
}
