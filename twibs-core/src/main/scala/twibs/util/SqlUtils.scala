package twibs.util

object SqlUtils {
  def escapeForLike(string: String) = string.replaceAll( """([%_\\])""", """\\$1""")
}
