package twibs.util

import twibs.TwibsTest

class IdGeneratorTest extends TwibsTest {
  test("Generate Ids") {
    var set = Set[String]()
    for (i <- 1 to 100) {
      val id = IdGenerator.next()
      id should not be null
      id.length should be(12)
      set should not contain id
      set += id
    }
  }

  test("Generate random string") {
    IdGenerator.randomString(128) should have length 128
    IdGenerator.randomString(128) should not equal IdGenerator.randomString(128)
  }
}
