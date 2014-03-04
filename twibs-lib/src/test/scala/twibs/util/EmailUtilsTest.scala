package twibs.util

import EmailUtils.isValidEmailAddress
import twibs.TwibsTest

class EmailUtilsTest extends TwibsTest {
  test("Email validation") {
    isValidEmailAddress("me@example.com") should beTrue
    isValidEmailAddress("me@example.com") should beTrue
    isValidEmailAddress("x.me@example.com") should beTrue
    isValidEmailAddress("a@a.de") should beTrue
    isValidEmailAddress("A@a.De") should beTrue
    isValidEmailAddress("_A_@a.De") should beTrue
    isValidEmailAddress("-A-@a.De") should beTrue
    isValidEmailAddress("%x%me%@example.com") should beTrue

    isValidEmailAddress("me@ex%ample.com") should beFalse
    isValidEmailAddress("x..me@example.com") should beFalse
    isValidEmailAddress("A@-a.De") should beFalse
    isValidEmailAddress(".s@a.de") should beFalse
    isValidEmailAddress("s.@a.de") should beFalse
    isValidEmailAddress("a.s@-online.de") should beFalse
    isValidEmailAddress("") should beFalse
    isValidEmailAddress("  ") should beFalse
    isValidEmailAddress(" aa ") should beFalse
    isValidEmailAddress(" a a ") should beFalse
    isValidEmailAddress("a!@a.de") should beFalse
    isValidEmailAddress("a") should beFalse
    isValidEmailAddress("me@") should beFalse
    isValidEmailAddress("@example") should beFalse
    isValidEmailAddress("@example.com") should beFalse
    isValidEmailAddress(" me@example.com ") should beFalse
    isValidEmailAddress("x me@example.com") should beFalse
    isValidEmailAddress(" me@example.com") should beFalse
    isValidEmailAddress("me@example.com ") should beFalse
    isValidEmailAddress("me@ example.com") should beFalse
    isValidEmailAddress("me @example.com") should beFalse
    isValidEmailAddress("me@example .com") should beFalse
    isValidEmailAddress("me@example. com") should beFalse
    isValidEmailAddress("me@example.") should beFalse
    isValidEmailAddress("me@example") should beFalse
    isValidEmailAddress("me@") should beFalse
    isValidEmailAddress("@example.com") should beFalse
    isValidEmailAddress("me(at)example.com") should beFalse
  }
}
