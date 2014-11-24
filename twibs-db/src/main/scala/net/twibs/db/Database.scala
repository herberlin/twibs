/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.db

import java.sql.Connection
import javax.sql.DataSource

import scala.concurrent.duration._
import scala.slick.jdbc.JdbcBackend.{Database => SlickDatabase}

import net.twibs.util.DynamicVariableWithDynamicDefault

import org.apache.tomcat.jdbc.pool.{PoolProperties, DataSource => TomcatDataSource}
import org.flywaydb.core.Flyway

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

  private lazy val database: SlickDatabase = synchronized {
    migrate()
    SlickDatabase.forDataSource(dataSource)
  }

  def migrate(): Unit =
    if (migrationLocations.nonEmpty) {
      val flyway = new Flyway()
      flyway.setInitOnMigrate(true)
      flyway.setLocations(migrationLocations: _*)
      flyway.setDataSource(dataSource)
      flyway.setValidateOnMigrate(false) //(RunMode.isProduction || RunMode.isStaging)
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

object Database extends DynamicVariableWithDynamicDefault[Database] {
  override def createFallback: Database = null

  implicit def implicitConnection: Connection = SlickDatabase.dynamicSession.conn

  def connection: Connection = SlickDatabase.dynamicSession.conn
}