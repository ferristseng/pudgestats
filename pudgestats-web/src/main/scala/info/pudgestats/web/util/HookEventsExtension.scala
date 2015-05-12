package info.pudgestats.web.util

import java.util.ArrayList

import com.dota.data.Hero

import info.pudgestats.core.Const
import info.pudgestats.core.replay.{Player, HookEvent}

/** Use with Java collections */
trait HookEventsExtensionJava extends HookEventsExtensionBase {
  import scala.collection.JavaConversions._

  def players: ArrayList[Player]
  def hookEvents: ArrayList[HookEvent]

  def _players    = this.players
  def _hookEvents = this.hookEvents
}

/** Use with Scala collections */
trait HookEventsExtension extends HookEventsExtensionBase {
  def players: Iterable[Player]
  def hookEvents: Iterable[HookEvent]

  def _players    = this.players
  def _hookEvents = this.hookEvents
}

protected trait HookEventsExtensionBase {
  protected def _players: Iterable[Player]
  protected def _hookEvents: Iterable[HookEvent]

  private lazy val _pudge = 
    this._players.find(p => p.hero == Const.PudgeNPCString)

  private lazy val _hooksUsed = 
    this._hookEvents.filter(e => !e.hit)

  private lazy val _hooksCreepsHit = 
    this._hookEvents.filter(e => PudgeHookTypeHelper.didHookCreep(e)) 

  private lazy val _hooksNeutralsHit = 
    this._hookEvents.filter(e => PudgeHookTypeHelper.didHookNeutral(e)) 

  private lazy val _hooksIllusionsHit = 
    this._hookEvents.filter(e => PudgeHookTypeHelper.didHookIllusion(e)) 

  private lazy val _hooksEnemyHeroesHit = 
    this._hookEvents.filter(e => 
      PudgeHookTypeHelper.didHookEnemyHero(e, this.pudge, this._players))
        
  private lazy val _hooksAlliedHeroesHit =
    this._hookEvents.filter(e => 
      PudgeHookTypeHelper.didHookAlliedHero(e, this.pudge, this._players))

  def pudge                 = this._pudge.get
  def pudgeOpt              = this._pudge
  def hooksUsed             = this._hooksUsed
  def hooksCreepsHit        = this._hooksCreepsHit
  def hooksNeutralsHit      = this._hooksNeutralsHit
  def hooksIllusionsHit     = this._hooksIllusionsHit
  def hooksEnemyHeroesHit   = this._hooksEnemyHeroesHit
  def hooksAlliedHeroesHit  = this._hooksAlliedHeroesHit
}
