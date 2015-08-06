/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest
import net.twibs.util.EmailUtils._

class EmailUtilsTest extends TwibsTest {
  test("Email validation") {
    isValidEmailAddress("me@example.com") shouldBe true
    isValidEmailAddress("me@example.com") shouldBe true
    isValidEmailAddress("x.me@example.com") shouldBe true
    isValidEmailAddress("a@a.de") shouldBe true
    isValidEmailAddress("A@a.De") shouldBe true
    isValidEmailAddress("_A_@a.De") shouldBe true
    isValidEmailAddress("-A-@a.De") shouldBe true
    isValidEmailAddress("%x%me%@example.com") shouldBe true

    isValidEmailAddress("") shouldBe false
    isValidEmailAddress("  ") shouldBe false
    isValidEmailAddress(" aa ") shouldBe false
    isValidEmailAddress(" a a ") shouldBe false
    isValidEmailAddress("a") shouldBe false
    isValidEmailAddress("me@") shouldBe false
    isValidEmailAddress("@example") shouldBe false
    isValidEmailAddress("@example.com") shouldBe false
    isValidEmailAddress("x me@example.com") shouldBe false
    isValidEmailAddress("me@ example.com") shouldBe false
    isValidEmailAddress("me @example.com") shouldBe false
    isValidEmailAddress("me@example .com") shouldBe false
    isValidEmailAddress("me@example. com") shouldBe false
    isValidEmailAddress("me@example.") shouldBe false
    isValidEmailAddress("me@") shouldBe false
    isValidEmailAddress("@example.com") shouldBe false
    isValidEmailAddress("me(at)example.com") shouldBe false

    isValidEmailAddress(" me@example.com ") shouldBe false
    isValidEmailAddress(" me@example.com") shouldBe false
    isValidEmailAddress("me@example.com ") shouldBe false

    isValidEmailAddress("A@-a.De") shouldBe false
    isValidEmailAddress("a.s@-online.de") shouldBe false
    isValidEmailAddress(".s@a.de") shouldBe false
    isValidEmailAddress("s.@a.de") shouldBe false
    isValidEmailAddress("a!@a.de") shouldBe false
    isValidEmailAddress("me@example") shouldBe false
    isValidEmailAddress("x..me@example.com") shouldBe false

    isRfc822InternetAddress( """"Me" <%x%me%@example.com>""") shouldBe true
    isRfc822InternetAddress( """"Me < %x%me%@example.com >""") shouldBe false
  }

  test("RFC822") {
    isRfc822InternetAddress("A@-a.De") shouldBe true
    isRfc822InternetAddress("a.s@-online.de") shouldBe true
    isRfc822InternetAddress(".s@a.de") shouldBe true
    isRfc822InternetAddress("s.@a.de") shouldBe true
    isRfc822InternetAddress("a!@a.de") shouldBe true
    isRfc822InternetAddress("me@example") shouldBe true
    isRfc822InternetAddress("x..me@example.com") shouldBe true
    isRfc822InternetAddress("me@example.com, me@example.com") shouldBe false
    isRfc822InternetAddress("me@ex%ample.com") shouldBe false

    toInternetAddressOption(""""Me" <%x%me%@example.com>""").get.getPersonal should be ("Me")
    toInternetAddressOption(""""Me" < %x%me%@example.com >""").get.getAddress should be ("%x%me%@example.com")
  }

  test("Email clean up") {
    cleanupEmailAddress(" mE. @example.com.") should be("me@example.com")
  }
}
