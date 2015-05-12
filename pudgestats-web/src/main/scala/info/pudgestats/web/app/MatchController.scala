package info.pudgestats.web.app

import org.squeryl.PrimitiveTypeMode._

import info.pudgestats.web.model.{MatchesStore, EventsStore, PlayersStore}

/** MatchController
  * 
  * Routes regarding dealing with a Match.
  * Routes further down are matched first!
  */
trait MatchController
{
  self: DefaultStack =>

  def pageEntriesNum: Int

  val matchShowRoute = get("/m/:id") {
    contentType = "text/html"
    
    val id = try { 
      params.getOrElse("id", "-1").toInt
    } catch {
      case ex: NumberFormatException => halt(404)
    }

    MatchesStore.matches.where(m => m.matchId === id).headOption match {
      case Some(mtch) => ssp("/matches/show", "mtch" -> mtch)
      case None => halt(404)
    }
  }

  val matchIndexRoute = get("/m/list") {
    contentType = "text/html"
    
    // Paginate the index
    val pageCurrent = params.getOrElse("page", "1").toInt - 1
    val pageTotal = MatchesStore.matches.count(_ => true) / this.pageEntriesNum
    val matches = 
      from(MatchesStore.matches)(m => 
        select(m) 
        orderBy(m.id.desc)).page(
          pageCurrent * this.pageEntriesNum, this.pageEntriesNum).toIterable

    ssp("/matches/index", "matches" -> matches,
                          "pageCurrent" -> pageCurrent,
                          "pageTotal" -> pageTotal)
  }
}
