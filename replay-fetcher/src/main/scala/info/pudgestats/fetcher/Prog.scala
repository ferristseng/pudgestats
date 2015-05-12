package info.pudgestats.fetcher

import org.slf4j.LoggerFactory

import java.nio.file.Paths
import java.util.concurrent.{Executors, TimeUnit}

import info.pudgestats.core.transport.{Transport, Digest}

import scala.util.{Success, Failure}
import scala.collection.mutable.HashSet
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/** The Program
  *
  * Combines the multiple components into into one! 
  * Two processes are run that modify two different sets of ValidMatches.
  * One pushes to an 'unprocessed' set, which contains any match found that has 
  * no download endpoint associated with it.
  *
  * When the unprocessed queue reaches or exceeds a certain threshold, 
  * a batch (whose size is specified in the configuration), is sent 
  * to a newly created Future, which queries Valve's GC server 
  * for additional details about the replay. After the client receives a 
  * response on all the matches, or a timeout is reached, the results 
  * are merged in the 'unprocessed' and 'processed' sets. Anything that is 
  * ready to download is considered processed, and any match where the replay 
  * is unavailable, or no response was received, is sent back to 'unprocessed'.
  *
  * The second process running is the downloader. At a specified interval, it 
  * takes matches from the processed queue, constructs the full download URL, 
  * and downloads the replay to a specified output directory.
  * 
  * When the download is complete, the replay is extracted, and the full
  * path of the replay is sent to a messaging queue.
  */
class Prog(
  private val conf: Config,
  private val matchFinder: FilteredMatchFinder, 
  private val accountScheduler: AccountScheduler,
  private val transport: Transport)
{
  private val logger                = LoggerFactory.getLogger("info.pudgestats.fetcher")
  private var startedChild          = false
  private val threadPoolExec        = Executors.newScheduledThreadPool(2)
  private var matchesDownloaded     = 0
  @volatile private var processed   = new HashSet[ValidMatch]
  @volatile private var unprocessed = new HashSet[ValidMatch]

  def start = {
    this.logger.trace(s"Starting Unprocessed: ${unprocessed.size}") 
    this.logger.trace(s"Starting Processed:   ${processed.size}")
    startScraper 
    startDownloader
  }

  /** Start match finder
    * 
    * Scrapes the Dota 2 Web API for new valid 
    * matches.
    */
  private def startScraper = {
    this.threadPoolExec.scheduleWithFixedDelay(new Runnable {
        override def run = {
          matchFinder.queryMatches.map {
            case m => {
              unprocessed.add(m)

              logger.trace(s"Match added to queue ${m}")
              logger.trace(s"Queue Size: ${unprocessed.size}")

              if (unprocessed.size >= conf.batchSize && !startedChild) {
                accountScheduler.nextAccount match {
                  case Some(acct) => findBatchDetails(acct)
                  case None => 
                }
              }
            }
          }
        }
    }, 0, conf.webapiInterval, TimeUnit.MILLISECONDS)
  }

  /** Start downloader */
  private def startDownloader = {
    this.threadPoolExec.scheduleWithFixedDelay(new Runnable {
      override def run = {
        processed.headOption match {
          case Some(mtch) => {
            processed.remove(mtch)

            try {
              downloadMatch(mtch)
            } catch {
              case ex: Throwable => {
                processed += mtch
                logger.error(s"An exception occured when downloading: $ex")
              }
            }
          }
          case None =>
        }
      }
    }, 0, conf.downloadInterval, TimeUnit.MILLISECONDS)
  }

  /** Takes matches from the unprocessed queue, and removes them */
  private def takeFromUnprocessed(i: Int): Set[ValidMatch] = { 
    val (left, right) = this.unprocessed.splitAt(i)
    this.unprocessed = right
    this.logger.trace(s"New Queue Size: ${this.unprocessed.size}")
    left.toSet
  }

  /** Launches an async task to get details about a match */
  private def findBatchDetails(acct: SteamAccount) = Future {
    val finder = new MatchDetailsFinder(takeFromUnprocessed(conf.batchSize), acct)

    this.startedChild = true

    try {
      finder.connect(1000 * 20 * 60)
    } catch {
      case ex: Throwable => this.logger.warn(s"Error while finding match details: $ex")
    }

    this.startedChild = false 

    (finder.processed, finder.unprocessed)
  } onSuccess {
    case (newProcessed, newUnprocessed) => {
      val ready = newProcessed.filter(m => m.status == Status.ReadyToDownload)
      val diff  = (newUnprocessed &~ ready) 

      this.unprocessed ++= diff 
      this.processed ++= ready 

      this.logger.trace(s"Success - New Processed Size: ${this.processed.size}")
      this.logger.trace(s"Success - New Unprocessed Size: ${this.unprocessed.size}")
    }
  }

  /** Downloads a match 
    * 
    * Any errors encountered will immediately requeue the match.
    * Once a download is completed, the full path is sent across the 
    * transport.
    */
  private def downloadMatch(mtch: ValidMatch, retry: Boolean = true): Unit = 
    mtch.replaySalt match {
      case Some(salt) => { 
        val file = s"${this.conf.replayPrefix}${mtch.id}_${salt}.dem"
        val path = Paths.get(this.conf.replayOutput, file).toString
        val url  = URLHelper.createReplayEndpoint(mtch.id, mtch.cluster, salt) 

        this.logger.trace(s"Downloading: $url")
        this.logger.trace(s"Saving to: $path")

        try {
          new Downloader(url, path).start // Synchronous

          this.transport.send(new Digest {
            def toBytes = path.getBytes 
          })
          
          mtch.status = Status.Completed

          this.matchesDownloaded += 1

          this.logger.trace(s"New Processed Queue Size: ${this.processed.size}")
          this.logger.trace(s"Matches Downloaded: ${this.matchesDownloaded}")
        } catch {
          case ex: Throwable => {
            this.logger.error(s"An error occured downloading: $ex")

            if (retry) this.downloadMatch(mtch, false)
          }
        }
      }
      case None => this.logger.error("A match was considered processed, " + 
                                     "when it should't have")
    }
}
