/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import ThreeTenTransition._
import java.sql.Timestamp
import org.threeten.bp.LocalDateTime
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{PositionedParameters, PositionedResult, SetParameter, GetResult}

object ThreeTenMapper {
  implicit val timestamp2dateTime = MappedColumnType.base[LocalDateTime, Timestamp](_.toTimestamp, _.toLocalDateTime)

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

}
