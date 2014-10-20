/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import org.scalatest.{FunSuite, Matchers}

class QuotedStringSplitterTest extends FunSuite with Matchers {
  test("Query splitter") {
    QuotedStringSplitter.splitStringRespectQuotes("\t\ba\n") should equal(List("a"))
    QuotedStringSplitter.splitStringRespectQuotes("\" a\tb\bc\nd \"") should equal(List("a b c d"))
    QuotedStringSplitter.splitStringRespectQuotes( """a""") should equal(List("a"))
    QuotedStringSplitter.splitStringRespectQuotes( """ a """) should equal(List("a"))
    QuotedStringSplitter.splitStringRespectQuotes( """"a"""") should equal(List("a"))
    QuotedStringSplitter.splitStringRespectQuotes( """ "a" """) should equal(List("a"))
    QuotedStringSplitter.splitStringRespectQuotes( """ "a b" """) should equal(List("a b"))
    QuotedStringSplitter.splitStringRespectQuotes( """ "a b " """) should equal(List("a b"))
    QuotedStringSplitter.splitStringRespectQuotes( """a b""") should equal(List("a", "b"))
    QuotedStringSplitter.splitStringRespectQuotes( """ "a"  "b c" """) should equal(List("a", "b c"))
    QuotedStringSplitter.splitStringRespectQuotes( """ "a" x "b c" """) should equal(List("a", "x", "b c"))
    QuotedStringSplitter.splitStringRespectQuotes( """ "a" x "b c" """) should equal(List("a", "x", "b c"))
  }
}
