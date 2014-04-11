/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

case class Parameters(parameterMap: Map[String, Seq[String]] = Map()) extends Serializable {
  def getInt(key: String, default: Int) = getIntOption(key) getOrElse default

  def getIntOption(key: String) =
    getStringOption(key).flatMap(string => try {
      Some(string.toInt)
    } catch {
      case e: NumberFormatException => None
    })

  def getBoolean(key: String, default: Boolean) = getBooleanOption(key) getOrElse default

  def getBooleanOption(key: String) = getStringOption(key).flatMap(string => Some("true".equals(string)))

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
}

object Parameters {
  implicit def convertToParameters(parameterMap: Map[String, Seq[String]]) = new Parameters(parameterMap)

  implicit def convertToParameters() = new Parameters()
}
