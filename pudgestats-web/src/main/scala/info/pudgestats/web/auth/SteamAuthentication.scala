package info.pudgestats.web.auth

import org.squeryl.PrimitiveTypeMode._

import org.scalatra.auth.ScentryStrategy
import org.scalatra.{Cookie, CookieOptions, ScalatraBase}

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import info.pudgestats.web.model.{SessionsStore, SteamUserSession}

/** Steam Authorization strategy
  *
  * TODO: Check against the DB, to see if the 
  * user's cookie is valid.
  */
class SteamAuthStrategy(protected val app: ScalatraBase)(
    implicit request: HttpServletRequest, 
    response: HttpServletResponse) 
  extends ScentryStrategy[SteamUserSession] 
{
  /** Unauthenticated Response - 403 */
  override def unauthenticated()(
    implicit request: HttpServletRequest, 
    response: HttpServletResponse) 
  = {
    app.halt(403)
  }

  /** Authenticate user -- get the user key otherwise 400 response */
  def authenticate()(
    implicit request: HttpServletRequest,
    response: HttpServletResponse)
  : Option[SteamUserSession] = {
    var key = app.params.getOrElse(SteamOpenIDClient.IdentityKey, 
                                   app.halt(400))
    var session = SteamUserSession.fromClaimedId(key)  

    SessionsStore.sessions.insertOrIgnore(session)

    Some(session)
  }

  /** Delete the user session from server side after logging out */
  override def afterLogout(
    user: SteamUserSession)(
    implicit request: HttpServletRequest,
    response: HttpServletResponse) 
  = {
    SessionsStore.sessions.delete(user.id)
  }
}
