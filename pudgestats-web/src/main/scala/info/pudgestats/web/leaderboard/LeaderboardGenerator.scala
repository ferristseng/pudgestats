package info.pudgestats.web

import org.slf4j.{Logger, LoggerFactory}

import org.squeryl.PrimitiveTypeMode._

import scala.collection.immutable.ListMap

import api.steam.{SteamUserClient, GetPlayerSummaries}
import api.steam.data.SteamUser

import info.pudgestats.core.conf.SteamConfig
import info.pudgestats.web.model.{LeaderboardsStore, 
  Leaderboard, SteamUserConversions, SteamUsersStore, LeaderboardMember, 
  ScoreType, ConfigsStore}

/** Generates the leaderboard content 
  *
  * Process that generates a leaderboard content given 
  * the results of a very specialized query. The query may 
  * take awhile for larger data sets.
  */
class PudgeStatsLeaderboardGenerator(
    val conf: SteamConfig)
  extends Runnable
{
  import SteamUserConversions._

  private val client    = new SteamUserClient(conf.steamApiKey)
  protected val logger  = LoggerFactory.getLogger("info.pudgestats.web.leaderboard")

  def run = transaction {
    val siteConf = ConfigsStore.getConfig
    
    this.logger.trace(s"Starting new leaderboard with ${siteConf.leaderboardSize} users. " + 
                      s"Threshold games for this leaderboard: ${siteConf.leaderboardIncludeThresh}")

    val leadersList = 
      LeaderboardsStore.leaderboardByHookAccuracy(
        siteConf.leaderboardSize, 
        siteConf.leaderboardIncludeThresh)

    val steamIds = leadersList.map { 
      case group => group.key.toString 
    }

    this.logger.trace(s"Found new leaderboard users: $steamIds")

    this.getPlayerSummaries(steamIds) {
      case users => {
        val leaderboard = LeaderboardsStore.leaderboards.insert(
          new Leaderboard(ScoreType.HookEnemyAccuracy))

        users.foreach {
          case user => {
            val group = leadersList.find(_.key == user.id.toLong).get

            group.measures match {
              case Some(accuracy) => {
                val steamUser = SteamUsersStore.users.insertOrIgnore(user)

                leaderboard.userAssociations.associate(
                  new LeaderboardMember(steamUser.id, accuracy))
              }
              case None => this.logger.warn(s"No `accuracy` measures on: ${group.key}")
            }
          }
        }

        this.logger.info(s"Added a new Leaderboard: ${leaderboard}")
      }
    }
  }

  private def getPlayerSummaries(
    _steamIds: List[String])(
    callback: Iterable[SteamUser] => Any) 
  = {
    val steamIds = _steamIds.foldRight("")((acc, id) => id + "," + acc)

    this.client.get(GetPlayerSummaries, ListMap("steamIds" -> steamIds)) match {
      case Left(users) if users.size > 0 => callback(users) 
      case Right(ex) => 
        this.logger.warn(s"An error occured while fetching steam user info: $ex")
      case _ => // Ignore...no users found
    }
  }
}
