/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import com.google.common.base.CharMatcher
import java.util.regex.Pattern

object QuotedStringSplitter {
  val regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'")
  val invalid = CharMatcher.INVISIBLE.or(CharMatcher.BREAKING_WHITESPACE)

  def splitStringRespectQuotes(query: String): List[String] = {
    val regexMatcher = regex.matcher(invalid.replaceFrom(query, ' '))
    var ret = List[String]()
    while (regexMatcher.find()) {
      ret = stripQuotes(regexMatcher.group()) :: ret
    }
    ret.reverse
  }

  private val stripper: CharMatcher = CharMatcher.anyOf("\" ")

  private def stripQuotes(string: String) = stripper.trimFrom(string)
}
