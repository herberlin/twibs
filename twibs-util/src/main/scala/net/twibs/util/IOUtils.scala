/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.io.{InputStream, InputStreamReader, File, FileOutputStream}
import java.net.URL
import java.nio.charset.StandardCharsets

import com.google.common.io.{CharStreams, ByteStreams}
import org.apache.commons.io.FileUtils

import scala.util.DynamicVariable

object Predef extends ThreeTenTransition with IOUtils

trait IOUtils {
  type WithCloseMethod = {def close(): Unit}

  class RichClosable[C](closable: C, close: () => Unit) {
    def closeAfter[R](f: => R): R = useAndClose(Unit => f)

    def useAndClose[R](f: C => R): R = {
      var t: Throwable = null
      try f(closable) catch {
        case x: Throwable => t = x; throw x
      } finally try close() catch {
        case x: Throwable => if (t != null) t.addSuppressed(x) else throw x
      }
    }
  }

  implicit def toRichClosable[C <: AutoCloseable](closable: C) =
    new RichClosable(closable, () => closable.close())

  import scala.language.reflectiveCalls

  implicit def toRichClosableReflected[C <: WithCloseMethod](closable: C) =
    new RichClosable(closable, () => closable.close())

  implicit def toRichInputStream(is: InputStream) = new {
    def readAsString = is.useAndClose { is => CharStreams.toString(new InputStreamReader(is, StandardCharsets.UTF_8))}

    def readAsBytes = is.useAndClose { is => ByteStreams.toByteArray(is)}
  }
}

object IOUtils extends IOUtils {
  def isDirectory(url: URL) = Option(FileUtils.toFile(url)) match {
    case Some(file) => file.isDirectory
    case None => url.getFile.endsWith("/")
  }

  def downloadIfDoesNotExist(file: File, url: URL) = if (!file.exists()) copy(url, file) else file

  def copy(url: URL, file: File) = {
    new FileOutputStream(file) useAndClose {
      os => url.openStream useAndClose {
        is => ByteStreams.copy(is, os)
      }
    }
    file
  }
}

class UnwrapableDynamicVariable[T](init: T) extends DynamicVariable[T](init: T) {
  @inline implicit def unwrap(companion: this.type): T = value
}

trait UnwrapCurrent[T] {
  @inline implicit def unwrap(companion: this.type): T = current

  def current: T
}
