/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import javax.servlet.http.HttpServletRequest

import net.twibs.util.Session

class HttpSession(httpServletRequest: HttpServletRequest) extends Session {
  def setAttribute(name: String, value: Any): Unit =
    httpServletRequest.getSession(true).setAttribute(name, value)

  def getAttribute(name: String): Option[Any] =
    Option(httpServletRequest.getSession(false)).flatMap(session => Option(session.getAttribute(name)))

  def removeAttribute(name: String): Unit =
    Option(httpServletRequest.getSession(false)).foreach(session => Option(session.removeAttribute(name)))

  def invalidate(): Unit =
    Option(httpServletRequest.getSession(false)).foreach(session => session.invalidate())
}
