package info.pudgestats.web.app

import scala.collection.immutable.ListMap

import org.scalatra.UrlGeneratorSupport

import org.apache.http.client.utils.URIBuilder

import api.steam.{SteamUserClient, GetPlayerSummaries}

import info.pudgestats.core.conf.SteamConfig
import info.pudgestats.web.auth.SteamOpenIDClient
import info.pudgestats.web.model.SteamUsersStore
import info.pudgestats.web.model.SteamUserConversions._

trait SessionController 
  extends UrlGeneratorSupport
{
  self: DefaultStack =>

  val conf: SteamConfig

  private val steamUserClient = new SteamUserClient(this.conf.steamApiKey)
  private val steamOpenIdClient = new SteamOpenIDClient

  val loginRoute = get("/s/login") {
    if (isAuthenticated) redirect("/")

    val steamUrl = 
      new URIBuilder()
        .setScheme("http")
        .setHost(request.serverName)
        .setPath(url(authenticateRoute))
        .setPort(request.serverPort.toInt)
        .build()

    redirect(steamOpenIdClient.buildLoginUrl(steamUrl))
  }

  val logoutRoute = get("/s/logout") {
    if (!isAuthenticated) redirect("/")

    scentry.logout

    redirect("/")
  }

  val authenticateRoute = get("/s/authenticate") {
    if (isAuthenticated) redirect("/")

    scentry.authenticate()

    this.steamUserClient.get(
      GetPlayerSummaries, 
      ListMap("steamids" -> this.userOption.get.id)) 
    match {
      case Left(users) if users.size > 0 => 
        SteamUsersStore.users.insertOrIgnore(users.head)
      case _ => 
    }

    redirect("/")
  }
}
