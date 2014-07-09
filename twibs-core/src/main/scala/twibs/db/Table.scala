package twibs.db

import java.sql._

import twibs.util.Loggable
import twibs.util.Predef._
import twibs.util.SortOrder.SortOrder
import twibs.util.ThreeTenTransition._

import com.google.common.base.Stopwatch
import org.threeten.bp.{LocalDate, LocalDateTime}

object Sql extends Loggable

class Table(val tableName: String) {
  protected implicit def table: Table = this

  case class StringColumn(name: String, default: String = "") extends Column[String] {
    def get(rs: ResultSet, pos: Int) = Option(rs.getString(pos)) getOrElse default

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setString(pos, value.asInstanceOf[String])

    def like(right: String): Where = new ColumnWhere("LIKE", right)
  }

  case class StringOptionColumn(name: String) extends Column[Option[String]] with OptionalColumn {
    def get(rs: ResultSet, pos: Int) = Option(rs.getString(pos))

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[String]] match {
      case Some(value) => ps.setString(pos, value)
      case None => ps.setNull(pos, Types.VARCHAR)
    }
  }

  case class LongColumn(name: String, default: Long = 0L) extends Column[Long] {
    def get(rs: ResultSet, pos: Int) = rs.getLong(pos) match {case r if rs.wasNull() => default case r => r }

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setLong(pos, value.asInstanceOf[Long])
  }

  case class LongOptionColumn(name: String) extends Column[Option[Long]] with OptionalColumn {
    def get(rs: ResultSet, pos: Int) = rs.getLong(pos) match {case r if rs.wasNull() => None case r => Some(r) }

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[Long]] match {
      case Some(value) => ps.setLong(pos, value)
      case None => ps.setNull(pos, Types.BIGINT)
    }
  }

  case class IntColumn(name: String, default: Int = 0) extends Column[Int] {
    def get(rs: ResultSet, pos: Int) = rs.getInt(pos) match {case r if rs.wasNull() => default case r => r }

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setInt(pos, value.asInstanceOf[Int])
  }

  case class IntOptionColumn(name: String) extends Column[Option[Int]] with OptionalColumn {
    def get(rs: ResultSet, pos: Int) = rs.getInt(pos) match {case r if rs.wasNull() => None case r => Some(r) }

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[Int]] match {
      case Some(value) => ps.setInt(pos, value)
      case None => ps.setNull(pos, Types.BIGINT)
    }
  }

  case class BooleanColumn(name: String, default: Boolean = false) extends Column[Boolean] {
    def get(rs: ResultSet, pos: Int) = rs.getBoolean(pos) match {case r if rs.wasNull() => default case r => r }

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setBoolean(pos, value.asInstanceOf[Boolean])
  }

  case class DoubleColumn(name: String, default: Double = 0d) extends Column[Double] {
    def get(rs: ResultSet, pos: Int) = rs.getDouble(pos) match {case r if rs.wasNull() => default case r => r }

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setDouble(pos, value.asInstanceOf[Double])
  }

  case class DoubleOptionColumn(name: String) extends Column[Option[Double]] with OptionalColumn {
    def get(rs: ResultSet, pos: Int) = rs.getDouble(pos) match {case r if rs.wasNull() => None case r => Some(r) }

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[Double]] match {
      case Some(value) => ps.setDouble(pos, value)
      case None => ps.setNull(pos, Types.DOUBLE)
    }
  }

  case class LocalDateTimeColumn(name: String, default: LocalDateTime = LocalDateTime.MIN) extends Column[LocalDateTime] {
    def get(rs: ResultSet, pos: Int) = Option(rs.getTimestamp(pos)).fold(default)(_.toLocalDateTime)

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setTimestamp(pos, value.asInstanceOf[LocalDateTime].toTimestamp)
  }

  case class LocalDateTimeOptionColumn(name: String) extends Column[Option[LocalDateTime]] with OptionalColumn {
    def get(rs: ResultSet, pos: Int) = Option(rs.getTimestamp(pos)).map(_.toLocalDateTime)

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[LocalDateTime]] match {
      case Some(value) => ps.setTimestamp(pos, value.toTimestamp)
      case None => ps.setNull(pos, Types.TIMESTAMP)
    }
  }

  case class LocalDateColumn(name: String, default: LocalDate = LocalDate.MIN) extends Column[LocalDate] {
    def get(rs: ResultSet, pos: Int) = Option(rs.getDate(pos)).fold(default)(_.toLocalDate)

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setDate(pos, value.asInstanceOf[LocalDate].toDate)
  }

  case class LocalDateOptionColumn(name: String) extends Column[Option[LocalDate]] with OptionalColumn {
    def get(rs: ResultSet, pos: Int) = Option(rs.getDate(pos)).map(_.toLocalDate)

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[LocalDate]] match {
      case Some(value) => ps.setDate(pos, value.toDate)
      case None => ps.setNull(pos, Types.DATE)
    }
  }

  case class EnumColumn[T <: Enumeration](name: String, enum: T, defaultIndex: Int = 0) extends Column[T#Value] {
    def get(rs: ResultSet, pos: Int) = enum(rs.getInt(pos) match { case r if rs.wasNull() => defaultIndex case r => r})

    def set(ps: PreparedStatement, pos: Int, value: Any) = ps.setInt(pos, value.asInstanceOf[T#Value].id)
  }

  case class EnumOptionColumn[T <: Enumeration](name: String, enum: T) extends Column[Option[T#Value]] {
    def get(rs: ResultSet, pos: Int) = rs.getInt(pos) match {case r if rs.wasNull() => None case r => Some(enum(r)) }

    def set(ps: PreparedStatement, pos: Int, valueOption: Any) = valueOption.asInstanceOf[Option[T#Value]] match {
      case Some(value) => ps.setInt(pos, value.id)
      case None => ps.setNull(pos, Types.INTEGER)
    }
  }

  def size(implicit connection: Connection) = Statement(s"SELECT count(*) FROM $tableName").size(connection)
}

class AggregateColumn[T](delegatee: Column[T]) extends Column[T]()(delegatee.table) {
  override def set(ps: PreparedStatement, pos: Int, value: Any): Unit = throw new IllegalArgumentException("Aggregate columns can not be set")

  override def name: String = delegatee.name

  override def get(rs: ResultSet, pos: Int): T = delegatee.get(rs, pos)
}

abstract class Column[T](implicit val table: Table) {
  self =>
  def name: String

  def fullName: String = table.tableName + "." + name

  private[db] def sget(rs: ResultSet, pos: Int): T = try get(rs, pos) catch {case e: Exception => Sql.logger.error(s"Retrieving value $fullName failed"); throw e}

  def get(rs: ResultSet, pos: Int): T

  def set(ps: PreparedStatement, pos: Int, value: Any): Unit

  protected class ColumnWhere(operator: String, right: T) extends Where {
    private[db] override def toStatement = Statement(s"$fullName $operator ?", (self, right) :: Nil)
  }

  protected class ColumnSeqWhere(operator: String, right: Seq[T]) extends Where {
    private[db] override def toStatement = Statement(s"$fullName $operator ?", (self, right) :: Nil)
  }

  protected class ColumnToColumn(operator: String, right: Column[T]) extends Where {
    private[db] override def toStatement = Statement(s"$fullName $operator ${right.fullName}", Nil)
  }

  def >(right: T): Where = new ColumnWhere(">", right)

  def <(right: T): Where = new ColumnWhere("<", right)

  def =!=(right: T): Where = new ColumnWhere("<>", right)

  def ===(right: T): Where = new ColumnWhere("=", right)

  def ===(right: Column[T]): Where = new ColumnToColumn("=", right)

  def in(vals: Seq[T]): Where = new Where {
    private[db] override def toStatement = Statement(s"$fullName in (${vals.map(_ => "?").mkString(",")})", vals.map(right => (self, right)))
  }

  def asc: OrderBy = new OrderBy {
    override def toStatement = Statement(s"$fullName ASC")
  }

  def desc: OrderBy = new OrderBy {
    override def toStatement = Statement(s"$fullName DESC")
  }

  def max: Column[T] = new AggregateColumn[T](this) {
    override def fullName = s"max(${super.fullName})"
  }

  def sum: Column[T] = new AggregateColumn[T](this) {
    override def fullName = s"sum(${super.fullName})"
  }
}

trait OptionalColumn {
  self: Column[_] =>
  def isNotNull = new Where {
    private[db] override def toStatement = Statement(s"$fullName IS NOT NULL")
  }

  def isNull = new Where {
    private[db] override def toStatement = Statement(s"$fullName IS NULL")
  }
}

trait SqlFunction[T] extends Column[T] {
  override def fullName: String = name
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

private case class Statement(sql: String, parameters: Seq[(Column[_], Any)] = Nil) {
  def ~(right: Statement) = Statement(sql + right.sql, parameters ++ right.parameters)

  def insert(connection: Connection): Unit = timed {preparedStatement(connection).useAndClose {_.execute()}}

  def insertAndReturn[R](connection: Connection)(column: Column[R]): R = timed {
    returningStatement(connection).useAndClose { ps =>
      ps.execute()
      ps.getGeneratedKeys useAndClose { generatedKeys =>
        if (generatedKeys.next()) {
          val md = generatedKeys.getMetaData
          val pos = (for (i <- 1 to md.getColumnCount if md.getColumnName(i) == column.name) yield i).headOption getOrElse 1
          column.get(generatedKeys, pos)
        } else {
          throw new SQLException("No generated key returned")
        }
      }
    }
  }

  def update(connection: Connection): Long = timed {preparedStatement(connection).useAndClose {_.executeUpdate()}}

  def select(connection: Connection): ResultSet = timed {preparedStatement(connection).executeQuery()}

  def size(connection: Connection) = timed {preparedStatement(connection).useAndClose {_.executeQuery().useAndClose { rs => rs.next(); rs.getLong(1)}}}

  private def preparedStatement(connection: Connection) = setParameters(connection.prepareStatement(sql))

  private def returningStatement(connection: Connection) = setParameters(connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS))

  private def setParameters(ps: PreparedStatement) = {
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

  def orderBy(orderBys: List[OrderBy]): Query[T]

  def orderByName(orderBy: List[(String, SortOrder)]): Query[T]

  def join(left: Column[_], right: Column[_]): Query[T]

  def groupBy(column: Column[_]): Query[T]

  def offset(offset: Long): Query[T]

  def limit(limit: Long): Query[T]

  def distinct: Query[T]

  def columns: List[Column[_]]

  def where: Where

  def orderBy: OrderBy

  def select(implicit connection: Connection): Iterator[T] with AutoCloseable

  def firstOption(implicit connection: Connection): Option[T] = {
    val s = select(connection)
    val it = s.toIterator
    val ret = if (it.hasNext) Some(it.next()) else None
    s.close()
    ret
  }

  def first(implicit connection: Connection): T = firstOption(connection).get

  def insert(value: T)(implicit connection: Connection): Unit

  def insertAndReturn[R](value: T)(column: Column[R])(implicit connection: Connection): R

  def update(value: T)(implicit connection: Connection): Long

  def toSelectSql: String

  def toInsertSql: String

  def toUpdateSql: String

  def size(implicit connection: Connection): Long

  def isEmpty(implicit connection: Connection): Boolean = size(connection) == 0

  def convert[R <: Product](to: (T) => R, from: (R) => Option[T] = (x: R) => None): Query[R]
}

private[db] class AutoCounter {
  private var count = 0

  def apply() = {
    count += 1
    count
  }
}

trait DeleteFrom {
  def where(where: Where): DeleteFrom

  def delete(implicit connection: Connection): Long

  def toDeleteSql: String
}
