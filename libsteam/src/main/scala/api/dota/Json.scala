//
// Dota 2 Web API JSON handlers
// Uses Google's GSON library as an underlying 
// tool for parsing JSON.
//

package api.dota.json

import scala.collection.mutable.MutableList

import com.google.gson.stream.{JsonReader, JsonWriter, JsonToken}
import com.google.gson.{TypeAdapter, JsonParseException}

import api.dota.data._
import api.common.data._
import api.common.json._

//
// The TypeAdapter for a Draft (Pick or Ban)
//
class DraftTypeAdapter
  extends SteamGenericTypeAdapter[Draft] 
{
  var isPick    = false
  var heroId    = 0
  var teamId    = 0
  var order     = 0

  def handleName(name: String, reader: JsonReader) = name match {
    case "is_pick"  => { this.isPick = reader.nextBoolean }
    case "hero_id"  => { this.heroId = reader.nextInt }
    case "team"     => { this.teamId = reader.nextInt }
    case "order"    => { this.order = reader.nextInt }
  }
  def create = if (this.isPick) {
    Pick(this.heroId, this.teamId, this.order)
  } else {
    Ban(this.heroId, this.teamId, this.order)
  }
}

//
// The TypeAdapter for a Match
//
class MatchTypeAdapter 
  extends SteamGenericTypeAdapter[Match] 
{
  var radiantTeamVictory  = false
  var duration            = 0
  var startTime           = 0
  var matchId             = 0
  var seqNum              = 0.toLong
  var cluster             = 0
  var lobbyType           = 0
  var gameMode            = 0
  var players             = new SteamApiDataSeq[Player]
  var picksBans: Option[List[Draft]] = None

  def handleName(name: String, reader: JsonReader) = name match {
    case "duration"       => { this.duration = reader.nextInt }
    case "radiant_win"    => { this.radiantTeamVictory = reader.nextBoolean }
    case "start_time"     => { this.startTime = reader.nextInt }
    case "match_id"       => { this.matchId = reader.nextInt }
    case "match_seq_num"  => { this.seqNum = reader.nextLong }
    case "game_mode"      => { this.gameMode = reader.nextInt }
    case "cluster"        => { this.cluster = reader.nextInt }
    case "lobby_type"     => { this.lobbyType = reader.nextInt }
    case "players"        => {
      new SteamApiDataCollectionTypeAdapter(new PlayerTypeAdapter).read(reader) match {
        case l: SteamApiDataSeq[_] => this.players = l
        case _ => throw new MatchError((): Unit)
      }
    }
    case "picks_bans"   => {
      new SteamApiDataCollectionTypeAdapter(new DraftTypeAdapter).read(reader) match {
        case l: SteamApiDataSeq[_] => this.picksBans = Some(l.toList)
        case _ => throw new MatchError((): Unit)
      }
    }
    case _ => { reader.skipValue }
  }

  def create = new Match(radiantTeamVictory, duration, startTime, matchId, seqNum, 
                         cluster, lobbyType, gameMode, players.toList, picksBans)

  override def reset = { this.picksBans = None }
}

//
// The TypeAdapter for a Player
//
class PlayerTypeAdapter 
  extends SteamGenericTypeAdapter[Player] 
{
  var accountId     = 0.toLong
  var playerSlot    = 0
  var hero          = 0 
  var items         = MutableList[Int]() 
  var kills         = 0
  var deaths        = 0 
  var assists       = 0 
  var leaverStatus  = 0
  var gold          = 0 
  var lastHits      = 0 
  var denies        = 0
  var goldPerMinute = 0 
  var xpPerMinute   = 0 
  var goldSpent     = 0 
  var heroDamage    = 0 
  var towerDamage   = 0
  var heroHealing   = 0 
  var level         = 0 

  def handleName(name: String, reader: JsonReader) = name match {
    case "account_id"     => { this.accountId = reader.nextLong }
    case "player_slot"    => { this.playerSlot = reader.nextInt }
    case "hero_id"        => { this.hero = reader.nextInt }
    case "item_0"         => { this.items += reader.nextInt } 
    case "item_1" | 
         "item_2" | 
         "item_3" | 
         "item_4" | 
         "item_5"         => { this.items += reader.nextInt } 
    case "kills"          => { this.kills = reader.nextInt }
    case "deaths"         => { this.deaths = reader.nextInt }
    case "assists"        => { this.assists = reader.nextInt }
    case "leaver_status"  => { this.leaverStatus = reader.nextInt }
    case "gold"           => { this.gold = reader.nextInt }
    case "last_hits"      => { this.lastHits = reader.nextInt }
    case "denies"         => { this.denies = reader.nextInt }
    case "gold_per_min"   => { this.goldPerMinute = reader.nextInt }
    case "xp_per_min"     => { this.xpPerMinute = reader.nextInt }
    case "gold_spent"     => { this.goldSpent = reader.nextInt }
    case "hero_damage"    => { this.heroDamage = reader.nextInt }
    case "tower_damage"   => { this.towerDamage = reader.nextInt }
    case "hero_healing"   => { this.heroHealing = reader.nextInt }
    case "level"          => { this.level = reader.nextInt }
    case _                => { reader.skipValue }
  }

  def create = new Player(accountId, hero, playerSlot, 
                          items.filter(x => (x > 0)).toList, 
                          kills, deaths, assists, leaverStatus, gold,
                          lastHits, denies, goldPerMinute, xpPerMinute,
                          goldSpent, heroDamage, towerDamage, 
                          heroHealing, level)
}
