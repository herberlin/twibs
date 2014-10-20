/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest
import net.twibs.util.EmailUtils._

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

    isValidEmailAddress("") should beFalse
    isValidEmailAddress("  ") should beFalse
    isValidEmailAddress(" aa ") should beFalse
    isValidEmailAddress(" a a ") should beFalse
    isValidEmailAddress("a") should beFalse
    isValidEmailAddress("me@") should beFalse
    isValidEmailAddress("@example") should beFalse
    isValidEmailAddress("@example.com") should beFalse
    isValidEmailAddress("x me@example.com") should beFalse
    isValidEmailAddress("me@ example.com") should beFalse
    isValidEmailAddress("me @example.com") should beFalse
    isValidEmailAddress("me@example .com") should beFalse
    isValidEmailAddress("me@example. com") should beFalse
    isValidEmailAddress("me@example.") should beFalse
    isValidEmailAddress("me@") should beFalse
    isValidEmailAddress("@example.com") should beFalse
    isValidEmailAddress("me(at)example.com") should beFalse

    isValidEmailAddress(" me@example.com ") should beFalse
    isValidEmailAddress(" me@example.com") should beFalse
    isValidEmailAddress("me@example.com ") should beFalse

    isValidEmailAddress("A@-a.De") should beFalse
    isValidEmailAddress("a.s@-online.de") should beFalse
    isValidEmailAddress(".s@a.de") should beFalse
    isValidEmailAddress("s.@a.de") should beFalse
    isValidEmailAddress("a!@a.de") should beFalse
    isValidEmailAddress("me@example") should beFalse
    isValidEmailAddress("x..me@example.com") should beFalse

    isRfc822InternetAddress( """"Me" <%x%me%@example.com>""") should beTrue
    isRfc822InternetAddress( """"Me < %x%me%@example.com >""") should beFalse
  }

  test("RFC822") {
    isRfc822InternetAddress("A@-a.De") should beTrue
    isRfc822InternetAddress("a.s@-online.de") should beTrue
    isRfc822InternetAddress(".s@a.de") should beTrue
    isRfc822InternetAddress("s.@a.de") should beTrue
    isRfc822InternetAddress("a!@a.de") should beTrue
    isRfc822InternetAddress("me@example") should beTrue
    isRfc822InternetAddress("x..me@example.com") should beTrue
    isRfc822InternetAddress("me@example.com, me@example.com") should beFalse
    isRfc822InternetAddress("me@ex%ample.com") should beFalse

    toInternetAddressOption(""""Me" <%x%me%@example.com>""").get.getPersonal should be ("Me")
    toInternetAddressOption(""""Me" < %x%me%@example.com >""").get.getAddress should be ("%x%me%@example.com")
  }

  test("Email clean up") {
    cleanupEmailAddress(" mE. @example.com.") should be("me@example.com")
  }
}
