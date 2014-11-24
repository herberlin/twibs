/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.sql.Timestamp
import java.util.{Date, Calendar}

import org.threeten.bp._

trait ThreeTenTransition {
  def zoneId = SystemSettings.zoneId

  implicit def convertInstant(instant: Instant) = new {
    def toLocalDateTime = LocalDateTime.ofInstant(instant, zoneId)

    def toLocalDate = toLocalDateTime.toLocalDate
  }

  implicit def convertCalendarToThreeTen(calendar: Calendar) = new {
    def toLocalDateTime = LocalDateTime.ofInstant(DateTimeUtils.toInstant(calendar), zoneId)

    def toLocalDate = toLocalDateTime.toLocalDate
  }

  implicit def convertTimestampToThreeTen(timestamp: Timestamp) = new {
    def toLocalDateTime = LocalDateTime.ofInstant(DateTimeUtils.toInstant(timestamp), zoneId)

    def toLocalDate = toLocalDateTime.toLocalDate
  }

  implicit def convertDateToThreeTen(date: Date) = new {
    def toLocalDateTime = LocalDateTime.ofInstant(DateTimeUtils.toInstant(date), zoneId)

    def toLocalDate = toLocalDateTime.toLocalDate
  }

  implicit def convertLocalDateTimeFromThreeTen(dateTime: LocalDateTime) = new {
    def toCalendar: Calendar = DateTimeUtils.toGregorianCalendar(ZonedDateTime.of(dateTime, zoneId))

    def toTimestamp = DateTimeUtils.toSqlTimestamp(dateTime)

    def toSystemEpochMillis = dateTime.atZone(zoneId).toInstant.toEpochMilli
  }

  implicit def convertLocalDateFromThreeTen(date: LocalDate) = new {
    def toCalendar: Calendar = DateTimeUtils.toGregorianCalendar(date.atStartOfDay(zoneId))

    def toDate = DateTimeUtils.toSqlDate(date)

    def toSystemEpochMillis = date.atStartOfDay.atZone(zoneId).toInstant.toEpochMilli
  }
}

object ThreeTenTransition extends ThreeTenTransition