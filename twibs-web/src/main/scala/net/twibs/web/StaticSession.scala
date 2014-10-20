/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

class StaticSession extends Session with StaticAttributeContainer {
  def invalidate(): Unit = Unit
}
