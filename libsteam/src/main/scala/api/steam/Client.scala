package api.steam

import scala.collection.immutable.ListMap

import api.steam.data._
import api.steam.json._
import api.common.data._
import api.common.json._
import api.common.client._

//
// Steam User API Acessible Resources
//
protected sealed trait SteamUserApiResource
protected case class GetPlayerSummariesResource()
  extends SteamUserApiResource

object GetPlayerSummaries extends GetPlayerSummariesResource

//
// Fetches user account information for a Steam (Dota 2)
// user.
//
class SteamUserClient(val key: String) 
  extends SteamApiClient[SteamUserApiResource] 
  with RequiresHttpClient 
{
    private val _API_PATH     = "ISteamUser"
    private val _API_VERSION  = "V00002"
    protected val resultKey   = "response"

    def get(
      resource: GetPlayerSummariesResource, 
      params: ListMap[String, String])
    : Either[SteamApiDataSeq[SteamUser], Exception] = {
      val _params = params + ("key" -> key)
      this.getResource("GetPlayerSummaries", _params,
        new SteamApiInnerCollectionTypeAdapter("players",
          new SteamApiDataCollectionTypeAdapter(new SteamUserTypeAdapter())))
    }

    protected def buildUrlPath(endpoint: String) = 
      this.base.setPath(s"/${this._API_PATH}/$endpoint/${this._API_VERSION}")
}
