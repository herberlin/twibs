/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import java.net.URL
import javax.servlet.ServletContext

import net.twibs.util.Request

class ServletContextResponder(servletContext: ServletContext) extends ResourceResponder {
  def getResourceOption(request: Request): Option[URL] = Option(servletContext.getResource(request.path))
}
