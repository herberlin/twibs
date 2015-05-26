/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest

class CollectionUtilsTest extends TwibsTest {
  test("Group to map") {
    val x = CollectionUtils.group(List(("1", "1"), ("1", "1")))
    val y = Map("1" -> Seq("1", "1"))
    x should equal(y)
  }

  test("Ungroup") {
    val x = CollectionUtils.ungroup(Map("1" -> Seq("1", "2")))
    val y = Seq("1" -> "1", "1" -> "2")
    x should equal(y)
  }
}
