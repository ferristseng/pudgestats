package info.pudgestats.web.util

import java.sql.Timestamp
import java.util.TimeZone
import java.text.SimpleDateFormat

import org.squeryl.annotations.Transient

/** SqlTimestampSupport utility mixin trait
  *
  * Provides formatted timestamps for a model with a Sql Timestamp.
  */
trait SqlTimestampSupport {
  val timestamp: Timestamp
  
  // Since this is exclusively used by squeryl models, it makes sense to make 
  // this Transient
  @Transient 
  lazy val timestampWithDefaultFormatting = 
    this.timestampWithFormatting("dd MMMM, yyyy -- hh:mm:ss aa zzz")

  def timestampWithFormatting(
    string: String, 
    timezone: TimeZone = TimeZone.getTimeZone("US/Eastern")) 
  = { 
    val formatter = new SimpleDateFormat(string)
    formatter.setTimeZone(timezone)
    formatter.format(this.timestamp)
  }
}

/** SqlUpdatedSupport utility mixin trait
  *
  * Pretty much functions the same as SqlTimestampSupport, 
  * but requires a different abstract member, and provides 
  * differently named formatting methods and members.
  */
trait SqlUpdatedSupport {
  val updated: Timestamp

  @Transient 
  lazy val updatedWithDefaultFormatting = 
    this.updatedWithFormatting("dd MMMM, yyyy -- hh:mm:ss aa zzz")

  def updatedWithFormatting(
    string: String, 
    timezone: TimeZone = TimeZone.getTimeZone("US/Eastern")) 
  = { 
    val formatter = new SimpleDateFormat(string)
    formatter.setTimeZone(timezone)
    formatter.format(this.updated)
  }
}
