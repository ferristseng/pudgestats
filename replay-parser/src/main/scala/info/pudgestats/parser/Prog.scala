package info.pudgestats.parser

import org.slf4j.LoggerFactory

import java.io.IOException
import java.nio.file.{Paths, Files}

import info.pudgestats.core.transport.{Transport, Consumer}

object Prog {
  /** Parse a single replay */
  def parseReplay(replayPath: String): PudgeJsonDigest = {
    val replayParser    = new ReplayParser(replayPath)
    val hookAggr        = new PudgeHookAggregator
    val killAggr        = new PudgeKillAggregator
    val rotAggr         = new PudgeRotAggregator 
    val dismAggr        = new PudgeDismemberAggregator
    val runeAggr        = new PudgeRunePickupAggregator
    val itemAggr        = new PudgePurchaseAggregator
    val abilityAggr     = new PudgeAbilityAggregator
    val deathAggr       = new PudgeDeathAggregator
    val playerInfo      = replayParser.playerInfo

    (replayParser += hookAggr += killAggr += deathAggr +=
     rotAggr += dismAggr += runeAggr += itemAggr += abilityAggr).parse

    new PudgeJsonDigest(replayParser.gameInfo, playerInfo, hookAggr, killAggr, 
      rotAggr, dismAggr, runeAggr, itemAggr, abilityAggr, deathAggr)
  }

  /** Policy to determine whether or not the digest
    * should be sent across the transport
    */
  protected def shouldTransportDigest(digest: PudgeJsonDigest) =
    digest.matchId != 0
}

class Prog(
    private val consumer: Consumer,
    private val transport: Transport)
  extends Runnable
{
  private var terminateFlag = false
  private val logger = LoggerFactory.getLogger("info.pudgestats.parser")

  def run =
    while(!this.terminateFlag) {
      this.consumer.receive match {
        case Some(data) => {
          val path = new String(data)

          this.logger.trace(s"New Replay: $path")

          try {
            this.parseReplayAndSend(path)
          } catch {
            case ex: IOException => 
              this.logger.error(s"IOException $path: $ex")
            case ex: RuntimeException =>
              this.logger.error(s"Could not parse $path: $ex")
            case ex: Exception =>
              this.logger.error(s"Uncaught Exception: $path: $ex")
          } finally {
            Files.deleteIfExists(Paths.get(path))

            this.consumer.ack
          }
        }
        case None =>
      }
    }

  def parseReplayAndSend(replayPath: String) = {
    val digest = Prog.parseReplay(replayPath)

    if (Prog.shouldTransportDigest(digest)) {
      this.transport.send(digest)
    }

    digest
  }

  def terminate = this.terminateFlag = true
}
