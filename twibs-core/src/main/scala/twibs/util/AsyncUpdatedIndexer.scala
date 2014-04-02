/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import org.apache.lucene.index._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

abstract class AsyncUpdatedIndexer extends Indexer {
  def indexReaderCachingDuration = 1 hour

  def recalculate() = calculateIndexCache.reset()

  private def recalculateIndex() = calculateIndexCache.value

  private val calculateIndexCache = LazyCache(indexReaderCachingDuration) {calculateIndexAsync()}

  private var updateFuture = future {}

  private def calculateIndexAsync(): Unit = synchronized {
    updateFuture = updateFuture map {_ => write(update)}
  }

  protected def update(indexWriter: IndexWriter): Unit

  override def read[T](func: (IndexReader) => T): T = {
    recalculateIndex()
    super.read(func)
  }

  recalculateIndex()
}
