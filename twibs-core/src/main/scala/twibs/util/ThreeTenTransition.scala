package twibs.util

import java.sql.Timestamp
import java.util.{Date, Calendar}
import org.joda.time.DateTime
import org.threeten.bp._

object ThreeTenTransition {
  val zoneId = ZoneId.systemDefault()

  // TODO; Switch to ThreeTen from Joda. Remove this function after done
  implicit def convertJodaDateTimeToThreeTen(dateTime: DateTime) = new {
    def toTTLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime.getMillis), zoneId)

    def toTTLocalDate = toTTLocalDateTime.toLocalDate
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
