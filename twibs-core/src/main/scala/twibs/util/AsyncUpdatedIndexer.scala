package twibs.util

import org.apache.lucene.index._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

abstract class AsyncUpdatedIndexer extends Indexer {
  def waitForUpdateIndexDuration = 100 millis

  def indexReaderCachingDuration = 5 seconds

  private def updateIndex() = updateIndexCache.value

  private val updateIndexCache = {
    LazyCache(indexReaderCachingDuration) {
      try {
        Await.result(updateIndexAsync(), waitForUpdateIndexDuration)
      }
      catch {
        case e: TimeoutException => // ignored
      }
    }
  }

  private var updateFuture = future {}

  private def updateIndexAsync(): Future[Unit] = synchronized {
    updateFuture = updateFuture map {
      _ => write(update)
    }
    updateFuture
  }

  protected def update(indexWriter: IndexWriter): Unit

  override def read[T](func: (IndexReader) => T): T = {
    updateIndex()
    super.read(func)
  }

  updateIndex()
}
