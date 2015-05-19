/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.net.URI

import com.google.common.io.Files

case class Path(parts: Seq[String], suffix: String, absolute: Boolean) {
  val string =
    if (parts.isEmpty) if (absolute) "/" else "./"
    else (if (absolute) "/" else "") + parts.mkString("/") + (suffix match {case "" | "/" => suffix case _ => "." + suffix})

  def tail = if (parts.size == 1) if (absolute) Path.root else Path.current else copy(parts.drop(1))

  def currentDir = if (isDir) this else parentDir

  def asDir = if (isDir) this else copy(suffix = "/")

  def isDir = suffix == "/"

  def isEmpty = parts.isEmpty

  def asRelative = copy(absolute = false)

  def asAbsolute = copy(absolute = true)

  def parentDir = copy(parts.dropRight(1), "/")

  def resolve(relativePath: Path) = Path(toURI.resolve(relativePath.toURI))

  def relativize(path: Path) = if (!absolute && path.absolute) path else Path(currentDir.toURI.relativize(path.toURI))

  def startsWith(path: Path) = path.isDir && path.absolute == absolute && currentDir.parts.startsWith(path.parts)

  def noSuffix = copy(suffix = "")

  def toURI = new URI(string)

  override def toString = string

  def ++(path: Path) = path.copy(currentDir.parts ++ path.parts, absolute = absolute)
}

object Path {
  val root = Path(Seq(), "/", absolute = true)
  val current = Path(Seq(), "/", absolute = false)

  implicit def apply(uri: URI): Path = apply(uri.toString)

  implicit def apply(pathString: String): Path = new URI(pathString).normalize().toString match {
    case "" | "." => current
    case "/" => root
    case p =>
      val suffix =
        if (pathString.endsWith("/")) "/"
        else Files.getFileExtension(p).toLowerCase
      Path(p.stripPrefix("/").stripSuffix("." + suffix).split("/"), suffix, p.startsWith("/"))
  }
}
