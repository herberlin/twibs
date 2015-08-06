/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest

class CollectionUtilsTest extends TwibsTest {
  test("Seq to map") {
    val x = CollectionUtils.seqToMap(List(("1", "1"), ("1", "1")))
    val y = Map("1" -> Seq("1", "1"))
    x should equal(y)
  }

  test("Map to seq") {
    val x = CollectionUtils.mapToSeq(Map("1" -> Seq("1", "2")))
    val y = Seq("1" -> "1", "1" -> "2")
    x should equal(y)
  }
}
