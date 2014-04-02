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
  type Closable = {def close(): Unit}

  implicit def toRichClosable[C <: Closable](closable: C) = new {
    def useAndClose[T](f: (C) => T): T = try f(closable) finally {closable.close()}

    def closeAfter[T](f: => T): T = try f finally {closable.close()}
  }
}
