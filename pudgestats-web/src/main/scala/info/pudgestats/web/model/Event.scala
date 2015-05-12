package info.pudgestats.web.model

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._

import scala.language.implicitConversions
  
object EventsStore extends Schema {
  val hookEvents      = table[HookEvent]
  val itemEvents      = table[PlayerItemEvent]
  val killEvents      = table[KillEvent]
  val runeEvents      = table[RuneEvent]
  val deathEvents     = table[DeathEvent]
  val abilityEvents   = table[PlayerAbilityEvent]
  val dismemberEvents = table[DismemberEvent] 
}

object EventConversions {
  import info.pudgestats.core.replay.{
    KillEvent => CoreKillEvent,
    HookEvent => CoreHookEvent,
    RuneEvent => CoreRuneEvent,
    DeathEvent => CoreDeathEvent,
    DismemberEvent => CoreDismemberEvent,
    PlayerItemEvent => CorePlayerItemEvent,
    PlayerAbilityEvent => CorePlayerAbilityEvent}

  implicit def coreHookEventToHookEvent(evt: CoreHookEvent) = 
    new HookEvent(evt.target, evt.hit, evt.illusion, evt.timestamp)

  implicit def coreKillEventToKillEvent(evt: CoreKillEvent) = 
    new KillEvent(evt.target, evt.timestamp)

  implicit def coreRuneEventToRuneEvent(evt: CoreRuneEvent) =
    new RuneEvent(evt.rune, evt.timestamp)

  implicit def coreDeathEventToDeathEvent(evt: CoreDeathEvent) = 
    new DeathEvent(evt.attacker, evt.illusion, evt.timestamp)

  implicit def coreDismemberEventToDismemberEvent(
    evt: CoreDismemberEvent) 
  = new DismemberEvent(evt.target, evt.illusion, evt.damage,
      evt.timestamp) 

  implicit def corePlayerItemEventToPlayerItemEvent(
    evt: CorePlayerItemEvent)
  = new PlayerItemEvent(evt.name, evt.timestamp)

  implicit def corePlayerAbilityEventToPlayerAbilityEvent(
    evt: CorePlayerAbilityEvent)
  = new PlayerAbilityEvent(evt.name, evt.level, evt.timestamp)
}
