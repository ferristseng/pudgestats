package info.pudgestats.parser

import org.slf4j.LoggerFactory

import java.io.IOException
import java.nio.file.{Paths, Files}

import scala.collection.JavaConversions._
import scala.collection.mutable.MutableList

import skadistats.clarity.Clarity
import skadistats.clarity.parser.{Profile => ClarityProfile}
import skadistats.clarity.model.{GameEvent => ClarityGameEvent, 
  StringTable, GameEventDescriptor}
import skadistats.clarity.`match`.Match

/** Some info about the Parser version
  *
  * Major versions of the Parser should have 
  * new tagged names
  */
object Parser {
  val Version     = 1.2f
  val VersionName = "THERE_ARE_MANY_GABES_BUT_ONLY_ONE_GABEN" 
}

/** Provide a wrapper for a GameEvent in Clarity */ 
protected class GameEvent(
  private val evt: ClarityGameEvent,
  private val desc: GameEventDescriptor)
{
  private def ->(k: String) = this.desc.getIndexForKey(k)
  def getInt(k: String)     = this.evt.getProperty[Int](this -> k)
  def getBool(k: String)    = this.evt.getProperty[Boolean](this -> k)
  def getString(k: String)  = this.evt.getProperty[String](this -> k)
  def getFloat(k: String)   = this.evt.getProperty[Float](this -> k)
  def getLong(k: String)    = this.evt.getProperty[Long](this -> k)
  def getShort(k: String)   = this.evt.getProperty[Short](this -> k)
}

/** Parser Helper
  *
  * Provides logic to create some objs in the Clarity
  * library
  */
private object ParserHelper 
{
  private val logger = LoggerFactory.getLogger("info.pudgestats.parser")

  def parseCombatLogMessage(
    evt: GameEvent)(
    table: StringTable)
  : CombatLogMessage = {
    new CombatLogMessage(
      CombatMessageType.fromInt(
        evt.getInt("type")),
        evt.getInt("sourcename"),
        evt.getInt("targetname"),
        evt.getInt("attackername"),
        evt.getInt("inflictorname"),
        evt.getBool("attackerillusion"),
        evt.getBool("targetillusion"),
        evt.getInt("value"),
        evt.getInt("health"),
        evt.getFloat("timestamp"),
        evt.getInt("targetsourcename"))(table)
  }
}

/** The Replay Parser
  *
  * Uses `clarity` to parse a replay. As the replay is being streamed,
  * `apply` on any registered `Aggregator`s is called with the messages 
  * parsed in the replay. 
  * 
  * @param p - the path of the replay to parse
  */
class ReplayParser(p: String)
{
  private val logger    = LoggerFactory.getLogger("info.pudgestats.parser")
  private val path      = Paths.get(p)
  private val aggrs     = new MutableList[Aggregator[ParsedMessage]]
  private var parseflag = false
  private val dotaMatch = new Match

  def entities          = this.dotaMatch.getEntities
  def descTables        = this.dotaMatch.getGameEventDescriptors
  def stringTables      = this.dotaMatch.getStringTables
  
  if (!Files.exists(path)) {
    throw new IOException(s"File '${p}' does not exist!")
  }

  lazy val gameInfo = Clarity.infoForFile(p)
  
  /** Acquires the game information from the prologue of the file */
  lazy val playerInfo = PlayerTable.fromCDemoInfo(this.gameInfo) match {
    case Some(table) => table
    case None => throw new RuntimeException("No valid game info available")
  }

  /** Adds an aggregator to be used during parsing */
  def +=(a: Aggregator[ParsedMessage]) = { this.aggrs += a; this }

  /** Parse replay
    *
    * Only should be called ONCE per replay parser. 
    * Once a replay is parsed...the ReplayParser 
    * stores all relevant information (that is needed).
    */
  def parse = if (!this.parseflag) {
    val iter = Clarity.peekIteratorForFile(path.toString,
      ClarityProfile.COMBAT_LOG, ClarityProfile.ENTITIES)

    this.parseflag = true    

    try {
      while (iter.hasNext) {
        val peek = iter.next

        if (peek.getBorder.isPeekTickBorder && 
            !this.dotaMatch.getGameEvents.isEmpty) 
        {
          val combatLogDesc = this.descTables.forName(Const.CombatLogKey)

          this.dotaMatch.getGameEvents.map {
            case evt if combatLogDesc.getEventId == evt.getEventId => {
              this.aggrs.map {
                case aggr => {
                  val gvt = new GameEvent(evt, combatLogDesc)
                  val msg = ParserHelper.parseCombatLogMessage(gvt)(
                    this.stringTables.forName(Const.CombatLogNamesKey))
                  aggr.apply(msg)
                }
              }
            }
            case _ =>
          }
          
          // TODO: Figure out a way to remove the dependency on 
          // the PudgeDTTableName here...Make it more general purpose!!
          val entity = this.entities.getByDtName("DT_DOTA_Unit_Hero_Pudge")        
          
          if (entity != null) {
            this.aggrs.map {
              case aggr => {
                val msg = new PlayerMessage(entity, 
                                            this.dotaMatch.getGameTime, 
                                            this.dotaMatch.getEntities)
                aggr.apply(msg)
              }
            }
          }
        }

        peek.apply(this.dotaMatch)
      }
    } finally {
      iter.close
    }
  } else { this.logger.warn("Parse already called...can not parse twice") }
}

