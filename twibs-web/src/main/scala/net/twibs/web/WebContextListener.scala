/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import javax.servlet.annotation.WebListener
import javax.servlet.{ServletContextEvent, ServletContextListener}

import net.twibs.util.Request

@WebListener
class WebContextListener extends ServletContextListener {
  private var was: Request = null

  def contextInitialized(sce: ServletContextEvent): Unit = {
    was = Request.copy(contextPath = sce.getServletContext.getContextPath)
    Request.activate(was)
  }

  def contextDestroyed(sce: ServletContextEvent): Unit = Request.deactivate(was)
}
