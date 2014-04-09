/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import java.sql._
import org.scalatest.BeforeAndAfterAll
import scala.slick.jdbc.GetResult
import slick.jdbc.StaticQuery.interpolation
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import twibs.TwibsTest
import twibs.util.Predef._
import twibs.db.Database

class DatabaseTest extends TwibsTest with BeforeAndAfterAll {
  var database: Database = null

  val search = "Michael"

  override def beforeAll(): Unit =
    database = new MemoryDatabase()

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
      val list = sql"SELECT * FROM t".as[(String, Int, Date)].list()
      list should equal(List(("test string", 1234, Date.valueOf("2012-12-21"))))
    }
  }

  test("Direct") {
    new GwbiDatabase() useAndClose {
      _.withSession {
        sql"SELECT count(*) FROM user_".as[(Int)].first should be > 78206
        sql"SELECT firstname, lastname, emailaddress, screenname FROM user_ WHERE firstname LIKE $search LIMIT 10 OFFSET 5".as(convert).list() should have size 10
      }
    }
  }

  test("Check email addresses") {
    val l = new GwbiDatabase() useAndClose {
      _.withSession(sql"SELECT emailaddress FROM user_".as[String].list().filterNot(EmailUtils.isValidEmailAddress))
    }
    l should have size 4
  }

  def convert = GetResult(r => (r.nextString(), r.nextString(), r.nextString(), r.nextString()))
}
