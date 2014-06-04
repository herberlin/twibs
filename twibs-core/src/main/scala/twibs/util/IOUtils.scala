/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import java.net.URL
import org.apache.commons.io.FileUtils

object IOUtils {
  def isDirectory(url: URL) = Option(FileUtils.toFile(url)) match {
    case Some(file) => file.isDirectory
    case None => url.getFile.endsWith("/")
  }
}

object Predef {
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
