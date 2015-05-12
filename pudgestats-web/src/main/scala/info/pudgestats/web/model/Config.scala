package info.pudgestats.web.model

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._

/** This shouldn't be instantiated in code
  * ever.
  */
class Config(
  val leaderboardIncludeThresh: Int,
  val leaderboardSize: Int)

object ConfigsStore extends Schema {
  val configsStore = table[Config]

  def getConfigOption = 
    from(configsStore)(c => 
      select(c)).page(0, 1).headOption

  def getConfig = 
    this.getConfigOption match {
      case Some(c) => c
      case None => throw new RuntimeException("No config rows were returned!")
    }
}
