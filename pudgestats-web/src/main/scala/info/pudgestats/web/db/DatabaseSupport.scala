package info.pudgestats.web.db

import com.mchange.v2.c3p0.ComboPooledDataSource

import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.{Session, SessionFactory}

import org.scalatra.ScalatraBase

import info.pudgestats.web.conf.DbConfig

object DatabaseSessionSupport 
{
  def key = {
    val n = getClass.getName
    if (n.endsWith("$")) n.dropRight(1) else n
  }
}

trait DatabaseSessionSupport 
{ 
  this: ScalatraBase =>

  import DatabaseSessionSupport._

  def dbSession = request.get(key).orNull.asInstanceOf[Session]

  before() {
    request(key) = SessionFactory.newSession
    this.dbSession.bindToCurrentThread
  }

  after() {
    this.dbSession.close
    this.dbSession.unbindFromCurrentThread
  }
}

trait DatabaseInit 
{ 
  def conf: DbConfig

  val cpds = new ComboPooledDataSource
  lazy val dbConn = s"jdbc:postgresql://${this.conf.dbHost}/${this.conf.dbName}"

  def configureDb = {
    this.cpds.setDriverClass("org.postgresql.Driver")
    this.cpds.setJdbcUrl(this.dbConn)
    this.cpds.setUser(this.conf.dbUser)
    this.cpds.setPassword(this.conf.dbPass)

    this.cpds.setMinPoolSize(1)
    this.cpds.setAcquireIncrement(1)
    this.cpds.setMaxPoolSize(50)

    SessionFactory.concreteFactory = Some(() => {
      Session.create(cpds.getConnection, new PostgreSqlAdapter) 
    })
  }

  def closeDbConnection = this.cpds.close
}
