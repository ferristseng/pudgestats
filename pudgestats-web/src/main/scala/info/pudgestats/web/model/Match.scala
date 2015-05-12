package info.pudgestats.web.model

import java.sql.Timestamp

import scala.collection.mutable.MutableList

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.annotations.Transient

import info.pudgestats.core.replay.ReplayEvent
import info.pudgestats.web.util.{PudgeHookTypeHelper,
  ExtendedTable, HasDuplicate, HookEventsExtension,
  SqlTimestampSupport, SqlUpdatedSupport}

class Match(
    val matchId: Int,
    val winner: Int,
    val mode: Int,
    val duration: Float,
    val selfRotDamage: Int,
    val creepRotDamage: Int,
    val neutralRotDamage: Int,
    val heroRotDamage: Int,
    val parserId: Int,
    var numHooksUsed: Int,
    var numHooksCreepsHit: Int,
    var numHooksNeutralsHit: Int,
    var numHooksIllusionsHit: Int,
    var numHooksEnemyHeroesHit: Int,
    var numHooksAlliedHeroesHit: Int,
    val timestamp: Timestamp,
    val updated: Timestamp = new Timestamp(System.currentTimeMillis))
  extends HasDuplicate[Match]
  with KeyedEntity[Int]
  with HookEventsExtension
  with SqlTimestampSupport
  with SqlUpdatedSupport
{
  val id = 0

  def dbEquiv(other: Match) = 
    this.matchId === other.matchId

  @Transient
  lazy val enemyRotDamage = this.creepRotDamage + this.heroRotDamage

  /** Parser information */
  lazy val parser = MatchesStore.parserToMatches.right(this)
  
  /** General match information */
  lazy val players = MatchesStore.matchToPlayers.left(this)

  /** Events that occured during the game */
  lazy val killEvents = MatchesStore.matchToKillEvents.left(this)
  lazy val runeEvents = MatchesStore.matchToRuneEvents.left(this)
  lazy val hookEvents = MatchesStore.matchToHookEvents.left(this)
  lazy val itemEvents = MatchesStore.matchToPlayerItemEvents.left(this)
  lazy val deathEvents = MatchesStore.matchToDeathEvents.left(this)
  lazy val abilityEvents = MatchesStore.matchToPlayerAbilityEvents.left(this)
  lazy val dismemberEvents = MatchesStore.matchToDismemberEvents.left(this)

  /** Sorted Events */
  lazy val killEventsSorted = this.killEvents.toArray.sorted[ReplayEvent]
  lazy val runeEventsSorted = this.runeEvents.toArray.sorted[ReplayEvent]
  lazy val hookEventsSorted = this.hookEvents.toArray.sorted[ReplayEvent]
  lazy val itemEventsSorted = this.itemEvents.toArray.sorted[ReplayEvent]
  lazy val deathEventsSorted = this.deathEvents.toArray.sorted[ReplayEvent]
  lazy val abilityEventsSorted = this.abilityEvents.toArray.sorted[ReplayEvent]
  lazy val dismemberEventsSorted = this.dismemberEvents.toArray.sorted[ReplayEvent]

  /** Timeline -- All events combined together, sorted by time they occur */
  lazy val timeline: Iterable[ReplayEvent] =
    (killEvents ++: 
      runeEvents ++: 
      itemEvents ++: 
      deathEvents ++: 
      this.processedHookEvents ++:
      this.processedAbilityEvents ++: 
      this.processedDismemberEvents)
    .toArray.sorted[ReplayEvent]

  /** Processes Hook events. Removes all events where 
    * Pudge throws a hook, and it hits shortly after
    */
  private def processedHookEvents: Iterable[HookEvent] = {
    val it = this.hookEventsSorted.filter(_.hit).toIterator

    if (it.hasNext) {
      var current = it.next

      this.hookEventsSorted.filter {
        case evt => {
          val diff = current.timestamp - evt.timestamp

          if (diff < 1.5f && diff > 0 && !evt.hit) {
            if (it.hasNext) { current = it.next }
            false
          } else {
            true
          }
        }
      }
    } else {
      this.hookEvents
    }
  }

  /** Processes Ability events. Removes all ability events where 
    * the level upgraded to is 0.
    */
  private def processedAbilityEvents: Iterable[ReplayEvent] = 
    this.abilityEvents.filter(_.level > 0)

  /** Processes Dismember events. Condenses all dismember events that 
    * are related
    */
  private def processedDismemberEvents: Iterable[DismemberEvent] = {
    val events = MutableList[DismemberEvent]()
    var evtBuf = MutableList[DismemberEvent]()

    var tmpEvt: Option[DismemberEvent] = None

    def collapseTmp = tmpEvt match {
      case Some(e) => {
        events += 
          new DismemberEvent(
            e.target, e.illusion, 
            evtBuf.foldLeft(0)(_ + _.damage), e.timestamp)
        evtBuf.clear
      }
      case None =>
    }

    for (evt <- this.dismemberEventsSorted) {
      if (evt.damage == -1) {
        collapseTmp
        tmpEvt = Some(evt)
      } else {
        evtBuf += evt
      }
    }

    collapseTmp

    events
  }

  override def toString = s"<Match id=$id>"
}

/** Match store */
object MatchesStore extends Schema {
  val matches = new ExtendedTable[Int, Match](table[Match])

  val parserToMatches = 
    oneToManyRelation(ParserVersionsStore.parserVersions, matches).
      via((p, m) => p.id === m.parserId)

  /** General info relations */
  val matchToPlayers = 
    oneToManyRelation(matches, PlayersStore.players).
      via((m, p) => m.id === p.matchId)

  /** Relations with Events */
  val matchToDeathEvents = 
    oneToManyRelation(matches, EventsStore.deathEvents).
      via((m, e) => m.id === e.matchId)

  val matchToDismemberEvents = 
    oneToManyRelation(matches, EventsStore.dismemberEvents).
      via((m, e) => m.id === e.matchId)

  val matchToHookEvents =
    oneToManyRelation(matches, EventsStore.hookEvents).
      via((m, e) => m.id === e.matchId)

  val matchToRuneEvents = 
    oneToManyRelation(matches, EventsStore.runeEvents).
      via((m, e) => m.id === e.matchId)

  val matchToKillEvents = 
    oneToManyRelation(matches, EventsStore.killEvents).
      via((m, e) => m.id === e.matchId)
  
  val matchToPlayerItemEvents = 
    oneToManyRelation(matches, EventsStore.itemEvents).
      via((m, e) => m.id === e.matchId)

  val matchToPlayerAbilityEvents =
    oneToManyRelation(matches, EventsStore.abilityEvents).
      via((m, e) => m.id === e.matchId)

  on(matches)(m => declare(
    m.matchId is unique
  ))
}
