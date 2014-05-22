/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import twibs.TwibsTest

class ConfigurationTest extends TwibsTest {
  test("User configuration is loaded") {
    Configuration.getString("test.user").get should be("tester")
  }
}
