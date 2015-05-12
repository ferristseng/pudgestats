package info.pudgestats.fetcher

import com.google.common.eventbus.Subscribe

import org.slf4j.LoggerFactory

import scala.collection.GenTraversable
import scala.collection.mutable.HashSet

import com.dota.Dota2Client
import com.dota.event._
import com.dota.const.Game
import com.dota.proto.DotaGcmessagesCommon.EDOTAGCMsg
import com.dota.proto.DotaGcmessagesClient.{CMsgGCMatchDetailsRequest,
  CMsgGCMatchDetailsResponse}
import com.steam.ClientDisconnected
import com.steam.event._
import com.steam.const.{EPersonaState, EResult}
import com.steam.net.gc.msg.ClientGCMsgProtoBuf

/** Finder for details of a Match
  * 
  * Processes batches of ValidMatches 
  *
  * TODO - Add better handling of different possible failures...
  *        Try to do this in a way where the MatchDetailsFinder will 
  *        always try to succeed in the allotted time in the time it 
  *        is given.
  *        I currently do not know how the MatchDetailsFinder will fail
  *        but hopefully after some testing, some types of failures will 
  *        reveal themselves organically.
  *
  * @param unprocessed  - items to process
  * @param steamAccount - Steam Account to use 
  * @param steamRetry   - Time to retry connecting while not connected
  * @param steamTimeout - Time to wait before giving up connection
  */
class MatchDetailsFinder(
  val unprocessed: Set[ValidMatch],
  val steamAccount: SteamAccount,
  val steamRetry: Int = 1000,
  val steamTimeout: Int = 10)
{
  private val logger        = LoggerFactory.getLogger("info.pudgestats.fetcher")
  private val dotaClient    = new Dota2Client(this.steamAccount.username, 
                                              this.steamAccount.password)
  val processed             = new HashSet[ValidMatch]

  /** Connection handler for the Dota2Client
    *
    * Implements reconnection policies, and other policies which 
    * depend on the Config object.
    */
  private object SteamEventHandler {
    @Subscribe
    def onGCWelcome(evt: GCWelcomeReceived) = { 
      logger.trace("GC Received Welcome!")

      dotaClient.steamClient.steamFriends.name = steamAccount.handle 
      dotaClient.steamClient.steamFriends.epersona = EPersonaState.Online

      new Thread(new Runnable {
        override def run = unprocessed.map { case m => requestMatch(m); Thread.sleep(2500) } 
      }).run
    }

    /** Handles received messages
      * 
      * Filters out match details responses for match requests. 
      *
      * If 100 matches are processed, then no more responses can be 
      * received of value.
      */
    @Subscribe
    def onMsgReceived(evt: SteamGCEvent) = {
      evt match {
        case GCMsgReceived(msg, appID, pkt) if appID == Game.steamID => {
          msg match {
            case EDOTAGCMsg.k_EMsgGCMatchDetailsResponse_VALUE => {
              // Received a message with a match!
              var req = ClientGCMsgProtoBuf.createReceived[
                CMsgGCMatchDetailsResponse.Builder,
                CMsgGCMatchDetailsResponse](pkt).body.build

              if (req.getResult == EResult.OK.id) {
                val mtch = req.getMatch
                val newm = new ValidMatch(mtch.getMatchId, mtch.getMatchSeqNum,
                                          mtch.getCluster, Some(mtch.getReplaySalt),
                                          Status(mtch.getReplayState.getNumber))
                processed.add(newm)
                if (processed.size == unprocessed.size) dotaClient.disconnect
              } else { logger.warn("Non-OK response received from GC") }
            }
            case _ => // Ignore
          }
        }
        case _ => // Ignore
      }
    }
  }

  // Register our event handler with the client
  this.dotaClient.addHandler(SteamEventHandler)

  /** Connects to an available Steam account */
  def connect(timeout: Long) = { 
    this.logger.trace(s"Logging in with: ${this.steamAccount.username}")

    while (!this.dotaClient.isConnected) {
      this.dotaClient.connect(this.steamTimeout)
      Thread.sleep(this.steamRetry)
    }

    this.dotaClient.steamClient.netThread.join(timeout)
    this.dotaClient.disconnect
  }

  /** Sends a MatchDetailsRequest to the GC
    * 
    * Makes an assumption that the server and client are both 
    * ready.
    */
  def requestMatch(mtch: ValidMatch) = { 
    val req = ClientGCMsgProtoBuf.createSend[
      CMsgGCMatchDetailsRequest.Builder,
      CMsgGCMatchDetailsRequest](EDOTAGCMsg.k_EMsgGCMatchDetailsRequest_VALUE)
    
    req.body.setMatchId(mtch.id)
    
    this.dotaClient.steamGC.send(req, Game.steamID)
  }
}
