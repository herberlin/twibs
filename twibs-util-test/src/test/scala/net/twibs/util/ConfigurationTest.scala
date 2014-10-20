/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest

class ConfigurationTest extends TwibsTest {
  test("User configuration is loaded") {
    Configuration.getString("username").get should be("tester")
  }

  test("application.conf shadows reference.conf") {
    Configuration.getString("LOCALES.de.FIELD.placeholder").get should be("not from reference.conf")
  }
}
