package twibs.db

import twibs.TwibsTest

class QueryDslTest extends TwibsTest {
  val userTable = new Table("users") {
    val id = new LongColumn("id")
    val firstName = new StringColumn("first_name")
    val lastName = new StringColumn("last_name")
    val sort = new LongColumn("sort")
  }

  val newsTable = new Table("ns") {
    val id = new LongColumn("id")
    val userId = new LongColumn("user_id")
  }

  import QueryDsl._

  test("Select simple sql") {
    query(userTable.firstName, userTable.lastName).toSelectSql should be("SELECT users.first_name,users.last_name FROM users")
  }

  test("Select sql with where and order statement") {
    val q = query(userTable.firstName, userTable.lastName).where((userTable.id > 0L || userTable.id < 100L) && userTable.firstName === "Frank").orderBy(userTable.firstName asc)
    q.toSelectSql should be("SELECT users.first_name,users.last_name FROM users WHERE (users.id > ? OR users.id < ?) AND users.first_name = ? ORDER BY users.first_name ASC")
  }

  test("Check precedence sql statement") {
    val q = query(userTable.id).where((userTable.id > 0L || userTable.id < 100L) && userTable.id > 0L || userTable.id < 100L)
    q.toSelectSql should be("SELECT users.id FROM users WHERE (users.id > ? OR users.id < ?) AND users.id > ? OR users.id < ?")
  }

  test("Extend select") {
    val first = query(userTable.id).where((userTable.id > 0L || userTable.id < 100L) && userTable.id > 0L || userTable.id < 100L).orderBy(userTable.lastName desc)
    val both = first.also(query(userTable.firstName))

    both.toSelectSql should be("SELECT users.id,users.first_name FROM users WHERE (users.id > ? OR users.id < ?) AND users.id > ? OR users.id < ? ORDER BY users.last_name DESC")
    both.toInsertSql should be("INSERT INTO users(id,first_name) VALUES(?,?)")
  }

  test("Insert sql statement") {
    val q = query(userTable.firstName, userTable.lastName)
    q.toInsertSql should be("INSERT INTO users(first_name,last_name) VALUES(?,?)")
  }

  test("Delete sql statement") {
    val q = deleteFrom(userTable).where(userTable.firstName !== "Ike")
    q.toDeleteSql should be("DELETE FROM users WHERE users.first_name <> ?")
  }

  test("Group by sql statement") {
    val q = query(userTable.firstName, userTable.id.max).join(userTable.id, newsTable.userId).groupBy(userTable.firstName).offset(10).limit(20)
    q.toSelectSql should be("SELECT users.first_name,max(users.id) FROM users JOIN ns ON ns.user_id = users.id GROUP BY users.first_name OFFSET 10 LIMIT 20")
  }

  test("Empty order by") {
    val q = query(userTable.firstName).orderBy(Nil)
    q.toSelectSql should be("SELECT users.first_name FROM users")
  }

  test("Modifiy database") {
    Database.use(new MemoryDatabase()) {
      Database.withStaticTransaction { implicit connection =>
        userTable.size should be(3)
        query(userTable.lastName).where(userTable.firstName like "Frank").size should be(1)
        query(userTable.firstName).also(query(userTable.lastName)).insertAndReturn("Frank", "appa")(userTable.id) should be(4L)
        userTable.size should be(4)
        query(userTable.firstName).where(userTable.firstName like "Frank").size should be(2)
        query(userTable.firstName).where(userTable.firstName like "Frank").distinct.size should be(1)
        query(userTable.firstName, userTable.lastName).where(userTable.lastName === "appa").update("Dweezil", "Zappa") should be(1)
        query(userTable.sort).where(userTable.lastName === "Zappa").update(1L) should be(2)
        deleteFrom(userTable).where(userTable.firstName === "Dweezil").delete should be(1)
        query(userTable.lastName).where(userTable.firstName like "Frank").size should be(1)
        query(userTable.firstName).where(userTable.lastName === "Zappa").orderBy(userTable.sort asc).orderBy(userTable.firstName desc).select.map(_._1).toList should be(List("Frank"))
        query(userTable.firstName).orderBy(userTable.sort desc).select.map(_._1).toList should be(List("Tommy", "Ike", "Frank"))
        deleteFrom(userTable).delete should be(3)
        userTable.size should be(0)
      }
    }
  }
}
