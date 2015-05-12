package info.pudgestats.parser

import info.pudgestats.core.transport.{
  PudgeJsonDigest => PudgeJsonDigestAbstract}

import com.dota2.proto.Demo.CDemoFileInfo

import com.google.gson.annotations.Expose

/** Creates a JSON digest from a variety of components 
  * to send to another program to be processed
  */
class PudgeJsonDigest(
    gameInfo: CDemoFileInfo,
    playerInfo: PlayerTable,
    hookAggr: PudgeHookAggregator,
    killAggr: PudgeKillAggregator,
    rotAggr: PudgeRotAggregator,
    dismAggr: PudgeDismemberAggregator,
    runeAggr: PudgeRunePickupAggregator,
    itemAggr: PudgePurchaseAggregator,
    abilityAggr: PudgeAbilityAggregator,
    deathAggr: PudgeDeathAggregator)
  extends PudgeJsonDigestAbstract[PudgeJsonDigest]
{
  @Expose val version     = Parser.Version
  @Expose val versionName = Parser.VersionName

  parseTimestamp    = System.currentTimeMillis

  matchTimestamp    = gameInfo.getGameInfo.getDota.getEndTime.toLong * 1000
  matchId           = gameInfo.getGameInfo.getDota.getMatchId
  matchWinner       = gameInfo.getGameInfo.getDota.getGameWinner
  matchMode         = gameInfo.getGameInfo.getDota.getGameMode
  matchDuration     = gameInfo.getPlaybackTime

  this.selfRotDamage    = rotAggr.selfRotDamageDealt
  this.creepRotDamage   = rotAggr.creepRotDamageDealt
  this.neutralRotDamage = rotAggr.neutralRotDamageDealt
  this.heroRotDamage    = rotAggr.heroRotDamageDealt

  hookAggr.hookEvents.map(e => this.hookEvents.add(e))
  killAggr.killEvents.map(e => this.killEvents.add(e))
  dismAggr.dismemberEvents.map(e => this.dismEvents.add(e))
  runeAggr.runeEvents.map(e => this.runeEvents.add(e))
  itemAggr.purchaseEvents.map(e => this.itemEvents.add(e))
  deathAggr.deathEvents.map(e => this.deathEvents.add(e))
  abilityAggr.abilityEvents.map(e => this.abilityEvents.add(e))
  playerInfo.players.values.map(p => this.players.add(p))
}
