/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import twibs.TwibsTest

class WebContextTest extends TwibsTest {
  test("Validate context path") {
    evaluating {
      WebContext.assertThatContextPathIsValid(null)
    } should produce[NullPointerException]
    (evaluating {
      WebContext.assertThatContextPathIsValid("/")
    } should produce[AssertionError]).getMessage should include("not be /")
    (evaluating {
      WebContext.assertThatContextPathIsValid("nix")
    } should produce[AssertionError]).getMessage should include("start with /")
    (evaluating {
      WebContext.assertThatContextPathIsValid("/x x")
    } should produce[AssertionError]).getMessage should include("invalid")
    WebContext.assertThatContextPathIsValid("") should equal("")
  }
}
