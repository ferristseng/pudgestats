package info.pudgestats.web.app

import org.squeryl.PrimitiveTypeMode._

import scala.collection.immutable.ListMap

import api.steam.{SteamUserClient, GetPlayerSummaries}

import info.pudgestats.core.conf.SteamConfig
import info.pudgestats.web.model.SteamUserConversions._
import info.pudgestats.web.model.{SteamUser, SteamUsersStore}

trait UserController
{
  self: DefaultStack =>

  val conf: SteamConfig

  private val steamUserClient = new SteamUserClient(this.conf.steamApiKey)

  val userShowRoute = get("/u/:id") {
    contentType = "text/html"

    val id = params.getOrElse("id", halt(404))

    val user: Option[SteamUser] = SteamUsersStore.users.lookup(id) match {
      case None => 
        this.steamUserClient.get(
          GetPlayerSummaries, 
          ListMap("steamids" -> id)) 
        match {
          case Left(users) if users.size > 0 => 
            Some(SteamUsersStore.users.insertOrIgnore(users.head))
          case Left(_) => halt(404)
          case _ => None
        }
      case result => result
    }

    ssp("/users/show", "steamUser" -> user, "steamId" -> id)
  }
}
