package twibs.util

import java.sql.Timestamp
import org.joda.time.DateTime
import scala.slick.jdbc.{SetParameter, GetResult}
import scala.slick.lifted.MappedTypeMapper
import scala.slick.session.{PositionedResult, PositionedParameters}

object JodaMapper {
  implicit def timestamp2dateTime = MappedTypeMapper.base[DateTime, Timestamp](
    dateTime => new Timestamp(dateTime.getMillis),
    date => new DateTime(date)
  )

  implicit object toDateTime extends GetResult[DateTime] {
    def apply(rs: PositionedResult) = {
      val timestamp = rs.nextTimestamp()
      if (timestamp == null) null else new DateTime(timestamp.getTime)
    }
  }

  implicit object toDateTimeOption extends GetResult[Option[DateTime]] {
    def apply(rs: PositionedResult) = rs.nextTimestampOption().map(timestamp => new DateTime(timestamp.getTime))
  }

  implicit object setDateTimeParameter extends SetParameter[DateTime] {
    def apply(d: DateTime, p: PositionedParameters): Unit = p.setTimestamp(new Timestamp(d.getMillis))
  }

  implicit object setDateTimeOptionParameter extends SetParameter[Option[DateTime]] {
    def apply(d: Option[DateTime], p: PositionedParameters): Unit =
      p.setTimestampOption(d.map(dateTime => new Timestamp(dateTime.getMillis)))
  }

}
