/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

import com.google.common.cache.{CacheLoader, CacheBuilder, LoadingCache}
import java.io._
import scala.collection.JavaConverters._
import twibs.util.Predef._
import twibs.util.{RunMode, Loggable}

class UniqueCacheResponder(delegate: Responder, fileOption: Option[File] = None) extends LoadingCacheResponder(delegate) with Loggable {
  protected val cache: LoadingCache[RequestCacheKey, Option[Response]] = CacheBuilder.newBuilder().build(loader)

  private def loader = new CacheLoader[RequestCacheKey, Option[Response]]() {
    def load(requestCacheKey: RequestCacheKey): Option[Response] = delegate.respond(Request)
  }

  def useStorage: Boolean = RunMode.isDevelopment

  def store(): Unit =
    fileOption.filter(file => useStorage).foreach(save)

  private[web] def save(file: File): Unit = {
    logger.info(s"Saving '${file.getAbsolutePath}'")
    try {
      new FileOutputStream(file) useAndClose {os => save(os)}
    } catch {
      case e: Exception =>
        if (logger.isDebugEnabled)
          logger.debug(s"Saving failed '${file.getAbsolutePath}'", e)
        else
          logger.warn(s"Saving failed '${file.getAbsolutePath}': ${e.getMessage}")
    }
  }

  private[web] def save(outputStream: OutputStream): Unit = {
    val oos = new ObjectOutputStream(outputStream)
    oos.writeObject(asSerializableMap())
    oos.flush()
  }

  private def asSerializableMap(): Map[RequestCacheKey, Response] =
    cache.asMap.asScala.filterNot(_._2.isEmpty).map(e => (e._1, e._2.get)).toMap

  def load(): Unit =
    fileOption.filter(file => useStorage && file.exists() && file.canRead && file.length > 0).foreach(load)

  private[web] def load(file: File): Unit = {
    logger.info(s"Loading '${file.getAbsolutePath}'")
    try {
      new FileInputStream(file) useAndClose {is => load(is)}
    } catch {
      case e: Exception =>
        if (logger.isDebugEnabled)
          logger.warn(s"Loading failed '${file.getAbsolutePath}'", e)
        else
          logger.warn(s"Loading failed '${file.getAbsolutePath}': ${e.getMessage}")
    }
  }

  private[web] def load(inputStream: InputStream): Unit =
    setSerializableMap(new ObjectInputStream(inputStream).readObject.asInstanceOf[Map[RequestCacheKey, Response]])

  private def setSerializableMap(map: Map[RequestCacheKey, Response]): Unit =
    map.foreach(x => cache.put(x._1, Some(x._2)))
}
