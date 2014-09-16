/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import java.sql.Timestamp
import java.util.{Date, Calendar}
import org.threeten.bp._

trait ThreeTenTransition {
  val zoneId = ZoneId.systemDefault()

  implicit def convertInstant(instant: Instant) = new {
    def toLocalDateTime = LocalDateTime.ofInstant(instant, zoneId)

    def toLocalDate = toLocalDateTime.toLocalDate
  }

  implicit def convertCalendarToThreeTen(calendar: Calendar) = new {
    def toLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(calendar.getTimeInMillis), zoneId)

    def toLocalDate = toLocalDateTime.toLocalDate
  }

  implicit def convertDateToThreeTen(date: Date) = new {
    def toLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime), zoneId)

    def toLocalDate = toLocalDateTime.toLocalDate
  }

  implicit def convertLocalDateTimeFromThreeTen(dateTime: LocalDateTime) = new {
    def toCalendar = {
      val ret = Calendar.getInstance()
      ret.setTimeInMillis(toSystemEpochMillis)
      ret
    }

    def toTimestamp = new Timestamp(toCalendar.getTime.getTime)

    def toSystemEpochMillis = dateTime.atZone(zoneId).toInstant.toEpochMilli
  }

  implicit def convertLocalDateFromThreeTen(date: LocalDate) = new {
    def toCalendar = {
      val ret = Calendar.getInstance()
      ret.setTimeInMillis(toSystemEpochMillis)
      ret
    }

    def toDate = new java.sql.Date(toCalendar.getTime.getTime)

    def toSystemEpochMillis = date.atStartOfDay.atZone(zoneId).toInstant.toEpochMilli
  }
}

object ThreeTenTransition extends ThreeTenTransition