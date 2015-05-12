package info.pudgestats.fetcher

import org.slf4j.LoggerFactory

import scala.collection.immutable.ListMap

import api.dota._
import api.dota.data._
import api.common.data._

/** A Match Finder 
  *
  * Queries the Steam API for a list of matches, and filters them.
  *
  * @param apiKey - Steam API Key
  * @param matchesRequested - number of matches to request per query
  * @param filters - the filters to filter matches by
  */
class FilteredMatchFinder(
  private val apiKey: String,
  private val matchesRequested: Int = 25,
  private val filters: List[Filter[Match]] = List(),
  startSeqNum: Option[Long] = None) 
{
  private val logger = LoggerFactory.getLogger("info.pudgestats.fetcher")
  private val client = new Dota2ApiClient(apiKey)
  protected var currentSeqNum = startSeqNum match {
    case Some(num) => num
    case None => {
      this.logger.trace("No match given to start from...querying the Web API " + 
                        "for a suitable place to start")
      this.client.get(MatchHistory, ListMap[String, String]()) match {
        case Left(matches) => {
          matches.lastOption match {
            case Some(m) => m.seqNum
            case _ => this.logger.warn("No matches returned by API") 
          }
        }
        case Right(ex) => 
          this.logger.warn(s"Received an error while retrieving matches: ${ex}")
      }
    }
  }

  /** Query the Web API for matches 
    *
    * Queries the Web API at a specified interval, trying to fetch matches
    * based on sequence number. Updates the current sequence number to 
    * the last match that was read.
    * 
    * Returns all matches that pass the filtering
    */
  def queryMatches: List[ValidMatch] = {
    this.logger.trace(s"Querying for matches starting with seqNum='${this.currentSeqNum}'")

    this.client.get(MatchHistoryBySequenceNum, 
      ListMap("matches_requested" -> matchesRequested.toString,
              "start_at_match_seq_num" -> currentSeqNum.toString)) match {
      case Left(matches) => {
        val filtered = matches.filter { 
          case m => this.filter(m)
        }.map {
          case m => new ValidMatch(m.matchId, m.seqNum, m.cluster, None, Status.Unknown)
        }.toList

        // Update the sequence number
        this.currentSeqNum = matches.lastOption match {
          case Some(m) => m.seqNum
          case _ => {
            this.logger.warn("Received no matches...couldn't set new seq num")
            this.currentSeqNum
          }
        }

        filtered
      }
      case Right(ex) => {
        this.logger.warn(s"Received an error while retrieving matches: ${ex}")
        List()
      }
    }
  }

  /** Filter matches
    *
    * Run through each internal filter.
    */
  protected def filter(m: Match): Boolean = 
    this.filters.foldRight(true)((f, acc) => acc && f.filter(m))
}
