package info.pudgestats.web.auth

import org.squeryl.PrimitiveTypeMode._

import org.scalatra.ScalatraBase
import org.scalatra.auth.{ScentryConfig, ScentrySupport, ScentryAuthStore}

import info.pudgestats.web.model.{SessionsStore, SteamUserSession}

/** Provides authentication support for a Steam User 
  * with cookies.
  */
trait AuthenticationSupport 
  extends ScalatraBase 
  with ScentrySupport[SteamUserSession] 
{ 
  self: ScalatraBase =>

  protected def fromSession = { 
    case id: String => try {
      SessionsStore.sessions.lookup(id).get
    } catch {
      case ex: Throwable => null
    }
  }
  protected def toSession = { 
    case s: SteamUserSession => s.id.toString
  }

  override def configureScentry = {
    scentry.store = new ScentryAuthStore.CookieAuthStore(this)
  }

  protected val scentryConfig = (new ScentryConfig { 
  }).asInstanceOf[ScentryConfiguration]

  override protected def registerAuthStrategies = {
    scentry.register("SteamAuth", app => new SteamAuthStrategy(app))
  }
}
