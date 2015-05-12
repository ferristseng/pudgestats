package info.pudgestats.web.model

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Transient

import com.dota.data.Hero

import info.pudgestats.core.Const
import info.pudgestats.core.replay.{Player => PlayerBase}

import scala.language.implicitConversions

class Player(
    val matchId: Int,
    _hero: String,
    _name: String,
    _steamId: Long,
    _gameTeam: Int)
  extends PlayerBase(
    _hero,
    _name,
    _steamId,
    _gameTeam)
  with KeyedEntity[Int]
{
  val id = 0

  def this() = this(0, "", "", 0l, 0) 

  def this(hero: String, name: String, steamId: Long, gameTeam: Int) =
    this(0, hero, name, steamId, gameTeam)

  /** Compiles the hero from the string identifier */
  @Transient
  lazy val heroCompiled = Hero.fromString(this.hero)

  @Transient
  lazy val isPudge = this.hero == Const.PudgeNPCString
}

object PlayersStore extends Schema {
  val players = table[Player]
}

object PlayerConversions {
  import info.pudgestats.core.replay.{Player => CorePlayer}

  implicit def corePlayerToPlayer(evt: CorePlayer) = 
    new Player(evt.hero, evt.name, evt.steamId, evt.gameTeam)
}
