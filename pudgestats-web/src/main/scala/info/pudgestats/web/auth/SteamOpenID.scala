package info.pudgestats.web.auth

import java.net.URI

import org.openid4java.message._
import org.openid4java.OpenIDException
import org.openid4java.consumer.{ConsumerManager, ConsumerException}
import org.openid4java.discovery.{Identifier, DiscoveryInformation,
                                  DiscoveryException}

object SteamOpenIDClient {
  val IdentityKey = "openid.identity"
}

/** Steam Open ID Client
  *
  * Manages the generation of URLs to interact with the  
  * Steam Open ID service
  */
class SteamOpenIDClient { 
  private val openIdProvider = "http://steamcommunity.com/openid"
  private val openIdManager  = new ConsumerManager()

  private lazy val discoveredEndpoints = {
    this.openIdManager.setMaxAssocAttempts(0)
    this.openIdManager.associate(
      this.openIdManager.discover(this.openIdProvider))
  }

  def buildLoginUrl(callback: URI): String = 
    this.buildLoginUrl(callback.toString)

  /** Create the Open ID login URL with a callback URL endpoint string */
  def buildLoginUrl(callback: String): String = {
    val authReq = 
      this.openIdManager.authenticate(this.discoveredEndpoints, callback)

    authReq.getDestinationUrl(true)
  }
}
