/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.sql.Timestamp
import java.util.{Calendar, Date}

import org.threeten.bp._

trait ThreeTenTransition {
  def zoneId = Request.zoneId

  implicit class RichInstant(instant: Instant) {
    def toZonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
  }

  implicit class RichCalendar(calendar: Calendar) {
    def toZonedDateTime = DateTimeUtils.toZonedDateTime(calendar)
  }

  implicit class RichTimestamp(timestamp: Timestamp) {
    def toZonedDateTime = ZonedDateTime.ofInstant(DateTimeUtils.toInstant(timestamp), zoneId)
  }

  implicit class RichDate(date: Date) {
    def toZonedDateTime = ZonedDateTime.ofInstant(DateTimeUtils.toInstant(date), zoneId)

    def toLocalDate = toZonedDateTime.toLocalDate
  }

  implicit class RichZonedDateTime(dateTime: ZonedDateTime) {
    def toCalendar: Calendar = DateTimeUtils.toGregorianCalendar(dateTime)

    def toTimestamp = DateTimeUtils.toSqlTimestamp(dateTime.withZoneSameInstant(zoneId).toLocalDateTime)

    def toSystemEpochMillis = toTimestamp.getTime
  }

  implicit class RichLocalDate(date: LocalDate) {
    def toCalendar: Calendar = DateTimeUtils.toGregorianCalendar(date.atStartOfDay(zoneId))

    def toDate = DateTimeUtils.toSqlDate(date)

    def toSystemEpochMillis = date.atStartOfDay.atZone(zoneId).toInstant.toEpochMilli
  }

  implicit object ZonedDateTimeOrdering extends Ordering[ZonedDateTime] {
    override def compare(x: ZonedDateTime, y: ZonedDateTime): Int = x.compareTo(y)
  }

  implicit object LocalDateTimeOrdering extends Ordering[LocalDateTime] {
    override def compare(x: LocalDateTime, y: LocalDateTime): Int = x.compareTo(y)
  }

  implicit def toThreeTenDuration(scalaDuration: scala.concurrent.duration.Duration): Duration =
    Duration.ofMillis(scalaDuration.toMillis)
}

object ThreeTenTransition extends ThreeTenTransition