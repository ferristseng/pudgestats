package info.pudgestats.web.app

import org.scalatra.{ScalatraServlet, Route, MethodOverride}
import org.scalatra.scalate.{ScalateSupport, 
  ScalateUrlGeneratorSupport}

import org.fusesource.scalate.{TemplateEngine, Binding}
import org.fusesource.scalate.layout.DefaultLayoutStrategy

import javax.servlet.http.HttpServletRequest

import scala.collection.mutable.Map

import info.pudgestats.web.db.DatabaseSessionSupport
import info.pudgestats.web.auth.AuthenticationSupport

/** Default Stack for Web Application */
trait DefaultStack 
  extends ScalatraServlet 
  with ScalateSupport
  with ScalateUrlGeneratorSupport
  with AuthenticationSupport
  with DatabaseSessionSupport
{
  override def isScalateErrorPageEnabled = false

  override protected def defaultTemplatePath: List[String] = List("/WEB-INF/templates/views")

  override protected def createTemplateEngine(config: ConfigT) = {
    val engine = super.createTemplateEngine(config)
  
    engine.layoutStrategy = new DefaultLayoutStrategy(engine,
      TemplateEngine.templateTypes.map("/WEB-INF/templates/layouts/default." + _): _*)
    engine.packagePrefix = "templates"
    engine.allowReload = false 
    engine.allowCaching = !this.isDevelopmentMode 

    engine
  }
  
  override protected def templateAttributes(
    implicit request: HttpServletRequest)
  : Map[String, Any] = {
    super.templateAttributes + ("session" -> scentry.userOption)
  }

  notFound {
    serveStaticResource().getOrElse {
      contentType = "text/html"

      ssp("/error/error", "errorCode" -> 404, "errorDesc" -> Some("Not Found"))
    }
  }

  error {
    case ex: Throwable => {
      contentType = "text/html"

      ssp("/error/error", "errorCode" -> 500, "errorDesc" -> Some(ex.toString))
    }
  }

  methodNotAllowed {
    case _ => {
      contentType = "text/html"

      ssp("/error/error", "errorCode" -> 405, "errorDesc" -> Some("Method Not Found"))
    }
  }
}
