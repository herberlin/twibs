package twibs.form.bootstrap3

import scala.xml.NodeSeq
import slick.jdbc.GetResult
import slick.jdbc.StaticQuery._
import slick.session.Database.threadLocalSession
import twibs.form.base.LazyCacheItem
import twibs.form.bootstrap3.SortOrder._
import twibs.util.SqlUtils

trait SqlTable[ElementType] extends Table[ElementType] {
  def whereSql = constraintSql match {case "" => "" case l => s"WHERE $l" }

  def constraintSql = conditions.filterNot(_.isEmpty).map(s => s"($s)").mkString(" AND ")

  def staticConditionSql = staticCondition match {case "" => "" case l => s"WHERE $l" }

  def conditions: List[String] = staticCondition :: searchCondition :: Nil

  def staticCondition = ""

  def fromSql: String

  def searchCondition = search.string.toLowerCase match {
    case "" => ""
    case s =>
      val esc = SqlUtils.escapeForLike(s)
      columns.collect({case column if column.searchable => toLikeClause(column, esc)}).mkString(" OR ")
  }

  def ordering = orderingColumns.filterNot(_.isEmpty).mkString(",") match {
    case "" => ""
    case s => s" ORDER BY $s"
  }

  def orderingColumns: List[String] = columns.map(column => column.innerSort.value match {
    case Ascending => column.orderName + " ASC"
    case Descending => column.orderName + " DESC"
    case _ => ""
  })

  def toLikeClause(column: Column, likeString: String): String

  def selectedColumnsSql = selectedColumns.mkString(",")

  def selectedColumns: List[String] = columns.filterNot(_.name.isEmpty).map(_.name) ::: dataColumnNames

  def dataColumnNames: List[String] = Nil
}

trait DbTable[ElementType] extends SqlTable[ElementType] {
  lazy val totalElementCount: Long = sql"""SELECT count(*) FROM #$fromSql #$staticConditionSql""".as[Long].first

  lazy val displayedElementCount: Long = if (whereSql.isEmpty) totalElementCount else sql"""SELECT count(*) FROM #$fromSql #$whereSql""".as[Long].first

  def query = sql"""SELECT #$selectedColumnsSql FROM #$fromSql #$whereSql #$ordering #$limitSql #$offsetSql"""

  def limitSql = limit match {case x if x > 0 => s"LIMIT $x" case _ => "" }

  def offsetSql = offset match {case x if x > 0 => s"OFFSET $x" case _ => "" }

  case class DateColumn(name: String, title: NodeSeq, value: (ElementType) => NodeSeq, sortable: Boolean = true) extends Column {
    def searchableString: String = dateToString(this)

    override def wrap: Boolean = false
  }

  case class TimestampColumn(name: String, title: NodeSeq, value: (ElementType) => NodeSeq, sortable: Boolean = true) extends Column {
    def searchableString: String = timestampToString(this)

    override def wrap: Boolean = false
  }

  case class CurrencyColumn(name: String, title: NodeSeq, value: (ElementType) => NodeSeq, sortable: Boolean = true) extends Column {
    def searchableString: String = currencyToString(this)

    override def wrap: Boolean = false
  }

  case class WeightColumn(name: String, title: NodeSeq, value: (ElementType) => NodeSeq, sortable: Boolean = true) extends Column {
    def searchableString: String = weightToString(this)

    override def wrap: Boolean = false
  }

  case class BooleanColumn(name: String, title: NodeSeq, value: (ElementType) => NodeSeq, sortable: Boolean = true) extends Column {
    def searchableString: String = booleanToString(this)
  }

  def booleanToString(column: Column) = s"""case when ${column.name} then 'Ja' else 'Nein' end"""

  def currencyToString(column: Column) = s"""to_char(${column.name}, '999G999G999D00 "EUR"')"""

  def weightToString(column: Column) = s"""to_char(${column.name}, '999G999G999D00 "kg"')"""

  def dateToString(column: Column) = s"""to_char(${column.name}, 'dd.MM.yy')"""

  def timestampToString(column: Column) = s"""to_char(${column.name}, 'dd.MM.yy HH24:MI:SS')"""
}

trait ElementDbTable[ElementType] extends DbTable[ElementType] {
  def tableBody: NodeSeq = elements.map(rows).flatten

  def elements: List[ElementType] = elementsCache

  val elementsCache = new LazyCacheItem(query.as(toResult).list())

  def toResult: GetResult[ElementType]
}

trait H2Table {
  self: SqlTable[_] =>
  def toLikeClause(column: Column, likeString: String) = s"LOWER(${column.searchableString}) LIKE '%$likeString%'"
}

trait PostgresqlTable {
  self: SqlTable[_] =>
  def toLikeClause(column: Column, likeString: String) = s"${column.searchableString} ILIKE '%$likeString%'"
}
