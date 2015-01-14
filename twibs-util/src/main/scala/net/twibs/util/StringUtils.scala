/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.google.common.io.BaseEncoding
import com.ibm.icu.text.Transliterator

object StringUtils {
  Transliterator.registerInstance(Transliterator.createFromRules("umlauts", "ä > ae; Ä > Ae; Ö > Oe; ö > oe; Ü > Ue; ü > ue; ² > 2; ³ > 3",Transliterator.FORWARD))

  /**
   * Returns the input string without whitespace converted to lower case replacing umlauts with corresponding double
   * character equivalents. All unknown characters are removed.
   *
   * @param input string to convert to computer label
   * @return an ASCII string without white spaces
   */
  def convertToComputerLabel(input: String): String = computerLabelTransliterator.transliterate(input.trim()).replaceAll("[\\s\\p{Punct}&&[^\\_]]+", "-")

  private val computerLabelTransliterator = Transliterator.getInstance("Lower(); umlauts; NFD; Latin-ASCII")

  def encryptPassword(salt: String, unencryptedPassword: String) = {
    var ba = Hashing.sha256().hashString(salt + unencryptedPassword, Charsets.UTF_8)
    for (i <- 0 to 997) {
      ba = Hashing.sha256().hashBytes(ba.asBytes())
    }
    BaseEncoding.base64().encode(ba.asBytes)
  }

  def encodeForContentDisposition(filename: String) = UrlUtils.encodeUrl(filename).replace("%3F", "?")

  def collapseWhiteSpaceAndPunctuationTo(string:String, replaceWith: String = " ") = string.replaceAll("[\\s\\p{Punct}]+", replaceWith).stripPrefix(replaceWith).stripSuffix(replaceWith)

  def convertToSystemIndependentFileName(string: String): String = collapseWhiteSpaceAndPunctuationTo(systemIndependentFileNameTransliterator.transliterate(string), "-")

  private val systemIndependentFileNameTransliterator = Transliterator.getInstance("umlauts; NFD; Latin-ASCII")
}
