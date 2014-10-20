/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.db

import java.sql.{Date, Timestamp}
import org.threeten.bp.{LocalDate, LocalDateTime}
//TODO: Replace PostgresDriver with generic type once someone knows how ;)
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{PositionedParameters, PositionedResult, SetParameter, GetResult}
import net.twibs.util.Predef._

object ThreeTenMapper {
  implicit val timestamp2dateTime = MappedColumnType.base[LocalDateTime, Timestamp](_.toTimestamp, _.toLocalDateTime)
  implicit val date2date = MappedColumnType.base[LocalDate, Date](_.toDate, _.toLocalDate)

  implicit object toDateTime extends GetResult[LocalDateTime] {
    def apply(rs: PositionedResult) = {
      val timestamp = rs.nextTimestamp()
      if (timestamp == null) null else timestamp.toLocalDateTime
    }
  }

  implicit object toDateTimeOption extends GetResult[Option[LocalDateTime]] {
    def apply(rs: PositionedResult) = rs.nextTimestampOption().map(timestamp => timestamp.toLocalDateTime)
  }

  implicit object setDateTimeParameter extends SetParameter[LocalDateTime] {
    def apply(d: LocalDateTime, p: PositionedParameters): Unit = p.setTimestamp(d.toTimestamp)
  }

  implicit object setDateTimeOptionParameter extends SetParameter[Option[LocalDateTime]] {
    def apply(d: Option[LocalDateTime], p: PositionedParameters): Unit =
      p.setTimestampOption(d.map(dateTime => dateTime.toTimestamp))
  }

  implicit object toDate extends GetResult[LocalDate] {
    def apply(rs: PositionedResult) = {
      val date = rs.nextDate()
      if (date == null) null else date.toLocalDate
    }
  }

  implicit object toDateOption extends GetResult[Option[LocalDate]] {
    def apply(rs: PositionedResult) = rs.nextDateOption().map(date => date.toLocalDate)
  }

  implicit object setDateParameter extends SetParameter[LocalDate] {
    def apply(d: LocalDate, p: PositionedParameters): Unit = p.setDate(d.toDate)
  }

  implicit object setDateOptionParameter extends SetParameter[Option[LocalDate]] {
    def apply(d: Option[LocalDate], p: PositionedParameters): Unit =
      p.setDateOption(d.map(date => date.toDate))
  }
}
