/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.io.InputStream

trait Upload {
  val id: String = IdGenerator.next()

  def name: String

  def size: Long

  def stream: InputStream

  def mimeTypeString: String = ApplicationSettings.tika.detect(stream, name)

  def sizeAsHumanReadableString = Formatters.getHumanReadableByteCountSi(size)
}
