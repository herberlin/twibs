/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import scala.collection.mutable

sealed trait RequestMethod

case object GetMethod extends RequestMethod

case object PostMethod extends RequestMethod

case object PutMethod extends RequestMethod

case object DeleteMethod extends RequestMethod

case object UnknownMethod extends RequestMethod

trait AttributeContainer extends Serializable {
  def removeAttribute(name: String): Unit

  def setAttribute(name: String, value: Any): Unit

  def getAttribute(name: String): Option[Any]

  def getAttribute[T](name: String, cls: Class[T]): Option[T] = getAttribute(name).map(_.asInstanceOf[T])

  def getAttribute[T <: Any](attributeName: String, default: => T): T =
    getAttribute(attributeName).fold(default)(_.asInstanceOf[T])

  def getAttributeOrStoreDefault[T <: Any](attributeName: String, default: => T): T = {
    getAttribute(attributeName).fold({
      val ret = default
      setAttribute(attributeName, ret)
      ret
    })(_.asInstanceOf[T])
  }
}

class SimpleAttributeContainer extends AttributeContainer {
  private val attributes = new mutable.HashMap[String, Any]()

  def setAttribute(name: String, value: Any): Unit = attributes.put(name, value)

  def getAttribute(name: String): Option[Any] = attributes.get(name)

  def removeAttribute(name: String): Unit = attributes.remove(name)
}

trait CookieContainer extends Serializable {
  def getCookie(name: String): Option[String]

  def removeCookie(name: String): Unit

  def setCookie(name: String, value: String): Unit
}

class SimpleCookieContainer extends CookieContainer {
  def getCookie(name: String) = None

  def removeCookie(name: String) = ()

  def setCookie(name: String, value: String) = ()
}
