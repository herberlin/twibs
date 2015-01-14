/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

object SqlUtils {
  def escapeForLike(string: String) = string.replaceAll( """([%_\\])""", """\\$1""")
}
