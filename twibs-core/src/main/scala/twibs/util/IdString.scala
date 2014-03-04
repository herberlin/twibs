package twibs.util

case class IdString(string: String) {
  def +(add: String) = IdString(string + add)

  def toCssId = "#" + string
}

object IdString {
  implicit def wrap(string: String): IdString = IdString(string)

  implicit def unwrap(is: IdString): String = is.string
}
