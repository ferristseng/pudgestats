package info.pudgestats.fetcher

import scala.collection.immutable.Set

import api.dota.data._

trait Filter[T] {
  def filter(m: T): Boolean 
}

class PlayerMatchFilter(
    private val heroesToKeep: Set[Integer],
    private val keepAnonymous: Boolean = false)
  extends Filter[Match] 
{
  def filter(m: Match): Boolean = 
    m.players.foldLeft(false)(
      (acc, p) => acc || 
                  (this.heroesToKeep.contains(p.hero) && 
                  (keepAnonymous || p.accountId != "4294967295".toLong))) 
}

class PlayerNumFilter(
    private val minPlayers: Int = 10)
  extends Filter[Match]
{
  def filter(m: Match): Boolean = m.players.size >= minPlayers
}

class RegionMatchFilter(
    private val regionsToKeep: Set[Integer])
  extends Filter[Match]
{
  def filter(m: Match): Boolean = 
    this.regionsToKeep.contains(m.cluster)
}

class ModeMatchFilter(
    private val modesToKeep: Set[Integer])
  extends Filter[Match]
{
  def filter(m: Match): Boolean = 
    this.modesToKeep.contains(m.gameMode)
}

class LobbyMatchFilter(
    private val lobbiesToKeep: Set[Integer])
  extends Filter[Match]
{
  def filter(m: Match): Boolean = 
    this.lobbiesToKeep.contains(m.lobbyType)
}

class AbandonMatchFilter(
    private val threshToKeep: Int)
  extends Filter[Match]
{
  def filter(m: Match): Boolean = {
    m.players.foldLeft(true)(
      (acc, p) => acc && p.leaverStatus == 0) || 
    m.duration > threshToKeep * 60
  }
}
