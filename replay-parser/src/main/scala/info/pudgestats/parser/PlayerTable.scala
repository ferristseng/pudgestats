package info.pudgestats.parser

import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap

import com.dota2.proto.Demo

import info.pudgestats.core.replay.Player

object PlayerTable {
  private val logger = LoggerFactory.getLogger("info.pudgestats.parser")
  
  def fromCDemoInfo(inf: Demo.CDemoFileInfo): Option[PlayerTable] = {
    if (!inf.hasGameInfo) return None
    if (!inf.getGameInfo.hasDota) return None
    
    val ginfo = inf.getGameInfo.getDota
    val table = new PlayerTable
    
    if (ginfo.getPlayerInfoCount != 10) {
      this.logger.warn("Player count was not 10...check match?")
      return None
    }
    
    ginfo.getPlayerInfoList.map {
      case p => {
        table += new Player(p.getHeroName, p.getPlayerName, 
                            p.getSteamid, p.getGameTeam)
      }
    }
    
    Some(table)
  }
}
  
/** Player Table
  *
  * Wraps the information available in the CDemoFileInfo
  * message.
  */
class PlayerTable private() {
  val players = new HashMap[String, Player]
  
  protected def +=(p: Player) = this.players += p.hero -> p

  def get(hero: String) = players.get(hero)

  override def toString = players.toString
}