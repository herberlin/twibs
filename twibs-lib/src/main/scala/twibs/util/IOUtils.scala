package twibs.util

import java.net.URL
import org.apache.commons.io.FileUtils

object IOUtils {
  def using[T, C <: {def close() : Unit}](closable: C)(f: C => T): T = try f(closable) finally {closable.close()}

  def isDirectory(url: URL) = Option(FileUtils.toFile(url)) match {
    case Some(file) => file.isDirectory
    case None => url.getFile.endsWith("/")
  }
}
