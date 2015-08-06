/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.sql.Timestamp
import java.util.Calendar

import net.twibs.testutil.TwibsTest
import org.threeten.bp._
import ThreeTenTransition._


class ThreeTenTransitionTest extends TwibsTest {
  val america = ZoneId.of("America/New_York")

  test("Convert to local date") {
    val europe = ZoneId.of("Europe/Berlin")
    val america = ZoneId.of("America/New_York")
    val e = ZonedDateTime.of(2012, 2, 1, 6, 0, 0, 376308000, europe)
    val a = ZonedDateTime.of(2012, 2, 1, 0, 0, 0, 376308000, america)
    e should not equal a
    e.isEqual( a) shouldBe true
    e.toTimestamp shouldBe a.toTimestamp
    e.toTimestamp.toZonedDateTime.isEqual(e) shouldBe true
    a.toTimestamp.toZonedDateTime.isEqual(e) shouldBe true
  }

    test("From Timestamp to ZonedDateTime and back") {
      val ts = new Timestamp(System.currentTimeMillis())
      ts should be(ts.toZonedDateTime.toTimestamp)
    }

    test("From ZonedDateTime to Timestamp and back") {
      val ldt = LocalDateTime.of(2012, 12, 21, 12, 13).atZone(america)
      ldt.isEqual(ldt.toTimestamp.toZonedDateTime) shouldBe true
    }

    test("From Calendar to ZonedDateTime and back") {
      val cal = Calendar.getInstance()
      cal.getTime should be(cal.toZonedDateTime.toCalendar.getTime)
    }

    test("Time in millis") {
      val now = ZonedDateTime.now()
      now.toCalendar.getTimeInMillis shouldBe now.toSystemEpochMillis
    }

    test("From ZonedDateTime to Calendar and back") {
      val ldt = LocalDateTime.of(2012, 12, 21, 12, 13).atZone(america)
      ldt.isEqual(ldt.toCalendar.toZonedDateTime) shouldBe true
    }

    test("From LocalDate to Date and back") {
      val ldt = LocalDate.of(2012, 12, 21)
      ldt should be(ldt.toDate.toLocalDate)
    }

    test("Precision") {
      val dt = ZonedDateTime.of(2014, 11, 17, 10, 37, 34, 376308000, america)
      dt.getNano should be(376308000)
      dt.toTimestamp.getNanos should be(376308000)
      dt.toTimestamp.toZonedDateTime.getNano should be(376308000)
    }
}
