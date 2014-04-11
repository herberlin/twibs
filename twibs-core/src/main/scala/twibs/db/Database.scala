/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.db

import com.googlecode.flyway.core.Flyway
import concurrent.duration._
import javax.sql.DataSource
import org.apache.tomcat.jdbc.pool.{DataSource => TomcatDataSource, PoolProperties}
import scala.slick.jdbc.JdbcBackend.{Database => SlickDatabase}
import twibs.util.DynamicVariableWithDynamicDefault

trait Database {
  def password: String

  def username: String

  def url: String

  def driver: String

  def migrationLocations = "db/migration" :: Nil

  def withSession[R](func: => R): R = database.withDynSession(func)

  def withTransaction[R](func: => R): R = database.withDynTransaction(func)

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
    if (!migrationLocations.isEmpty) {
      val flyway = new Flyway()
      flyway.setInitOnMigrate(true)
      flyway.setLocations(migrationLocations: _*)
      flyway.setDataSource(dataSource)
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

object Database extends DynamicVariableWithDynamicDefault[Database](null)
