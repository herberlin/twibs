/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.io.{File, FileOutputStream}
import java.net.URL
import com.google.common.io.ByteStreams
import org.apache.commons.io.FileUtils

object Predef extends ThreeTenTransition {
  type WithCloseMethod = {def close(): Unit}

  implicit def toRichClosable[C <: WithCloseMethod](closable: C) = new {
    def closeAfter[R](f: => R): R = useAndClose(Unit => f)

    def useAndClose[R](f: C => R): R = {
      var t: Throwable = null
      try f(closable) catch {
        case x: Throwable => t = x; throw x
      } finally try closable.close() catch {
        case x: Throwable => if (t != null) t.addSuppressed(x) else throw x
      }
    }
  }
}

object IOUtils {
  def isDirectory(url: URL) = Option(FileUtils.toFile(url)) match {
    case Some(file) => file.isDirectory
    case None => url.getFile.endsWith("/")
  }

  def downloadIfDoesNotExist(file: File, url: URL) = if (!file.exists()) copy(url, file) else file

  import Predef._

  def copy(url: URL, file: File) = {
    new FileOutputStream(file) useAndClose {
      os => url.openStream useAndClose {
        is => ByteStreams.copy(is, os)
      }
    }
    file
  }
}
