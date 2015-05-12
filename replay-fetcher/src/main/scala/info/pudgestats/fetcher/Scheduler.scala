package info.pudgestats.fetcher

import org.slf4j.LoggerFactory

/** Should always return a valid account 
  * if one exists in the pool, otherwise 
  * return `None`
  */
trait AccountScheduler {
  def nextAccount: Option[SteamAccount]
}

object AccountScheduler {
  val DotaMaxRequests = 100
  val HoursInDay      = 24
}

/** Basic scheduler implementation
  * 
  * Keeps track of the time which the next
  * account was checked out, and will only 
  * return accounts in such a way that 
  * it will be impossible for that account to 
  * have exceeded the Dota max request limit.
  *
  * @param accounts - the account pool to choose from
  * @param _batchSize - the number of request that will be 
  *                    made for each account
  */
class BasicAccountScheduler(
    private val accounts: List[SteamAccount],
    _batchSize: Int = AccountScheduler.DotaMaxRequests) 
  extends AccountScheduler
{
  private val logger = LoggerFactory.getLogger("info.pudgestats.fetcher")
  private val batchSize = if (_batchSize > AccountScheduler.DotaMaxRequests) {
    this.logger.warn("Batch size exceeded maximum value...setting to max!")
    AccountScheduler.DotaMaxRequests
  } else {
    _batchSize
  }

  def nextAccount = this.accounts.find((a) => a.isAvailable) match {
    case Some(acct) => {
      acct.deactivate(
        ((this.batchSize.toFloat / AccountScheduler.DotaMaxRequests) * 24).toInt)
      Some(acct)
    }
    case None => None
  }
}
