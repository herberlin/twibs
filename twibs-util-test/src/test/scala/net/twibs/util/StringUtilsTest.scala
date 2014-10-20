/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest

class StringUtilsTest extends TwibsTest {
  test("Convert to computer label") {
    StringUtils.convertToComputerLabel("  Äßt ! möh_ren ! ") should equal("aesst-moeh_ren-")
  }
  test("Password encryption") {
    StringUtils.encryptPassword("asd7688754987", "ein passwort im klartext") should equal("wFkA/DgSirl0VFyOEnl/K7ALBPfJ4iOn3ZGjmUbCQF0=")
  }
  test("Encode uri") {
    StringUtils.encodeForContentDisposition("a.pdf") should equal("a.pdf")
    StringUtils.encodeForContentDisposition("File : a.pdf") should equal("File+%3A+a.pdf")
  }
  test("Convert text to file name") {
    StringUtils.convertToSystemIndependentFileName("  Äßt\t !\n \r Rote---Möhrên_hier ! ² für ³ #") should equal("Aesst-Rote-Moehren-hier-2-fuer-3")
  }
}
