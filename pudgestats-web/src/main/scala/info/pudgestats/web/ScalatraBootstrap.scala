import java.util.concurrent.{Executors, TimeUnit}

import org.slf4j.{Logger, LoggerFactory}

import org.scalatra._

import javax.servlet.ServletContext

import info.pudgestats.web.app._
import info.pudgestats.web.db.DatabaseInit
import info.pudgestats.web.conf.Config
import info.pudgestats.web.transport.RabbitMQConsumerSupport 
import info.pudgestats.web.{PudgeStatsPersister, 
  PudgeStatsLeaderboardGenerator}

/** Stackable Trait to deal log errors */
trait DefaultErrorHandling extends Runnable
{
  protected val logger: Logger

  abstract override def run = try {
    super.run
  } catch {
    case ex: Exception => this.logger.warn(s"Exception: $ex")
  }
}

/** Startup...
  * 
  * Configures the DB, maps routes, and initializes the
  * worker operations that generate content for the website
  */
class ScalatraBootstrap 
  extends LifeCycle 
  with DatabaseInit 
  with RabbitMQConsumerSupport
{
  val conf = new Config(System.getenv("PUDGE_WEB_CONF"))

  protected val queueName = conf.saveQueue

  protected val threadPool = Executors.newScheduledThreadPool(2)  

  private val logger = LoggerFactory.getLogger("info.pudgestats.web.bootstrap")

  override def init(context: ServletContext) = {
    val persister = new PudgeStatsPersister(this.consumer) 
      with DefaultErrorHandling
    val generator = new PudgeStatsLeaderboardGenerator(this.conf) 
      with DefaultErrorHandling

    context.initParameters(org.scalatra.EnvironmentKey) = System.getenv("PUDGE_ENV")

    this.logger.info(s"Started in ${context.initParameters(org.scalatra.EnvironmentKey)}")

    this.threadPool.scheduleAtFixedRate(generator, 0, 20, TimeUnit.MINUTES)
    this.threadPool.scheduleAtFixedRate(persister, 0, 3, TimeUnit.SECONDS)
    this.configureDb

    context.mount(new MainController(this.conf), "/*")
  }

  override def destroy(context: ServletContext) = {
    this.logger.info("Waiting for scheduled jobs...")

    this.threadPool.shutdown
    this.threadPool.awaitTermination(1, TimeUnit.MINUTES)

    this.logger.info("Closing RabbitMQ consumer...")

    this.consumer.close

    this.logger.info("Closing DB...")

    this.closeDbConnection
  }
}

