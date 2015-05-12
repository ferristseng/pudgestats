package api.steam 

import scala.collection.immutable.ListMap
import scala.collection.mutable.MutableList

import org.scalatest.FunSuite

import api.common.data._

class Dota2SteamSpec 
  extends FunSuite
{
  val userClient = new SteamUserClient(System.getenv("PUDGE_STEAM_KEY"))
  
  test("GetPlayerSummaries should succeed with valid steamid") {
    assert(this.userClient.get(GetPlayerSummaries, 
      ListMap("steamids" -> "76561197962809243")).isLeft)
  }

  test("GetPlayerSummaries should succeed with invalid steamid") {
    this.userClient.get(GetPlayerSummaries, ListMap("steamids" -> "0")) match {
      case Left(l) => assert(l.size == 0)
      case _ => fail()
    }
  }

  test("GetPlayerSummaries should succeed with multiple steamids") {
    this.userClient.get(GetPlayerSummaries, 
      ListMap("steamids" -> "76561198026767796,76561197962809243")) match {
      case Left(l) => assert(l.size == 2)
      case _ => fail()
    }
  }

  test("GetPlayerSummaries should get valid users with same steamid") {
    this.userClient.get(GetPlayerSummaries, ListMap("steamids" -> "76561198026767796")) match {
      case Left(l) => assert(l.head.steamId == "76561198026767796")
      case _ => fail()
    }
  }
}
