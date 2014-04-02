/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

object SqlUtils {
  def escapeForLike(string: String) = string.replaceAll( """([%_\\])""", """\\$1""")
}
