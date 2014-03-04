package twibs.util

import com.google.common.base.CharMatcher
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.de.GermanAnalyzer
import org.apache.lucene.analysis.util.CharArraySet
import org.apache.lucene.index._
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search._
import org.apache.lucene.store.{AlreadyClosedException, RAMDirectory, Directory}
import org.apache.lucene.util.Version

class Indexer extends Loggable {
  val directory: Directory = new RAMDirectory()

  val analyzer: Analyzer = new GermanAnalyzer(Version.LUCENE_46, CharArraySet.EMPTY_SET, CharArraySet.EMPTY_SET)

  private var indexWriter = openIndexWriter
  indexWriter.commit()

  def write(func: (IndexWriter) => Any): Unit = {
    try {
      func(indexWriter)
    }
    catch {
      case t: Throwable =>
        logger.warn("Updating index failed", t)
        indexWriter.rollback()
        throw t
    }
    try {
      indexWriter.commit()
    }
    catch {
      case e: AlreadyClosedException => indexWriter = openIndexWriter
    }
    invalidateIndexReader()
  }

  def openIndexWriter: IndexWriter = new IndexWriter(directory, indexWriterConfig)

  def indexWriterConfig = new IndexWriterConfig(Version.LUCENE_46, analyzer)

  def invalidateIndexReader(): Unit = synchronized {
    indexReaderCache.valueOption.map(_.decRef)
    indexReaderCache.reset()
  }

  def search[T](query: Query, numberOfDocs: Int = 10000)(func: (TopDocs) => T): T = search {
    indexSearcher => func(indexSearcher.search(query, numberOfDocs))
  }

  def search[T](func: (IndexSearcher) => T): T = read {
    indexReader => func(new IndexSearcher(indexReader))
  }

  def read[T](func: (IndexReader) => T): T = {
    val indexReader = openIndexReaderAndIncRef
    try {
      func(indexReader)
    } finally {
      indexReader.decRef()
    }
  }

  private def openIndexReaderAndIncRef: IndexReader = synchronized {
    val ret = indexReaderCache.value
    ret.incRef()
    ret
  }

  private val indexReaderCache = LazyCache(openIndexReader)

  def openIndexReader: DirectoryReader = DirectoryReader.open(directory)

  def createQuery(queryString: String, fields: Seq[String]): Query =
    createQuery(queryString, fields, fields.map(_ => BooleanClause.Occur.SHOULD))

  def createQuery(queryString: String, fields: Seq[String], flags: Seq[BooleanClause.Occur]): Query = {
    require(fields.length == flags.length, "fields.length != flags.length")

    val wildcardMatcher: CharMatcher = CharMatcher.anyOf("*?")
    def removeLeadingWildcards(string: String) = wildcardMatcher.trimLeadingFrom(string)

    val ret = new BooleanQuery()
    for (text <- QuotedStringSplitter.splitStringRespectQuotes(removeLeadingWildcards(queryString))) {
      for (i <- 0 to fields.length - 1) {
        val queryParser = new QueryParser(Version.LUCENE_46, fields(i), analyzer)
        val ored = new BooleanQuery()
        ored.add(queryParser.parse(text), BooleanClause.Occur.SHOULD)
        val query = queryParser.parse("\"" + text + "\"")
        query.setBoost(2)
        ored.add(query, BooleanClause.Occur.SHOULD)
        ored.add(new TermQuery(new Term(fields(i), text)), BooleanClause.Occur.SHOULD)
        ret.add(ored, flags(i))
      }
    }
    ret
  }
}
