/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import concurrent.duration._

import net.twibs.testutil.TwibsTest


class LazyCacheTest extends TwibsTest {
  test("Test timeout cache") {
    var x = 0

    val cache = LazyCache(40 millis) {
      x = x + 1
      x
    }

    cache.value should be(1)
    cache.value should be(1)
    cache.reset()
    cache.value should be(2)
    Thread.sleep(41)
    cache.value should be(3)
  }
}
