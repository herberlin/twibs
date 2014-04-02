/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import java.security.SecureRandom

class RandomStringGenerator(val chars: String) {
  val random = new SecureRandom

  def randomString(numberOfCharacters: Int) = {
    val sb = new StringBuilder(numberOfCharacters)
    appendRandomChars(numberOfCharacters, sb)
    sb.toString()
  }

  def appendRandomChars(numberOfChars: Int, sb: StringBuilder): Unit =
    for (i <- 1 to numberOfChars) {
      sb.append(chars(random.nextInt(chars.length)))
    }

  def appendChar(int: Int, sb: StringBuilder): Unit = sb.append(chars(int % chars.length))
}
