package info.pudgestats.web.model

import java.sql.Timestamp

import scala.language.implicitConversions

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, KeyedEntity}

import api.steam.data.{SteamUser => SteamUserBase}

import info.pudgestats.core.Const
import info.pudgestats.web.util.{ExtendedTable, HasDuplicate}

class SteamUser(
    steamId: String,
    name: String,
    profileUrl: String,
    avatar: String,
    avatarMedium: String,
    avatarFull: String,
    val timestamp: Timestamp = new Timestamp(System.currentTimeMillis))
  extends SteamUserBase(
    steamId,
    name,
    profileUrl,
    avatar,
    avatarMedium,
    avatarFull)
  with KeyedEntity[String]
  with HasDuplicate[SteamUser]
{
  val id = steamId

  lazy val pudgeMatches = 
    join(PlayersStore.players, MatchesStore.matches.rightOuter)((p, m) => 
      where(p.steamId === this.id.toLong and p.hero === Const.PudgeNPCString) 
      select(m)
      orderBy(m.get.timestamp.desc)
      on(m.map(_.id) === p.matchId))
    .filter(!_.isEmpty)
    .map(_.get)

  def dbEquiv(other: SteamUser) = this.id === other.id
}

/** Steam users store */
object SteamUsersStore extends Schema {
  val users = new ExtendedTable[String, SteamUser](table[SteamUser])
}

object SteamUserConversions {
  implicit def steamApiUserToSteamUser(user: SteamUserBase) = 
    new SteamUser(user.steamId, user.name, user.profileUrl, user.avatar,
      user.avatarMedium, user.avatarFull)
}
