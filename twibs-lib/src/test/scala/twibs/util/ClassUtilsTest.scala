package twibs.util

import twibs.TwibsTest

class ClassUtilsTest extends TwibsTest {
  test("Convert to id") {
    ClassUtils.toId(getClass) should equal("twibs-util-classutilstest")
  }

  test("CompilationTimestamp") {
    ClassUtils.getCompilationTime(getClass) should be < System.currentTimeMillis
  }
}
