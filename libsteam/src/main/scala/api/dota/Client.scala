package api.dota

import scala.collection.immutable.ListMap

import api.dota.data._
import api.dota.json._
import api.common.data._
import api.common.json._
import api.common.client._

//
// Dota 2 API Accessible Web Resources
//
protected sealed trait Dota2ApiResource
protected case class MatchHistoryBySequenceNumResource() 
  extends Dota2ApiResource
protected case class MatchDetailsResource() 
  extends Dota2ApiResource
protected case class MatchHistoryResource() 
  extends Dota2ApiResource

// Objects accessible to the user
object MatchHistoryBySequenceNum extends MatchHistoryBySequenceNumResource 
object MatchDetails extends MatchDetailsResource
object MatchHistory extends MatchHistoryResource 

//
// Dota 2 API Client
//
// Interface for interacting with the Dota 2 Web API
// Limited to a few resources that need to be used, but
// should be easily extensible to cover everything that 
// the Dota 2 Web API provides.
//
// Example: ```
//    def main(args: Array[String]) {
//      val client = new Dota2ApiClient("xxx")
//      client.get(MatchHistoryBySequenceNum, ListMap("start_at_match_seq_num" -> "291090933",
//                                                    "matches_requested" -> "150")) match {
//        case Left(l) => for (m <- l) { 
//            println("Match:: " + m.matchId)
//            for (player <- m.players) {
//              println(player.hero + ": " + player.items)
//            }
//            case _ => { }
//          }
//        }
//        case Right(ex) => println(ex) 
//      }
//      client.get(MatchDetails, ListMap("match_id" -> "674645840")) match {
//        case Left(u) => println("good " + u.matchId)
//        case Right(ex) => println("bad " + ex)
//    }
// ```
//
class Dota2ApiClient(val key: String)
  extends SteamApiClient[Dota2ApiResource] 
  with RequiresHttpClient 
{
  private val _API_PATH     = "IDOTA2Match_570"
  private val _API_VERSION  = "V001" 
  protected val resultKey   = "result"

  //
  // Gets a resource given a concrete Dota2ApiResource (which corresponds directly 
  // to a route available in the Dota 2 Web API).
  //
  // Example: ```
  //  val client = new Dota2ApiClient(key)
  //  val matches = client.get(MatchHistoryBySequenceNum, ListMap("matches_requested" -> "5"))
  //  matches match {
  //    case Left(s) => { ... }
  //    case _ => { }
  //  }
  //  ```
  //
  def get(
    resource: MatchDetailsResource, 
    params: ListMap[String, String])
  : Either[Match, Exception] = {
    val _params = params + ("key" -> key)
    this.getResource("GetMatchDetails", _params, 
      new MatchTypeAdapter()) 
  }

  def get(
    resource: MatchHistoryBySequenceNumResource,
    params: ListMap[String, String])
  : Either[SteamApiDataSeq[Match], Exception] = {
    val _params = params + ("key" -> key)
    this.getResource("GetMatchHistoryBySequenceNum", _params, 
      new SteamApiInnerCollectionTypeAdapter("matches", 
        new SteamApiDataCollectionTypeAdapter(new MatchTypeAdapter))) 
  }

  def get(
    resource: MatchHistoryResource,
    params: ListMap[String, String])
  : Either[SteamApiDataSeq[Match], Exception] = {
    val _params = params + ("key" -> key)
    this.getResource("GetMatchHistory", _params,
      new SteamApiInnerCollectionTypeAdapter("matches",
        new SteamApiDataCollectionTypeAdapter(new MatchTypeAdapter)))
  }

  protected def buildUrlPath(endpoint: String) =
    this.base.setPath(s"/${this._API_PATH}/$endpoint/${this._API_VERSION}")
}
