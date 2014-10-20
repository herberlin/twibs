/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest

class ClassUtilsTest extends TwibsTest {
  test("Convert to id") {
    ClassUtils.toId(getClass) should equal("net-twibs-util-classutilstest")
  }

  test("CompilationTimestamp") {
    ClassUtils.getCompilationTime(getClass) should be < System.currentTimeMillis
  }
}
