package api.dota 

import scala.collection.immutable.ListMap
import scala.collection.mutable.MutableList

import org.scalatest.FunSuite

import api.dota.data._

class Dota2ApiSpec 
  extends FunSuite
{
  val dotaClient = new Dota2ApiClient(System.getenv("PUDGE_STEAM_KEY"))

  test("Dota2Client should successfully get valid MatchDetails") {
    assert(this.dotaClient.get(
      MatchDetails, ListMap("match_id" -> "674645840")).isLeft)
  }

  test("Dota2Client setting params on GetMatchHistoryBySequenceNum should work") {
    this.dotaClient.get(MatchHistoryBySequenceNum, ListMap("matches_requested" -> "5")) match {
      case Left(l) => {
        assert(l.size == 5)
      }
      case _ => fail("Bad Response")
    }
  }

  test("Dota2Client should succesfully get valid GetMatchHistoryBySequenceNum") {
    assert(this.dotaClient.get(
      MatchHistoryBySequenceNum, ListMap[String, String]()).isLeft)
  }
}
