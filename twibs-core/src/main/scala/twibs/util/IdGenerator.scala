package twibs.util

import org.threeten.bp.LocalDateTime

/**
 * Generates Unique ids using a counter and a random part of
 * numbers and ascii characters
 */
object IdGenerator extends RandomStringGenerator("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ") {
  def next() = {
    val sb = new StringBuilder()
    val dateTime = LocalDateTime.now()
    appendChar(dateTime.getYear - 2000, sb)
    appendChar(dateTime.getMonthValue, sb)
    appendChar(dateTime.getDayOfMonth, sb)
    appendChar(dateTime.getHour, sb)
    appendChar(dateTime.getMinute, sb)
    appendChar(dateTime.getSecond, sb)
    appendRandomChars(6, sb)
    sb.toString()
  }
}
