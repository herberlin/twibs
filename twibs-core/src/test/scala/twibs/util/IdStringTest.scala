/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import twibs.TwibsTest
import twibs.util.JavaScript._

class IdStringTest extends TwibsTest {
  test("Test concat") {
    (IdString("") ~ "test").string should be ("test")
    (IdString("key") ~ "").string should be ("key")
    (IdString("key") ~ "key2").string should be ("key_key2")
  }

  test("Test add string") {
    (IdString("") + "test").string should be ("test")
    (IdString("key") + "").string should be ("key")
    (IdString("key") + "key2").string should be ("keykey2")
  }
}
