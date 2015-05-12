package info.pudgestats.web.model

import java.sql.Timestamp

import scala.collection.immutable.ListMap

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.annotations.Transient

import info.pudgestats.web.util.{ExtendedTable, HasDuplicate}

object SteamUserSession {
  /** Get the SteamID from the OpenID service URL response 
    *
    * The Steam Web API FAQ describes that 
    * the claimed ID returned by the 
    * OpenID service will contain the 
    * 64-bit Steam user ID in the form:
    * 'http://steamcommunity.com/openid/id/<steamid>'
    */
  def fromClaimedId(url: String): SteamUserSession = {
    val id = url.filter((c) => c.isDigit)

    new SteamUserSession(id)
  }
}

/** A user session backed by Steam
  *
  * The ID corresponds to a real Steam User ID
  */
class SteamUserSession(
    val id: String,
    val timestamp: Timestamp = new Timestamp(System.currentTimeMillis))
  extends KeyedEntity[String] 
  with HasDuplicate[SteamUserSession]
{
  def dbEquiv(other: SteamUserSession) = this.id === other.id

  @Transient
  lazy val steamUser = SteamUsersStore.users.lookup(this.id)

  @Transient
  lazy val name = this.steamUser match {
    case Some(steamUser) => steamUser.name
    case None => this.id
  }

  override def toString = 
    s"<SteamUserSession id=${String.valueOf(this.id)}>"
}

/** Session store */
object SessionsStore extends Schema {
  val sessions = new ExtendedTable[String, SteamUserSession](table[SteamUserSession])
}
