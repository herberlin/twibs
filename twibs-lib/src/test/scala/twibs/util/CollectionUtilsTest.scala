package twibs.util

import twibs.TwibsTest

class CollectionUtilsTest extends TwibsTest {
  test("Test zip to map") {
    val x = CollectionUtils.zipToMap(List(("1", "1"), ("1", "1")))
    val y = Map("1" -> Seq("1", "1"))
    x should equal(y)
  }
}
