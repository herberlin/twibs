/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import Parsers._
import org.scalatest.{FunSuite, Matchers}

class ParsersTest extends FunSuite with Matchers {
  test("Convert Ints") {
    "test".toIntOption should be(None)
    "1".toIntOption should be(Some(1))
    "1".toIntWithDefault(0) should be(1)
    "a".toIntWithDefault(2) should be(2)
  }

  test("Convert Longs") {
    "test".toLongOption should be(None)
    "1".toLongOption should be(Some(1L))
    "1".toLongWithDefault(0L) should be(1L)
    "a".toLongWithDefault(2L) should be(2L)
  }
}
