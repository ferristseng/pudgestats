package info.pudgestats.web

import java.sql.Timestamp

import org.squeryl.PrimitiveTypeMode._

import org.slf4j.LoggerFactory

import com.dota.data.Hero

import info.pudgestats.core.replay.ReplayEvent
import info.pudgestats.core.transport.Consumer
import info.pudgestats.web.db.DatabaseInit
import info.pudgestats.web.model._
import info.pudgestats.web.util.PudgeHookTypeHelper
import info.pudgestats.web.transport.PudgeJsonDigest

/** Operation that runs in background, consuming pudge stat digests received 
  * by a consumer, and persisting it to the database
  *
  * @param consumer - the consumer to use
  */
class PudgeStatsPersister(
    private val consumer: Consumer)
  extends Runnable
{
  import EventConversions._
  import PlayerConversions._
  import scala.collection.JavaConversions._

  protected val logger = LoggerFactory.getLogger("info.pudgestats.web.persister")

  def run = {
    this.consumer.receive match {
      case Some(data) => {
        val digest = (new PudgeJsonDigest).fromJsonString(new String(data))

        this.logger.info(s"New Match:      ${digest.matchId}")
        this.logger.info(s"Parser Version: ${digest.parserVersion}")
        this.logger.info(s"Version Name:   ${digest.versionName}")

        transaction {
          val existed = !MatchesStore.matches
            .where(m => m.matchId === digest.matchId).headOption.isEmpty 
          val parser = ParserVersionsStore.parserVersions.insertOrIgnore(
            new ParserVersion(digest.parserVersion.toString, digest.versionName))

          digest.pudgeOpt match {
            case Some(pudge) if (!existed) => {
              val mtch = MatchesStore.matches.insert(
                new Match(
                  digest.matchId, 
                  digest.matchWinner, 
                  digest.matchMode, 
                  digest.matchDuration, 
                  digest.selfRotDamage, 
                  digest.creepRotDamage, 
                  digest.neutralRotDamage, 
                  digest.heroRotDamage, 
                  parser.id,
                  digest.hooksUsed.size,
                  digest.hooksCreepsHit.size,
                  digest.hooksNeutralsHit.size,
                  digest.hooksIllusionsHit.size,
                  digest.hooksEnemyHeroesHit.size,
                  digest.hooksAlliedHeroesHit.size,
                  new Timestamp(digest.matchTimestamp),
                  new Timestamp(digest.parseTimestamp)))

              // Call the digest handlers
              this.handleDeathEvents(digest, mtch)
              this.handleDismemberEvents(digest, mtch)
              this.handleRuneEvents(digest, mtch)
              this.handleItemEvents(digest, mtch)
              this.handleAbilityEvents(digest, mtch)
              this.handleKillEvents(digest, mtch)
              this.handleHookEvents(digest, mtch)
              this.handlePlayers(digest, mtch)
            }
            case None => 
              this.logger.error(s"Match ${digest.matchId} had no pudge player")
            case _ => // Ignore...Already added
          }
        }
        
        this.consumer.ack
      }
      case None => this.logger.info("Received nothing...")
    }
  }

  /** Handlers for events in digest */
  private def handleDeathEvents(
    digest: PudgeJsonDigest, 
    mtch: Match) 
  = {
    digest.deathEvents.map { case e => mtch.deathEvents.associate(e) }
  }

  private def handleDismemberEvents(
    digest: PudgeJsonDigest,
    mtch: Match)
  = {
    digest.dismEvents.map { case e => mtch.dismemberEvents.associate(e) }
  }

  private def handleRuneEvents(
    digest: PudgeJsonDigest,
    mtch: Match)
  = {
    digest.runeEvents.map { case e => mtch.runeEvents.associate(e) }
  }

  private def handleItemEvents(
    digest: PudgeJsonDigest,
    mtch: Match)
  = {
    digest.itemEvents.map { case e => mtch.itemEvents.associate(e) }
  }

  private def handleAbilityEvents(
    digest: PudgeJsonDigest,
    mtch: Match)
  = {
    digest.abilityEvents.map { case e => mtch.abilityEvents.associate(e) }
  }

  private def handleKillEvents(
    digest: PudgeJsonDigest,
    mtch: Match)
  = {
    digest.killEvents.map { case e => mtch.killEvents.associate(e) }
  }

  private def handleHookEvents(
    digest: PudgeJsonDigest,
    mtch: Match)
  = {
    digest.hookEvents.map { case e => mtch.hookEvents.associate(e) }
  }

  private def handlePlayers(
    digest: PudgeJsonDigest,
    mtch: Match)
  = {
    digest.players.map { case p => mtch.players.associate(p) }
  }
}
