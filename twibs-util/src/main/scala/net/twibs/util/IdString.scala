/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

case class IdString(string: String) {
  def ~(add: String) = (string, add) match {
    case ("", s) => IdString(s)
    case (s, "") => this
    case (s, a) => IdString(s + "_" + a)
  }

  def +(add: String) = IdString(string + add)

  def toCssId = "#" + string
}

object IdString {
  implicit def wrap(string: String): IdString = IdString(string)

  implicit def unwrap(is: IdString): String = is.string
}
