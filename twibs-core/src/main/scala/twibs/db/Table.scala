package twibs.db

import com.google.common.base.Stopwatch
import java.sql.{Types, Connection, PreparedStatement, ResultSet}
import org.threeten.bp.{LocalDate, LocalDateTime}
import twibs.util.Loggable
import twibs.util.Predef._
import twibs.util.ThreeTenTransition._

object Sql extends Loggable

class Table(val tableName: String) {
  protected implicit def table: Table = this

  case class StringColumn(name: String) extends Column[String] {
    def get(rs: ResultSet) = rs.getString(name)

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setString(pos, value.asInstanceOf[String])

    def like(right: String): Where = new ColumnWhere("LIKE", right)
  }

  case class StringOptionColumn(name: String) extends Column[Option[String]] {
    def get(rs: ResultSet) = Option(rs.getString(name))

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[String]] match {
      case Some(value) => ps.setString(pos, value)
      case None => ps.setNull(pos, Types.VARCHAR)
    }
  }

  case class LongColumn(name: String) extends Column[Long] {
    def get(rs: ResultSet) = rs.getLong(name)

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setLong(pos, value.asInstanceOf[Long])
  }

  case class LongOptionColumn(name: String) extends Column[Option[Long]] {
    def get(rs: ResultSet) = Option(rs.getLong(name))

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[Long]] match {
      case Some(value) => ps.setLong(pos, value)
      case None => ps.setNull(pos, Types.BIGINT)
    }
  }

  case class BooleanColumn(name: String) extends Column[Boolean] {
    def get(rs: ResultSet) = rs.getBoolean(name)

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setBoolean(pos, value.asInstanceOf[Boolean])
  }

  case class DoubleColumn(name: String) extends Column[Double] {
    def get(rs: ResultSet) = rs.getDouble(name)

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setDouble(pos, value.asInstanceOf[Double])
  }

  case class DoubleOptionColumn(name: String) extends Column[Option[Double]] {
    def get(rs: ResultSet) = Option(rs.getDouble(name))

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[Double]] match {
      case Some(value) => ps.setDouble(pos, value)
      case None => ps.setNull(pos, Types.DOUBLE)
    }
  }

  case class LocalDateTimeColumn(name: String) extends Column[LocalDateTime] {
    def get(rs: ResultSet) = rs.getTimestamp(name).toLocalDateTime

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setTimestamp(pos, value.asInstanceOf[LocalDateTime].toTimestamp)
  }

  case class LocalDateTimeOptionColumn(name: String) extends Column[Option[LocalDateTime]] {
    def get(rs: ResultSet) = Option(rs.getTimestamp(name).toLocalDateTime)

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[LocalDateTime]] match {
      case Some(value) => ps.setTimestamp(pos, value.toTimestamp)
      case None => ps.setNull(pos, Types.TIMESTAMP)
    }
  }

  case class LocalDateColumn(name: String) extends Column[LocalDate] {
    def get(rs: ResultSet) = rs.getDate(name).toLocalDate

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setDate(pos, value.asInstanceOf[LocalDate].toDate)
  }

  case class LocalDateOptionColumn(name: String) extends Column[Option[LocalDate]] {
    def get(rs: ResultSet) = Option(rs.getDate(name).toLocalDate)

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[LocalDate]] match {
      case Some(value) => ps.setDate(pos, value.toDate)
      case None => ps.setNull(pos, Types.DATE)
    }
  }

  case class EnumColumn[T <: Enumeration](name: String, enum: T) extends Column[T#Value] {
    def get(rs: ResultSet) = enum(rs.getInt(name))

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setInt(pos, value.asInstanceOf[T#Value].id)
  }

  def size(implicit connection: Connection) = Statement(s"SELECT count(*) FROM $tableName").size(connection)
}

abstract class Column[T](implicit val table: Table) {
  def name: String

  def fullName: String = table.tableName + "." + name

  protected class ColumnWhere(operator: String, right: T) extends Where {
    private[db] override def toStatement = Statement(s"$fullName $operator ?", (Column.this, right) :: Nil)
  }

  protected class ColumnSeqWhere(operator: String, right: Seq[T]) extends Where {
    private[db] override def toStatement = Statement(s"$fullName $operator ?", (Column.this, right) :: Nil)
  }

  def >(right: T): Where = new ColumnWhere(">", right)

  def <(right: T): Where = new ColumnWhere("<", right)

  def !==(right: T): Where = new ColumnWhere("<>", right)

  def ===(right: T): Where = new ColumnWhere("=", right)

  def in(vals: List[T]): Where = new Where {
    private[db] override def toStatement = Statement(s"$fullName in (${vals.map(_ => "?").mkString(",")})", vals.map(right => (Column.this, right)))
  }

  def asc: OrderBy = new OrderBy {
    override def toStatement = Statement(s"$fullName ASC")
  }

  def desc: OrderBy = new OrderBy {
    override def toStatement = Statement(s"$fullName DESC")
  }

  def get(rs: ResultSet): T

  def set(ps: PreparedStatement, pos: Int, value: Any): Unit
}

trait Where {
  self =>
  def &&(right: Where) = new Where {
    override private[db] def toStatement: Statement = self.toStatement(precendence) ~ Statement(" AND ") ~ right.toStatement(precendence)

    override private[db] def precendence = 3

    override private[db] def multiple = true
  }

  def ||(right: Where) = new Where {
    override private[db] def toStatement: Statement = self.toStatement(precendence) ~ Statement(" OR ") ~ right.toStatement(precendence)

    override private[db] def precendence = 1

    override private[db] def multiple = true
  }

  private[db] def toStatement(parentPrecedence: Int): Statement = if (multiple && parentPrecedence > precendence) Statement("(") ~ toStatement ~ Statement(")") else toStatement

  private[db] def toStatement: Statement

  private[db] def multiple = false

  private[db] def precendence = 0
}

trait OrderBy {
  self =>
  def ++(right: OrderBy) = new OrderBy {
    override private[db] def toStatement: Statement = self.toStatement ~ Statement(",") ~ right.toStatement
  }

  private[db] def toStatement: Statement
}

private case class Statement(sql: String, parameters: List[(Column[_], Any)] = Nil) {
  def ~(right: Statement) = Statement(sql + right.sql, parameters ::: right.parameters)

  def execute(connection: Connection): Unit = timed {preparedStatement(connection).useAndClose {_.execute()}}

  def executeUpdate(connection: Connection): Long = timed {preparedStatement(connection).useAndClose {_.executeUpdate()}}

  def executeQuery(connection: Connection): ResultSet = timed {preparedStatement(connection).executeQuery()}

  def size(connection: Connection) = timed {preparedStatement(connection).useAndClose {_.executeQuery().useAndClose { rs => rs.next(); rs.getLong(1)}}}

  private def preparedStatement(connection: Connection) = {
    val ps = connection.prepareStatement(sql)
    parameters.view.zipWithIndex.foreach { case ((column, value), index) => column.set(ps, index + 1, value)}
    ps
  }

  private def timed[T](f: => T): T = {
    val sw = Stopwatch.createStarted()
    try {
      val ret = f
      Sql.logger.debug(s"Execution successful ($sw): $sql")
      ret
    } catch {
      case e: Exception =>
        Sql.logger.error(s"Execution failed ($sw): $sql")
        throw e
    }
  }
}

trait Query[T <: Product] {

  def also[R <: Product](right: Query[R]): Query[(T, R)]

  def where(where: Where): Query[T]

  def orderBy(orderBy: OrderBy): Query[T]

  def columns: List[Column[_]]

  def where: Where

  def orderBy: OrderBy

  def select(implicit connection: Connection): TraversableOnce[T] with AutoCloseable

  def insert(value: T)(implicit connection: Connection): Unit

  def update(value: T)(implicit connection: Connection): Long

  def toSelectSql: String

  def toInsertSql: String

  def toUpdateSql: String

  def size(implicit connection: Connection): Long

  def from(rs: ResultSet): T
}

trait DeleteFrom {
  def where(where: Where): DeleteFrom

  def delete(implicit connection: Connection): Long

  def toDeleteSql: String
}
