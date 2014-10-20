/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import javax.servlet.annotation.WebListener
import javax.servlet.{ServletContextEvent, ServletContextListener}

import net.twibs.util.{UrlUtils, DynamicVariableWithDynamicDefault}

object WebContext extends DynamicVariableWithDynamicDefault[WebContext](new WebContext("")) {
  def assertThatContextPathIsValid(contextPath: String) = {
    if (!contextPath.isEmpty) {
      assert(contextPath != "/", "contextPath must not be /")
      assert(contextPath.startsWith("/"), s"contextPath '$contextPath' must start with /")
      assert("/" + UrlUtils.encodeUrl(UrlUtils.decodeUrl(contextPath.substring(1))) == contextPath, s"contextPath '$contextPath' is invalid")
    }
    contextPath
  }
}

class WebContext(val path: String) {
  WebContext.assertThatContextPathIsValid(path)

  def use[R](f: => R): R = WebContext.use(this)(f)
}

@WebListener
class WebContextListener extends ServletContextListener {
  def contextInitialized(sce: ServletContextEvent): Unit = WebContext.activate(new WebContext(sce.getServletContext.getContextPath))

  def contextDestroyed(sce: ServletContextEvent): Unit = WebContext.deactivate(WebContext.default)
}
