/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.webtest

import net.twibs.testutil.TwibsJettySeleniumTest
import org.eclipse.jetty.webapp.WebAppContext

class InteractiveIT extends TwibsJettySeleniumTest {
  override def filterClass = classOf[TestFilter]

  override def resourceBase = "src/main/webapp"

  test("Any") {
    open("/index.html")
    waitForWindowClosed()
  }

  override def init(webAppContext: WebAppContext): Unit = {
    webAppContext.addFilter(classOf[AASourceMapFilter], "*.css", null)
    webAppContext.addFilter(classOf[AASourceMapFilter], "*.js", null)
    super.init(webAppContext)
  }
}
