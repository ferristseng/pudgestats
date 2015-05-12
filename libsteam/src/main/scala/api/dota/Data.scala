//
// Representation of data returned by 
// the Dota 2 Web API
//

package api.dota.data

import scala.collection.immutable.List

import api.common.data._

//
// Dota 2 Picks and Bans 
//
sealed trait Draft extends SteamApiData {
  val hero: Int
  val team: Int
  val order: Int
}

case class Pick(
    val hero: Int, 
    val team: Int, 
    val order: Int) 
  extends Draft

case class Ban(
    val hero: Int,
    val team: Int,
    val order: Int)
  extends Draft

//
// Different forms of data that the 
// Dota 2 Web API returns
// Player | Match
//
case class Player(
    val accountId:      Long,
    val hero:           Int, 
    val playerSlot:     Int,
    val items:          List[Int], 
    val kills:          Int, 
    val deaths:         Int,
    val assists:        Int, 
    val leaverStatus:   Int,
    val gold:           Int, 
    val lastHits:       Int, 
    val denies:         Int,
    val goldPerMinute:  Int,
    val xpPerMinute:    Int, 
    val goldSpent:      Int, 
    val heroDamage:     Int, 
    val towerDamage:    Int, 
    val heroHealing:    Int, 
    val level:          Int)
  extends SteamApiData {
  val isDire        = if ((playerSlot & 0x80) == 0x80) true else false
  val isRadiant     = !isDire

  override def toString = s"Player::(${this.hero})"
}

case class Match(
    val radiantTeamVictory:   Boolean, 
    val duration:             Int, 
    val startTime:            Int, 
    val matchId:              Int,
    val seqNum:               Long,
    val cluster:              Int,
    val lobbyType:            Int,
    val gameMode:             Int,
    val players:              List[Player],
    val picksBans:            Option[List[Draft]]) 
  extends SteamApiData {

  override def toString   = s"Match::(${this.matchId})"
}
