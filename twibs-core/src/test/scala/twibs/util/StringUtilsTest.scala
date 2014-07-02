/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import twibs.TwibsTest

class StringUtilsTest extends TwibsTest {
  test("Convert to computer label") {
    StringUtils.convertToComputerLabel("  Äßt ! möhren ! ") should equal("aesst-moehren-")
  }
  test("Password encryption") {
    StringUtils.encryptPassword("asd7688754987", "ein passwort im klartext") should equal("wFkA/DgSirl0VFyOEnl/K7ALBPfJ4iOn3ZGjmUbCQF0=")
  }
  test("Encode uri") {
    StringUtils.encodeFilenameForContentDisposition("a.pdf") should equal("a.pdf")
    StringUtils.encodeFilenameForContentDisposition("File : a.pdf") should equal("File+%3A+a.pdf")
  }
}
