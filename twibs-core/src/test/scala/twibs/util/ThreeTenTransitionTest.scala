/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import java.sql.Timestamp
import java.util.Calendar

import twibs.TwibsTest
import twibs.util.ThreeTenTransition._

import org.threeten.bp.{LocalDate, LocalDateTime}

class ThreeTenTransitionTest extends TwibsTest {
  test("From Timestamp to LocalDateTime and back") {
    val ts = new Timestamp(System.currentTimeMillis())
    ts should be(ts.toLocalDateTime.toTimestamp)
  }

  test("From LocalDateTime to Timestamp and back") {
    val ldt = LocalDateTime.of(2012, 12, 21, 12, 13)
    ldt should be(ldt.toTimestamp.toLocalDateTime)
  }

  test("From Calendar to LocalDateTime and back") {
    val cal = Calendar.getInstance()
    cal should be(cal.toLocalDateTime.toCalendar)
  }

  test("From LocalDateTime to Calendar and back") {
    val ldt = LocalDateTime.of(2012, 12, 21, 12, 13)
    ldt should be(ldt.toCalendar.toLocalDateTime)
  }

  test("From LocalDate to Date and back") {
    val ldt = LocalDate.of(2012, 12, 21)
    ldt should be(ldt.toDate.toLocalDate)
  }
}
