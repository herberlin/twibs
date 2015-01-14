/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.db

import java.sql.{Connection, SQLException}
import javax.sql.DataSource

import net.twibs.util.{DynamicVariableWithDefault, DynamicVariableWithDynamicDefault}
import net.twibs.util.Predef._
import org.apache.tomcat.jdbc.pool.{PoolProperties, DataSource => TomcatDataSource}
import org.flywaydb.core.Flyway

import scala.concurrent.duration._

trait Database {
  def password: String

  def username: String

  def url: String

  def driver: String

  def migrationLocations = "db/migration" :: Nil

  def withTransaction[R](func: => R): R =
    dataSource.getConnection.useAndClose { conn =>
      try {
        conn.setAutoCommit(false)
        try {
          var done = false
          try {
            val ret = Database.useConnection(conn)(func)
            conn.commit()
            done = true
            ret
          } finally
            if (!done) conn.rollback()
        } finally {
          conn.setAutoCommit(true)
        }
      }
    }

  def withSavepoint[T](f: => T): T = {
    var ok = false
    val connection = Database.connection
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

  def init(): Unit = dataSource

  lazy val dataSource: DataSource = {
    val ret = createDataSource()
    migrate(ret)
    ret
  }

  protected def migrate(ds: DataSource): Unit =
    if (migrationLocations.nonEmpty) {
      val flyway = new Flyway()
      flyway.setInitOnMigrate(true)
      flyway.setLocations(migrationLocations: _*)
      flyway.setDataSource(ds)
      flyway.setValidateOnMigrate(false) //(RunMode.isProduction || RunMode.isStaging)
      flyway.migrate()
    }

  def commit(): Unit = Database.connection.commit()

  def rollback(): Unit = Database.connection.rollback()

  def close(): Unit = {
    dataSource.asInstanceOf[TomcatDataSource].close()
    Database.deactivate(this)
  }
}

object Database extends DynamicVariableWithDynamicDefault[Database] {
  override def createFallback: Database = null

  private val connectionVar = new DynamicVariableWithDefault[Connection] {
    override def default: Connection = throw new SQLException("No active connection")
  }

  def useConnection[R](c: Connection)(func: => R): R = connectionVar.use(c)(func)

  def connection: Connection = connectionVar.current
}
