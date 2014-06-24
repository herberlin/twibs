/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.db

import concurrent.duration._
import java.sql.Connection
import javax.sql.DataSource
import org.apache.tomcat.jdbc.pool.{DataSource => TomcatDataSource, PoolProperties}
import org.flywaydb.core.Flyway
import scala.slick.jdbc.JdbcBackend.{Database => SlickDatabase}
import twibs.util.{RunMode, DynamicVariableWithDynamicDefault}

trait Database {
  def password: String

  def username: String

  def url: String

  def driver: String

  def migrationLocations = "db/migration" :: Nil

  def withTransaction[R](func: => R): R = database.withDynTransaction(func)

  def withStaticTransaction[R](func: (Connection) => R): R = database.withTransaction(session => func(session.conn))

  def withSavepoint[T](f: => T): T = {
    var ok = false
    val connection = SlickDatabase.dynamicSession.conn
    val savePoint = connection.setSavepoint()
    try {
      val ret = f
      ok = true
      ret
    } finally {
      if (ok) {
        connection.releaseSavepoint(savePoint)
      } else {
        connection.rollback(savePoint)
      }
    }
  }

  def createDataSource(): TomcatDataSource = {
    val properties = new PoolProperties()
    properties.setUrl(url)
    properties.setDriverClassName(driver)
    properties.setUsername(username)
    properties.setPassword(password)
    properties.setMaxActive(400)
    properties.setTestOnBorrow(true)
    properties.setValidationInterval((1 seconds).toMillis)
    properties.setValidationQuery("SELECT 1")

    val ret = new TomcatDataSource()
    ret.setPoolProperties(properties)
    ret
  }

  private lazy val database: SlickDatabase = {
    migrate()
    SlickDatabase.forDataSource(dataSource)
  }

  def migrate(): Unit =
    if (migrationLocations.nonEmpty) {
      val flyway = new Flyway()
      flyway.setInitOnMigrate(true)
      flyway.setLocations(migrationLocations: _*)
      flyway.setDataSource(dataSource)
      flyway.setValidateOnMigrate(RunMode.isProduction || RunMode.isStaging)
      flyway.migrate()
    }

  lazy val dataSource: DataSource = createDataSource()

  def commit(): Unit = SlickDatabase.dynamicSession.conn.commit()

  def rollback(): Unit = SlickDatabase.dynamicSession.rollback()

  def init(): Unit = database

  def close(): Unit = {
    dataSource.asInstanceOf[TomcatDataSource].close()
    Database.deactivate(this)
  }
}

object Database extends DynamicVariableWithDynamicDefault[Database](null) {
  implicit def connection: Connection = SlickDatabase.dynamicSession.conn
}
