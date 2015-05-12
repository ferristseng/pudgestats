package info.pudgestats.web.util

import scala.collection.GenTraversable

import info.pudgestats.core.replay.{Player, HookEvent}

import com.dota.helper.NameHelper

object PudgeHookTypeHelper 
{
  def didHookCreep(e: HookEvent) = 
    e.hit && NameHelper.isCreep(e.target)

  def didHookIllusion(e: HookEvent) = 
    e.hit && e.illusion

  def didHookEnemyHero(
    e: HookEvent, 
    pudge: Player, 
    players: GenTraversable[Player]) 
  : Boolean = 
    this.didHookHeroTemplate(e, players)(p => this.didHookEnemyHero(e, pudge, p)) 

  def didHookAlliedHero(
    e: HookEvent, 
    pudge: Player, 
    players: GenTraversable[Player])
  : Boolean = 
    this.didHookHeroTemplate(e, players)(p => this.didHookAlliedHero(e, pudge, p)) 

  def didHookNeutral(e: HookEvent) = 
    e.hit && NameHelper.isNeutral(e.target)

  private def didHookEnemyHero(
    e: HookEvent, 
    pudge: Player, 
    target: Player)
  : Boolean =
    pudge.gameTeam != target.gameTeam && e.hit && !e.illusion

  private def didHookAlliedHero(
    e: HookEvent, 
    pudge: Player, 
    target: Player)
  : Boolean = 
    pudge.gameTeam == target.gameTeam && e.hit && !e.illusion

  /** Template method for determining if pudge hooked a hero 
    * of a certain kind
    */
  private def didHookHeroTemplate(
    e: HookEvent, 
    players: GenTraversable[Player])(
    fn: Player => Boolean) 
  : Boolean = 
    if (NameHelper.isHero(e.target)) {
      players.find(p => p.hero == e.target) match {
        case Some(player) => fn(player)
        case None => false
      }
    } else {
      false
    }
}
