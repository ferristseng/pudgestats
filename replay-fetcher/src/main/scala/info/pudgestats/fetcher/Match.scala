package info.pudgestats.fetcher

/** Possible statuses of a ValidMatch 
  *
  * Placed an order to satisfy the compare 
  * functionality of ValidMatch
  */
object Status extends Enumeration {
  type Status         = Value
  val ReadyToDownload = Value(0)
  val ReplayPending   = Value(1)
  val ReplayNotFound  = Value(2)
  val Unknown         = Value(3)
  val DownloadError   = Value(4)
  val Completed       = Value(5)
}

/** A valid Dota match, and the status of its replay */
class ValidMatch(
    val id: Int,
    val seqNum: Long,
    val cluster: Int,
    var replaySalt: Option[Int],
    var status: Status.Status)
  extends StringSerializable[ValidMatch]
  with Ordered[ValidMatch]
{
  val timestamp = System.currentTimeMillis

  /** Default constructor used souly for deserializing */
  def this() = this(0, 0.toLong, 0, None, Status.Unknown)

  /** Ordering comparisons */
  object Comparison {
    val GT = 1
    val LT = -1
    val EQ = 0
  }

  /** Implement `compare` for Ordered[T]
    *
    * Prioritize matches with a lower sequence number, 
    * because they may have replays that are expiring soon.
    * 
    * As a second parameter, prioritize matches where there 
    * was an error downloading, or the replay is pending, as 
    * opposed to replays that have an unknown status. 
    * Unknown statuses generally imply newer replays.
    */
  def compare(that: ValidMatch) = if (this.isComplete) {
    Comparison.LT 
  } else if (that.isComplete) {
    Comparison.GT
  } else {                                    // Both are not complete
    if (this.seqNum > that.seqNum) {          // Case where `this` is newer
      Comparison.LT
    } else {                                  // Case where `this` is older 
      Comparison.GT                           // (note they will never be equal)
    }
  }

  /** Check if the replay has been downloaded or does not exist */
  def isComplete = 
    this.status == Status.Completed || this.status == Status.ReplayNotFound

  /** Implement `StringSerializable`
    *
    * Pretty much just separates the members my commas.
    */
  def serialize: String = {
    val replaySaltAsInt = this.replaySalt match {
      case Some(salt) => salt
      case None => -1
    }

    s"${this.id},${this.seqNum},${this.cluster}," +
    s"${replaySaltAsInt},${this.status.id},"
  }

  def deserialize(s: String): ValidMatch = {
    val parts = s.split(',')
    val intToReplaySalt = parts(3).toInt match {
      case -1 => None
      case x => Some(x)
    }

    new ValidMatch(parts(0).toInt, parts(1).toLong, parts(2).toInt, 
                   intToReplaySalt, Status(parts(4).toInt))
  }

  /** Checks if the other is an instance of ValidMatch, and compares all 
    * the fields
    */
  override def equals(other: Any): Boolean = {
    other.isInstanceOf[ValidMatch] &&
    this.id         == other.asInstanceOf[ValidMatch].id
  }

  override def hashCode: Int = this.id.hashCode

  override def toString: String = 
    s"<ValidMatch id='${this.id}' status='${this.status}' " + 
    s"cluster='${this.cluster}' salt='${this.replaySalt}'>"
}

