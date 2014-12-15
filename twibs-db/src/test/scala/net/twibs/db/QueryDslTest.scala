package net.twibs.db

import net.twibs.testutil.TwibsTest

class QueryDslTest extends TwibsTest {
  val userTable = new Table("users") {
    val id = new LongColumn("id")
    val firstName = new StringColumn("first_name")
    val lastName = new StringColumn("last_name")
    val sort = new LongColumn("sort")
    val email = new StringOptionColumn("email")
  }

  val newsTable = new Table("ns") {
    val id = new LongColumn("news_id")
    val userId = new LongColumn("user_id")
    val title = new StringColumn("title")
  }

  val newsDetail = new Table("nd") {
    val id = new LongColumn("news_detail_id")
    val newsId =  new LongColumn("news_id")
    val detail = new StringColumn("detail")
  }

  import QueryDsl._

  test("Select simple sql") {
    query(userTable.firstName, userTable.lastName).toSelectSql should be("SELECT users.first_name,users.last_name FROM users")
  }

  test("Test query with none") {
    query(userTable.firstName, userTable.lastName).where(userTable.email === None).toSelectSql should be("SELECT users.first_name,users.last_name FROM users WHERE users.email IS NULL")
    query(userTable.firstName, userTable.lastName).where(userTable.email === Some("a@example.com")).toSelectSql should be("SELECT users.first_name,users.last_name FROM users WHERE users.email = ?")
  }

  test("Query is not null") {
    query(userTable.firstName).also(query(userTable.lastName)).where(userTable.email.isNotNull).where(userTable.firstName =!= "Zappa").toSelectSql should be("SELECT users.first_name,users.last_name FROM users WHERE users.email IS NOT NULL AND users.first_name <> ?")
  }

  test("Test not") {
    query(userTable.firstName).where(userTable.email =!= Some("a") && userTable.firstName === "").toSelectSql should be("SELECT users.first_name FROM users WHERE users.email <> ? AND users.first_name = ?")
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
    val q = deleteFrom(userTable).where(userTable.firstName =!= "Ike")
    q.toDeleteSql should be("DELETE FROM users WHERE users.first_name <> ?")
  }

  test("Group by sql statement") {
    val q = query(userTable.firstName, userTable.id.max, newsDetail.detail).join(userTable.id, newsTable.userId).join(newsTable.id, newsDetail.newsId).groupBy(userTable.firstName).offset(10).limit(20).where(userTable.firstName =!= "Frank")
    q.toSelectSql should be("SELECT users.first_name,max(users.id),nd.detail FROM users JOIN ns ON users.id = ns.user_id JOIN nd ON ns.news_id = nd.news_id WHERE users.first_name <> ? GROUP BY users.first_name OFFSET 10 LIMIT 20")
  }

  test("Join with 'also'") {
    val l = new JoinList(Seq())
      .add(userTable.id, newsTable.userId)
      .add(userTable.firstName, newsTable.title)
      .add(userTable.id, newsTable.userId) // Multiple times to proof distinct
      .add(newsTable.id, newsDetail.newsId)
    l.tables should be(Seq(newsDetail, newsTable, userTable))
    l.toJoinSql should be("users JOIN ns ON users.first_name = ns.title AND users.id = ns.user_id JOIN nd ON ns.news_id = nd.news_id")

    val q1 = query(userTable.firstName, newsTable.id).join(userTable.id, newsTable.userId)
    q1.toSelectSql should be("SELECT users.first_name,ns.news_id FROM users JOIN ns ON users.id = ns.user_id")

    val q2 = query(newsTable.title, newsDetail.detail).join(newsTable.id, newsDetail.newsId)
    q2.toSelectSql should be("SELECT ns.title,nd.detail FROM ns JOIN nd ON ns.news_id = nd.news_id")

    q1.also(q2).toSelectSql should be("SELECT users.first_name,ns.news_id,ns.title,nd.detail FROM users JOIN ns ON users.id = ns.user_id JOIN nd ON ns.news_id = nd.news_id")
    q2.also(q1).toSelectSql should be("SELECT ns.title,nd.detail,users.first_name,ns.news_id FROM users JOIN ns ON users.id = ns.user_id JOIN nd ON ns.news_id = nd.news_id")
  }

  test("Empty order by") {
    val q = query(userTable.firstName).orderBy(Nil)
    q.toSelectSql should be("SELECT users.first_name FROM users")
  }

  test("Modifiy database") {
    Database.use(new MemoryDatabase()) {
      Database.withTransaction {
        implicit def connection = Database.connection
        userTable.size should be(3)
        query(userTable.lastName).where(userTable.firstName like "Frank").size should be(0)
        query(userTable.lastName).where(userTable.firstName like "frank").size should be(1)
        query(userTable.firstName).also(query(userTable.lastName)).returning(userTable.id).insert("Frank", "appa") should be(4L)
        userTable.size should be(4)
        query(userTable.firstName).where(userTable.firstName like "frank").size should be(2)
        query(userTable.firstName).where(userTable.firstName like "frank").distinct.size should be(1)
        query(userTable.firstName, userTable.lastName).where(userTable.lastName === "appa").update("Dweezil", "Zappa") should be(1)
        query(userTable.sort).where(userTable.lastName === "Zappa").update(1L) should be(2)
        deleteFrom(userTable).where(userTable.firstName === "Dweezil").delete should be(1)
        query(userTable.lastName).where(userTable.firstName like "frank").size should be(1)
        query(userTable.firstName, userTable.lastName).convert(User.tupled,User.unapply).where(userTable.id like "%1%").size should be(1)
        query(userTable.firstName).where(userTable.lastName === "Zappa").orderBy(userTable.sort asc).orderBy(userTable.firstName desc).select.map(_._1).toList should be(List("Frank"))
        query(userTable.firstName).orderBy(userTable.sort desc).select.map(_._1).toList should be(List("Tommy", "Ike", "Frank"))
        query(userTable.firstName, userTable.lastName).convert(User.tupled,User.unapply).where(userTable.firstName === "Tommy").first.lastName should be ("Mars")
        deleteFrom(userTable).delete should be(3)
        userTable.size should be(0)
      }
    }
  }

  case class User(firstName: String, lastName: String)
}
