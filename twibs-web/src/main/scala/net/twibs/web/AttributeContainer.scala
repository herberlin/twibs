/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

trait AttributeContainer {
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
