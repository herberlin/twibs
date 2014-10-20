/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.webtest

import net.twibs.testutil.TwibsJettySeleniumTest

class InteractiveIT extends TwibsJettySeleniumTest {
  override def filterClass = classOf[TestFilter]

  override def resourceBase = "target/twibs-form-test-webapp"

  test("Any") {
    open("/index.html")
    waitForWindowClosed()
  }
}
