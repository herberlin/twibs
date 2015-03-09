/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import concurrent.duration._

import net.twibs.testutil.TwibsTest

class MemoTest extends TwibsTest {
  test("Test timeout cache") {
    var x = 0

    val cache = Memo {
      x = x + 1
      x
    }.recomputeAfter(40 millis)

    cache() should be(1)
    cache() should be(1)
    cache.reset()
    cache() should be(2)
    Thread.sleep(41)
    cache() should be(3)
  }
}
