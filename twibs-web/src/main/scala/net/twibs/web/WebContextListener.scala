/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import javax.servlet.annotation.WebListener
import javax.servlet.{ServletContextEvent, ServletContextListener}

import net.twibs.util.RequestSettings

@WebListener
class WebContextListener extends ServletContextListener {
  private var was: RequestSettings = null

  def contextInitialized(sce: ServletContextEvent): Unit = {
    was = RequestSettings.copy(contextPath = sce.getServletContext.getContextPath)
    RequestSettings.activate(was)
  }

  def contextDestroyed(sce: ServletContextEvent): Unit = RequestSettings.deactivate(was)
}
