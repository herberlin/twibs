package twibs.util

import org.apache.lucene.index.IndexWriter
import org.apache.lucene.search.MatchAllDocsQuery
import org.scalatest.BeforeAndAfterEach
import twibs.TwibsTest

class IndexerTest extends TwibsTest with BeforeAndAfterEach {
  val ike = new TestIndexable("ike", "Ike", "Willis", "willi@example.com")
  val tommy = new TestIndexable("tommy", "Tommy", "Mars", "tommy@example.com")
  val terry = new TestIndexable("frank", "Terry", "Bozzio", "terry@example.com")

  var indexer: Indexer = null

  override def beforeEach(): Unit =
    indexer = new Indexer()

  test("Test index writer update method can rollback") {
    new AsyncUpdatedIndexer() {
      protected override def update(indexWriter: IndexWriter): Unit = {
        indexWriter.rollback()
      }
    }.search(searcher => Unit)
  }

  test("Empty indexer") {
    allTopDocs.scoreDocs should have size 0
  }

  test("Insert two items") {
    indexer.write(ike.insert)
    indexer.write(ike.insert)

    allTopDocs.scoreDocs should have size 2
  }

  test("Insert two and remove one item") {
    indexer.write(ike.insert)
    indexer.write(tommy.insert)
    indexer.write(ike.delete)

    allTopDocs.scoreDocs should have size 1
  }

  test("Insert two items with the same key and remove one should remove both") {
    indexer.write(ike.insert)
    indexer.write(new TestIndexable("ike", "Tommy", "Mars", "mars@example.com").insert)
    indexer.write(ike.delete)

    allTopDocs.scoreDocs should have size 0
  }

  test("Insert and find") {
    indexer.write(ike.insert)
    indexer.write(tommy.insert)
    indexer.write(terry.insert)

    query("T").scoreDocs should have size 0
    query("Mars").scoreDocs should have size 1
    query("Tommy").scoreDocs should have size 1
    query("Tommy Ike").scoreDocs should have size 2
    query("tommy@example.com").scoreDocs should have size 1
    query("tommy@*").scoreDocs should have size 1
    query("*tommy@*").scoreDocs should have size 1
    query("?tommy@*").scoreDocs should have size 1
  }

  test("Indexer with last modified") {
    val idx = new Indexer with IndexerWithLastModified

    idx.lastModifiedMillis should be(0L)
    val x = System.currentTimeMillis
    idx.write(iw => Unit)
    idx.lastModifiedMillis should be >= x
  }

  def query(queryString: String) = {
    val query = indexer.createQuery(queryString, Array("lastName", "firstName", "emailAddress"))
    indexer.search(query) {
      topDocs => topDocs
    }
  }

  def allTopDocs = indexer.search(new MatchAllDocsQuery) {
    topDocs => topDocs
  }
}
