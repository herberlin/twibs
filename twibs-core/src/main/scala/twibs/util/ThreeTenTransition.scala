package twibs.util

import java.util.{Date, Calendar}
import org.joda.time.DateTime
import org.threeten.bp.{LocalDate, ZoneOffset, Instant, LocalDateTime}
import java.sql.Timestamp

object ThreeTenTransition {
  // TODO; Switch to ThreeTen from Joda. Remove this function after done
  implicit def convertJodaDateTimeToThreeTen(dateTime: DateTime) = new {
    def toTTLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime.getMillis), ZoneOffset.UTC)

    def toTTLocalDate = toTTLocalDateTime.toLocalDate
  }

  implicit def convertCalendarToThreeTen(calendar: Calendar) = new {
    def toLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(calendar.getTimeInMillis), ZoneOffset.UTC)

    def toLocalDate = toLocalDateTime.toLocalDate
  }

  implicit def convertDateToThreeTen(date: Date) = new {
    def toLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime), ZoneOffset.UTC)

    def toLocalDate = toLocalDateTime.toLocalDate
  }

  implicit def convertLocalDateTimeFromThreeTen(dateTime: LocalDateTime) = new {
    def toCalendar = {
      val ret = Calendar.getInstance()
      ret.setTimeInMillis(dateTime.toInstant(ZoneOffset.UTC).toEpochMilli)
      ret
    }

    def toTimestamp = new Timestamp(toCalendar.getTime.getTime)
  }

  implicit def convertLocalDateFromThreeTen(date: LocalDate) = new {
    def toCalendar = {
      val ret = Calendar.getInstance()
      ret.setTimeInMillis(date.atStartOfDay.toInstant(ZoneOffset.UTC).toEpochMilli)
      ret
    }

    def toDate = new java.sql.Date(toCalendar.getTime.getTime)
  }
}
