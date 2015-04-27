/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

case class Parameters(parameterMap: Map[String, Seq[String]] = Map()) extends Serializable {
  def getBoolean(key: String, default: Boolean) = getBooleanOption(key) getOrElse default

  def getBooleanOption(key: String) = getFormatted(key, _.equals("true"))

  def getInt(key: String, default: Int) = getIntOption(key) getOrElse default

  def getIntOption(key: String) = getFormatted(key, _.toInt)

  def getLong(key: String, default: Long) = getLongOption(key) getOrElse default

  def getLongOption(key: String)  = getFormatted(key, _.toLong)

  def getFloat(key: String, default: Double) = getFloatOption(key) getOrElse default

  def getFloatOption(key: String) = getFormatted(key, _.toFloat)

  def getDouble(key: String, default: Double) = getDoubleOption(key) getOrElse default

  def getDoubleOption(key: String) = getFormatted(key, _.toDouble)

  def getFormatted[T](key: String, f: String => T) =
    getStringOption(key).flatMap(string => try {
      Some(f(string))
    } catch {
      case e: NumberFormatException => None
    })

  def getString(key: String, default: String) = getStringOption(key) getOrElse default

  def getStringOption(key: String): Option[String] = getStringsOption(key).flatMap(_.headOption)

  def getStringsOption(key: String): Option[Seq[String]] = getStringsOptionNotEmpty(key)

  private def getStringsOptionNotEmpty(key: String): Option[Seq[String]] =
    getStringsNotNullOption(key) match {
      case Some(Seq()) => Some(Seq(""))
      case any => any
    }

  private def getStringsNotNullOption(key: String): Option[Seq[String]] =
    (parameterMap.get(key) orElse parameterMap.get(key + "[]")).map(_.map(value => Option(value) getOrElse ""))

  override def equals(obj: Any): Boolean = obj.isInstanceOf[Parameters] && obj.asInstanceOf[Parameters].parameterMap.equals(parameterMap)

  override def hashCode(): Int = 17 + parameterMap.hashCode()

  def toURLString = if(parameterMap.isEmpty) "" else "?" + parameterMap.flatMap(e => e._2.map(v => s"${e._1}=$v")).mkString("&")
}

object Parameters {
  implicit def convertToParameters(parameterMap: Map[String, Seq[String]]) = new Parameters(parameterMap)

  implicit def convertToParameters() = new Parameters()
}
