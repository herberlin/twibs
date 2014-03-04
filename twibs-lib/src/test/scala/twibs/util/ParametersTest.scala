package twibs.util

import twibs.TwibsTest

class ParametersTest extends TwibsTest {
  test("Get parameter") {
    val parameters: Parameters = Map("empty" -> Seq(), "null" -> Seq(null), "one" -> Seq("1"), "two" -> Seq("2", "3"))

    parameters.getStringOption("nix") should equal(None)
    parameters.getStringOption("null") should equal(Some(""))
    parameters.getStringOption("empty") should equal(Some(""))
    parameters.getStringOption("one") should equal(Some("1"))
    parameters.getStringOption("two") should equal(Some("2"))
  }
}
