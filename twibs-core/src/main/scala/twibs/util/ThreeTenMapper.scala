package twibs.util

import ThreeTenTransition._
import org.threeten.bp.LocalDateTime
import scala.slick.jdbc.{SetParameter, GetResult}
import scala.slick.session.{PositionedParameters, PositionedResult}

object ThreeTenMapper {
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
