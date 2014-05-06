/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import java.sql._
import org.scalatest.BeforeAndAfterAll
import scala.slick.jdbc.GetResult
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import slick.jdbc.StaticQuery.interpolation
import twibs.TwibsTest
import twibs.db.Database

class DatabaseTest extends TwibsTest with BeforeAndAfterAll {
  var database: Database = null

  override def beforeAll(): Unit =
    database = new MemoryDatabase() {
      override def migrationLocations: List[String] = "memory-db/migration" :: Nil
    }

  override def afterAll(): Unit =
    database.close()

  test("Create Table") {
    database.withSession(sqlu"CREATE TABLE t ( s VARCHAR(200), i BIGINT, d DATE)".first) should be(0)
  }

  test("Insert data") {
    database.withSession(sqlu"INSERT INTO t (s, i, d) VALUES ('test string', 1234, '2012-12-21')".first) should be(1)
  }

  test("Get list value") {
    implicit val r = GetResult(r => (r.nextString(), r.nextInt(), r.nextDate()))
    database.withSession {
      val list = sql"SELECT * FROM t".as[(String, Int, Date)].list
      list should equal(List(("test string", 1234, Date.valueOf("2012-12-21"))))
    }
  }
}
