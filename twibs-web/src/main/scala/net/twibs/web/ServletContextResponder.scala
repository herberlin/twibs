/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import java.net.URL
import javax.servlet.ServletContext

class ServletContextResponder(servletContext: ServletContext) extends ResourceResponder {
  def getResourceOption(request: Request): Option[URL] = Option(servletContext.getResource(request.path))
}


