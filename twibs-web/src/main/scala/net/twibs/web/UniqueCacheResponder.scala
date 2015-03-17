/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import java.io._

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import net.twibs.util.Predef._
import net.twibs.util.{ContentRequest, Loggable, Request, RunMode}

import scala.collection.convert.wrapAsScala._

class UniqueCacheResponder(delegate: Responder, fileOption: Option[File] = None) extends LoadingCacheResponder(delegate) with Loggable {
  protected val cache: LoadingCache[ContentRequest, Option[Response]] = CacheBuilder.newBuilder().build(loader)

  private def loader = new CacheLoader[ContentRequest, Option[Response]]() {
    def load(requestCacheKey: ContentRequest): Option[Response] = delegate.respond(Request)
  }

  def useStorage: Boolean = RunMode.isPrivate

  def store(): Unit =
    fileOption.filter(file => useStorage).foreach(save)

  private[web] def save(file: File): Unit = {
    logger.info(s"Saving '${file.getAbsolutePath}'")
    try {
      new FileOutputStream(file) useAndClose { os => save(os)}
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

  private def asSerializableMap(): Map[ContentRequest, Response] =
    cache.asMap.filterNot(_._2.isEmpty).map(e => (e._1, e._2.get)).toMap

  def load(): Unit =
    fileOption.filter(file => useStorage && file.exists() && file.canRead && file.length > 0).foreach(load)

  private[web] def load(file: File): Unit = {
    logger.info(s"Loading '${file.getAbsolutePath}'")
    try {
      new FileInputStream(file) useAndClose { is => load(is)}
    } catch {
      case e: Exception =>
        if (logger.isDebugEnabled)
          logger.warn(s"Loading failed '${file.getAbsolutePath}'", e)
        else
          logger.warn(s"Loading failed '${file.getAbsolutePath}': ${e.getMessage}")
    }
  }

  private[web] def load(inputStream: InputStream): Unit =
    setSerializableMap(new ObjectInputStream(inputStream).readObject.asInstanceOf[Map[ContentRequest, Response]])

  private def setSerializableMap(map: Map[ContentRequest, Response]): Unit =
    map.foreach(x => cache.put(x._1, Some(x._2)))
}
