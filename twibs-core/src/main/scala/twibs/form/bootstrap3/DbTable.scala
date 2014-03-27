package twibs.form.bootstrap3

import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.xml.{Elem, NodeSeq}
import slick.jdbc.GetResult
import slick.jdbc.StaticQuery._
import twibs.form.base._
import twibs.form.bootstrap3.SortOrder._
import twibs.util.SqlUtils

trait DbTable[ElementType] extends Table {

  trait DbColumn extends Column {
    def value: (ElementType) => NodeSeq

    def searchableString: String

    def searchable: Boolean = true

    def orderName = name
  }

  def rows(element: ElementType): NodeSeq = row(element)

  def row(element: ElementType): Elem = <tr>{visibleColumns.collect {case column: DbColumn => <td>{column.value(element)}</td>}}</tr>

  case class StringDbColumn(name: String, title2: NodeSeq, value: (ElementType) => NodeSeq, override val sortable: Boolean = true) extends DbColumn {
    def searchableString: String = name
  }

  case class TransientDbColumn(title2: NodeSeq, value: (ElementType) => NodeSeq) extends DbColumn {
    def name: String = ""

    def searchableString: String = ""

    override def searchable: Boolean = false

    override def sortable: Boolean = false
  }

  def whereSql = constraintSql match {case "" => "" case l => s"WHERE $l" }

  def constraintSql = conditions.filterNot(_.isEmpty).map(s => s"($s)").mkString(" AND ")

  def staticConditionSql = staticCondition match {case "" => "" case l => s"WHERE $l" }

  def conditions: List[String] = staticCondition :: searchCondition :: Nil

  def staticCondition = ""

  def fromSql: String

  def searchCondition = queryString.toLowerCase match {
    case "" => ""
    case s =>
      val esc = SqlUtils.escapeForLike(s)
      columns.collect({case column: DbColumn if column.searchable => toLikeClause(column, esc)}).mkString(" OR ")
  }

  def ordering = orderingColumns.filterNot(_.isEmpty).mkString(",") match {
    case "" => ""
    case s => s" ORDER BY $s"
  }

  def orderingColumns: List[String] = columns.collect {
    case column: DbColumn => column.sort match {
      case Ascending => column.orderName + " ASC"
      case Descending => column.orderName + " DESC"
      case _ => ""
    }
  }

  def toLikeClause(column: DbColumn, likeString: String) = s"${column.searchableString} ILIKE '%$likeString%'"

  def selectedColumnsSql = selectedColumns.mkString(",")

  def selectedColumns: List[String] = columns.filterNot(_.name.isEmpty).map(_.name) ::: dataColumnNames

  def dataColumnNames: List[String] = Nil

  lazy val totalElementCount: Long = sql"""SELECT count(*) FROM #$fromSql #$staticConditionSql""".as[Long].first

  lazy val displayedElementCount: Long = if (whereSql.isEmpty) totalElementCount else sql"""SELECT count(*) FROM #$fromSql #$whereSql""".as[Long].first

  def query = sql"""SELECT #$selectedColumnsSql FROM #$fromSql #$whereSql #$ordering #$limitSql #$offsetSql"""

  def limitSql = limit match {case x if x > 0 => s"LIMIT $x" case _ => "" }

  def offsetSql = offset match {case x if x > 0 => s"OFFSET $x" case _ => "" }

  case class DateDbColumn(name: String, title2: NodeSeq, value: (ElementType) => NodeSeq, override val sortable: Boolean = true) extends DbColumn {
    def searchableString: String = dateToString(this)

    override def wrap: Boolean = false
  }

  case class TimestampDbColumn(name: String, title2: NodeSeq, value: (ElementType) => NodeSeq, override val sortable: Boolean = true) extends DbColumn {
    def searchableString: String = timestampToString(this)

    override def wrap: Boolean = false
  }

  case class CurrencyDbColumn(name: String, title2: NodeSeq, value: (ElementType) => NodeSeq, override val sortable: Boolean = true) extends DbColumn {
    def searchableString: String = currencyToString(this)

    override def wrap: Boolean = false
  }

  case class WeightDbColumn(name: String, title2: NodeSeq, value: (ElementType) => NodeSeq, override val sortable: Boolean = true) extends DbColumn {
    def searchableString: String = weightToString(this)

    override def wrap: Boolean = false
  }

  case class BooleanDbColumn(name: String, title2: NodeSeq, value: (ElementType) => NodeSeq, override val sortable: Boolean = true) extends DbColumn {
    def searchableString: String = booleanToString(this)
  }

  def booleanToString(column: DbColumn) = s"""case when ${column.name} then 'Ja' else 'Nein' end"""

  def currencyToString(column: DbColumn) = s"""to_char(${column.name}, '999G999G999D00 "EUR"')"""

  def weightToString(column: DbColumn) = s"""to_char(${column.name}, '999G999G999D00 "kg"')"""

  def dateToString(column: DbColumn) = s"""to_char(${column.name}, 'dd.MM.yy')"""

  def timestampToString(column: DbColumn) = s"""to_char(${column.name}, 'dd.MM.yy HH24:MI:SS')"""
}

trait ElementDbTable[ElementType] extends DbTable[ElementType] {
  def tableBody: NodeSeq = elements.map(rows).flatten

  def elements: List[ElementType] = elementsCache

  val elementsCache = new LazyCacheItem(query.as(toResult).list())

  def toResult: GetResult[ElementType]
}
