package info.pudgestats.web.app

import org.squeryl.PrimitiveTypeMode._

import info.pudgestats.web.conf.Config
import info.pudgestats.web.model.LeaderboardsStore

/** The main controller is the main access point 
  * into the application.
  *
  * Related controllers are mixed in, so routes 
  * are injected into the context for Scalate
  * templates.
  */
class MainController(val conf: Config)
  extends DefaultStack 
  with UserController
  with MatchController
  with SessionController
{
  val pageEntriesNum = 25

  get("/") {
    contentType = "text/html"

    val leaderboard = 
      from(LeaderboardsStore.leaderboards)(l => 
        select(l)
        orderBy(l.timestamp.desc)).page(0, 1)
      .head

    ssp("/main/index", "leaderboard" -> leaderboard)
  }  

  get("/about") {
    contentType = "text/html"

    ssp("/main/about")
  }

  get("/parsing") {
    contentType = "text/html"

    ssp("/main/parsing")
  }

  get("/bug") {
    contentType = "text/html"

    ssp("/main/bug")
  }

  get("/rescore") {
    contentType = "text/html"

    ssp("/main/rescore")
  }

  get("/scoring") {
    contentType = "text/html"

    ssp("/main/scoring")
  }
}
