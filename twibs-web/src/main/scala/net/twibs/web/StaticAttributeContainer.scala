/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import collection.mutable

trait StaticAttributeContainer extends AttributeContainer {
  private val attributes = new mutable.HashMap[String, Any]()

  def setAttribute(name: String, value: Any): Unit = attributes.put(name, value)

  def getAttribute(name: String): Option[Any] = attributes.get(name)

  def removeAttribute(name: String): Unit = attributes.remove(name)
}
