/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.testutil.TwibsTest

class WebContextTest extends TwibsTest {
  test("Validate context path") {
    intercept[NullPointerException] {
      WebContext.assertThatContextPathIsValid(null)
    }
    intercept[AssertionError] {
      WebContext.assertThatContextPathIsValid("/")
    }.getMessage should include("not be /")
    intercept[AssertionError] {
      WebContext.assertThatContextPathIsValid("nix")
    }.getMessage should include("start with /")
    intercept[AssertionError] {
      WebContext.assertThatContextPathIsValid("/x x")
    }.getMessage should include("invalid")
    WebContext.assertThatContextPathIsValid("") should equal("")
  }
}
