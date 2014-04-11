/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

object Parsers {
  implicit def enrichWithNumberParsers(string: String) = new {
    private def nfe[T](t: => T) =
      try Some(t) catch {
        case e: NumberFormatException => None
      }

    def toLongOption: Option[Long] = nfe(string.toLong)

    def toLongWithDefault(default: Long) = toLongOption getOrElse default

    def toIntOption: Option[Int] = nfe(string.toInt)

    def toIntWithDefault(default: Int) = toIntOption getOrElse default

    def toShortOption: Option[Short] = nfe(string.toShort)

    def toShortWithDefault(default: Short) = toShortOption getOrElse default

    def toByteOption: Option[Byte] = nfe(string.toByte)

    def toByteWithDefault(default: Byte) = toByteOption getOrElse default

    def toDoubleOption: Option[Double] = nfe(string.toDouble)

    def toDoubleWithDefault(default: Double) = toDoubleOption getOrElse default

    def toFloatOption: Option[Float] = nfe(string.toFloat)

    def toFloatWithDefault(default: Float) = toFloatOption getOrElse default
  }
}
