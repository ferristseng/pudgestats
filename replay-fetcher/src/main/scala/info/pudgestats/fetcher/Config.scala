package info.pudgestats.fetcher

import com.typesafe.config.ConfigFactory

import java.io.File

import scala.collection.JavaConversions._

import info.pudgestats.core.conf.{TypesafeConfigWrapper, 
  RabbitConfig, SteamConfig}

/** Configuration class 
  *
  * Parses a configuration file at a specific file path
  * throwing a ParseException if not found or if 
  * a mandatory config option is not specified.
  */
class Config(path: String)
  extends TypesafeConfigWrapper
  with SteamConfig
  with RabbitConfig
{
  val conf                = ConfigFactory.parseFile(new File(path))
  
  private val _steamUsers = conf.getObjectList("steam_users")

  val steamTimeout        = conf.getInt("steam_timeout")
  val steamRetry          = conf.getInt("steam_retry")
  val steamReconnect      = conf.getInt("steam_reconnect")

  val batchSize           = conf.getInt("batch_size")
  val downloadInterval    = conf.getInt("download_interval")
  val parseQueue          = conf.getString("parse_queue")

  val dotaRegions         = conf.getIntList("dota_regions") 
  val dotaModes           = conf.getIntList("dota_modes")
  val dotaLobbies         = conf.getIntList("dota_lobby")
  val dotaHeroes          = conf.getIntList("dota_heroes")
  val dotaAbandonThresh   = conf.getInt("dota_abandon_thresh")

  val webapiResultNum     = conf.getInt("webapi_result_num")
  val webapiInterval      = conf.getInt("webapi_interval")

  val replayOutput        = conf.getString("replay_output")
  val replayPrefix        = conf.getString("replay_prefix")

  val steamUsers          = this._steamUsers.map { 
                              case o => (o.toConfig.getString("name"), 
                                         o.toConfig.getString("pass"), 
                                         o.toConfig.getString("handle")) 
                            }
}
