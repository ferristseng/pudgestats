package info.pudgestats.fetcher

import scala.collection.mutable.MutableList
import scala.collection.immutable.ListMap

import org.scalatest.FunSuite

import api.dota._
import api.dota.data._
import api.common.data._

class FilterSpec 
  extends FunSuite
{
  val client = new Dota2ApiClient(System.getenv("PUDGE_STEAM_KEY"))

  def testOnMatches(
    startSeqNum: Int, 
    requested: Int = 25)(
    desc: String)(  
    fun: (SteamApiDataSeq[_]) => Unit) = 
  {
    test(desc) {
      this.client.get(MatchHistoryBySequenceNum, 
        ListMap("matches_requested" -> requested.toString, 
                "start_at_match_seq_num" -> startSeqNum.toString)) match {
        case Left(l: SteamApiDataSeq[_]) => fun(l)
        case Right(_) => fail("Bad Response: Retest...")
        case _ => fail("Bug!")
      }
    }
  }

  def testOnMatchesWithFilter(
    startSeqNum: Int,
    requested: Int = 25)( 
    desc: String,
    filterer: Filter[Match])(
    fun: (MutableList[_]) => Unit) = 
  {
    testOnMatches(startSeqNum, requested)(desc) {
      case matches: SteamApiDataSeq[_] => {
        val filtered = matches.filter {
          case m: Match => filterer.filter(m)
          case _ => fail("Invalid type received...this is a bug")
        }
        fun(filtered)
      }
      case _ => fail("Invalid type received...this is a bug")
    }
  }

  def testOnSeqMatches        = testOnMatchesWithFilter(709720383)_
  def testOnSingMatch         = testOnMatchesWithFilter(709720383, 1)_
  def testOnAbandonMatch      = testOnMatchesWithFilter(729847709, 1)_
  def testOnValidAbandonMatch = testOnMatchesWithFilter(856507820, 1)_
  
  testOnSeqMatches("Player filter should properly filter out " + 
                   "matches with the specified hero", 
                   new PlayerMatchFilter(Set(14))) {
    case seq => assert(seq.size == 1)
  }

  testOnSeqMatches("Player filter should not filter out any " + 
                   "matches if given an improper hero id",
                   new PlayerMatchFilter(Set(-1))) {
    case seq => assert(seq.size == 0)
  }

  testOnSeqMatches("Region filter should filter out " +
                   "matches with the specified regions",
                   new RegionMatchFilter(Set(121, 122, 123, 124))) {
    case seq => assert(seq.size == 5)
  }

  testOnSeqMatches("Region filter should not filter out any " + 
                   "matches if given an improper region id",
                   new RegionMatchFilter(Set(9999))) {
    case seq => assert(seq.size == 0)
  }

  testOnSeqMatches("Mode filter should filter out " + 
                   "matches with the specified modes",
                   new ModeMatchFilter(Set(2, 16, 1))) {
    case seq => assert(seq.size == 21)
  }

  testOnSeqMatches("Mode filter should not filter out any " +
                   "matches if given an improper mode id",
                   new ModeMatchFilter(Set(-1, 9999))) {
    case seq => assert(seq.size == 0)
  }

  testOnSeqMatches("Lobby filter should filter out " +
                   "matches with the specified lobby types",
                   new LobbyMatchFilter(Set(7))) {
    case seq => assert(seq.size == 6)
  }

  testOnSeqMatches("Lobby filter should not filter out any " + 
                   "matches if given an improper lobby type id",
                   new LobbyMatchFilter(Set(-1))) {
    case seq => assert(seq.size == 0)
  }

  testOnAbandonMatch("Abandon filter should not filter out matches " + 
                     "with duration more than specified", 
                     new AbandonMatchFilter(15)) {
     case seq => assert(seq.size == 1)
  }

  testOnAbandonMatch("Abandon filter should filter out matches " + 
                     "with duration less than specifier",
                     new AbandonMatchFilter(60)) {
    case seq => assert(seq.size == 0)
  }

  testOnValidAbandonMatch("Player Num Filter should filter out matches " + 
                          "with valid abandoned matches", 
                          new PlayerNumFilter(10)) {
    case seq => assert(seq.size == 0)
  }

  testOnSeqMatches("Player Num Filter should not filter out valid matches",
                    new PlayerNumFilter(10)) {
    case seq => assert(seq.size == 22)
  }

  testOnSeqMatches("Player Num Filter should filter out all matches if " +
                   "given an input of 11",
                   new PlayerNumFilter(11)) {
    case seq => assert(seq.size == 0)
  }
}
