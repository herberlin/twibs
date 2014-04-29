/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import org.apache.lucene.index.{IndexNotFoundException, DirectoryReader, IndexWriter}
import scala.collection.JavaConverters._

trait IndexerWithLastModified extends Indexer {
  val LAST_COMMITTED_MILLIS = "last-committed-millis"

  override def write(func: (IndexWriter) => Any): Unit =
    super.write {
      indexWriter =>
        func(indexWriter)
        indexWriter.setCommitData(createCommitUserData())
    }

  //  def lastModified: DateTime = new DateTime(lastModifiedMillis)

  def lastModifiedMillis: Long = getLastCommitUserData.get(LAST_COMMITTED_MILLIS).fold(0L)(_.toLong)

  private def createCommitUserData(): java.util.Map[String, String] = createCommitUserData(System.currentTimeMillis).asJava

  private def getLastCommitUserData: Map[String, String] =
    try {
      DirectoryReader.listCommits(directory).asScala.toList.lastOption.fold(createCommitUserData(0))(_.getUserData.asScala.toMap)
    } catch {
      case e: IndexNotFoundException => createCommitUserData(0)
    }

  private def createCommitUserData(committed: Long): Map[String, String] = Map[String, String](LAST_COMMITTED_MILLIS -> committed.toString)
}
