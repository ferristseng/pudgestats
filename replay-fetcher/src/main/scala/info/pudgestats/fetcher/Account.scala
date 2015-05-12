package info.pudgestats.fetcher

/** A Steam User account
  *
  * Other than the obvious, the MatchDetailsFinder 
  * keeps track of the next time the SteamAccount 
  * should be active. That means when we hit the
  * query limit, the Steam account deactivates 
  * for 24 hours.
  */
class SteamAccount(
  val username: String,
  val password: String,
  val handle: String,
  var nextActive: Long = System.currentTimeMillis)
{
  def deactivate(hrs: Float) = this.nextActive = 
    System.currentTimeMillis + 3600 * (hrs * 1000).toLong

  def isAvailable: Boolean = System.currentTimeMillis >= this.nextActive

  override def toString = 
    s"<SteamAccount username=$username handle=$handle time=$nextActive " + 
    s"available=$isAvailable>"
}

