package twibs.form.bootstrap3

import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.xml.{Elem, Unparsed, NodeSeq}
import slick.jdbc.GetResult
import slick.jdbc.StaticQuery._
import twibs.form.base._
import twibs.form.bootstrap3.SortOrder._
import twibs.util.JavaScript._
import twibs.util.{Pagination, Translator, SqlUtils}
import twibs.web.Request

trait DbTable[ElementType] extends ItemContainer {
  override def prefixForChildNames: String = super.prefixForChildNames + name + "-"

  override def translator: Translator = super.translator.kind("TABLE")

  override def html: NodeSeq =
    <div>
      <nav class="form-inline table-controls-top">
        {pageSize.enrichedHtml}
        <span class="display-text">{pageSize.formGroupTitle}</span>
        {search.enrichedHtml}
      </nav>
      {tableHtml}
    </div>

  val pageSize = new Field("page-size") with SingleSelectField with IntValues with Required with Inline with SubmitOnChange {
    override def initialOptions: List[OptionI] = toOptions(10 :: 25 :: 50 :: 100 :: Int.MaxValue :: Nil)

    override def defaultValues = optionValues(1) :: Nil

    override def titleForValue(value: ValueType): String = value match {
      case Int.MaxValue => t"all-table-items: All"
      case _ => super.titleForValue(value)
    }
  }

  val search = new Field("search") with StringValues with SearchField with Inline with Result {
    override def inputCssClasses = "submit-while-typing" :: super.inputCssClasses

    override def formGroupCssClasses: List[String] = "pull-right" :: super.formGroupCssClasses

    override def parse(request: Request): Unit = {
      super.parse(request)
      if (request.parameters.getStringsOption(name + "-submit-while-typing").isDefined)
        result = InsteadOfFormDisplay(updateTableData)
    }
  }

  def updateTableData = jQuery(tableId).call("html", NodeSeq.seqToNodeSeq(tableHtml.child))

  final def tableId = id + "-table"

  def tableHtml =
    <div id={tableId}>
      <table class={tableCssClasses}>
        <thead>{tableHead}</thead>
        <tbody>{tableBody}</tbody>
      </table>
      <style>{Unparsed(columnsStyle)}</style>
      <nav class="form-inline clearfix">
        {displayedElementsNodeSeq}{pageNavigation.enrichedHtml}
      </nav>
      {columns.map(_.innerSort.enrichedHtml)}
    </div>

  def columnsStyle = visibleColumns.zipWithIndex.map(e => e._1.style(e._2)).mkString("")

  def tableCssClasses = "table" :: "table-bordered" :: "table-striped" :: "sortable" :: Nil

  def displayedElementsNodeSeq = <div class="pagination">{displayedElementsText}</div>

  def displayedElementsText: String =
    if (pagination.displayedElementCount < pagination.totalElementCount)
      if (pagination.displayedElementCount == 0)
        t"pagination-filtered-empty: Showing no entries (filtered from ${pagination.totalElementCount})"
      else
        t"pagination-filtered: Showing ${pagination.firstElementNumber + 1} to ${pagination.lastElementNumber + 1} of ${pagination.displayedElementCount} (filtered from ${pagination.totalElementCount})"
    else if (pagination.displayedElementCount == 0)
      t"pagination-empty: Showing no entries"
    else
      t"pagination-unfiltered: Showing ${pagination.firstElementNumber + 1} to ${pagination.lastElementNumber + 1} of ${pagination.displayedElementCount}"

  lazy val pagination = new Pagination(pageNavigation.value, displayedElementCount, totalElementCount, pageSize.value)

  def tableHead: NodeSeq = <tr>{ visibleColumns.map(_.tableHeader) }</tr>

  def tableBody: NodeSeq

  trait Column {
    def name: String

    def title: NodeSeq

    def value: (ElementType) => NodeSeq

    def searchableString: String

    def sortable: Boolean

    def searchable: Boolean = true

    def visible: Boolean = true

    def titleNodeSeq = title

    def orderName = name

    def sortOrder: SortOrder = innerSort.value

    def wrap = true

    def tableHeader = <th class={cssClasses}>{titleNodeSeq}</th>
      .set(sortable, "name", setSort.name)
      .set(sortable, "value", innerSort.string)
      .addClass(sortable, "submit")

    private def cssClasses = sortCssClass :: Nil

    def style(index: Int) = wrapStyle(index) + (innerSort.value match {
      case Ascending | Descending =>
        s"#${tableId.string} > table > tbody > tr:nth-child(odd) > td:nth-child(${index + 1}) { background-color: #eaebff; }" +
          s"#${tableId.string} > table > tbody > tr:nth-child(even) > td:nth-child(${index + 1}) { background-color: #d3d6ff; }"
      case _ => ""
    })

    def wrapStyle(index: Int) = if (wrap) "" else s"#${tableId.string} > table > tbody > tr > td:nth-child(${index + 1}) { white-space: nowrap; }"

    import SortOrder._

    private def sortCssClass = innerSort.value match {
      case NotSortable => ""
      case Ascending => "sort-asc"
      case Descending => "sort-desc"
      case Unsorted => "sort"
    }

    val innerSort = new HiddenInput("sort") {
      type ValueType = SortOrder

      override def valueToStringConverter: ValueToStringConverter = value => value.id.toString

      override def stringToValueConverter: StringToValueConverter = string => try {
        Success(SortOrder(string.toInt))
      } catch {
        case e: NumberFormatException => Failure(string, t"format-message: Please enter a valid sort order.")
      }

      override def defaultValues = (if (sortable) Unsorted else NotSortable) :: Nil

      def toggle(): Unit = {
        columns.filterNot(_.sort == this).foreach(_.sort.reset())
        value match {
          case NotSortable => // Ignored
          case Ascending => value = Descending
          case _ => value = Ascending
        }
      }
    }

    val setSort = new Executor("set-sort") {
      override def execute(parameters: Seq[String]): Unit = innerSort.toggle()
    }

    def sort: HiddenInput = innerSort
  }

  def columns: List[Column]

  def visibleColumns = columns.filter(_.visible)

  trait PageNavigationField extends Field with LongValues {
    override def html: NodeSeq = inputsAsHtml

    override def inputAsEnrichedHtml(input: Input, index: Int): NodeSeq =
      <ul class="pagination pull-right">{
        pagination.pages.map {
          page =>
            <li>{
              if (page.disabled || isDisabled)
                <span>{page.title}</span>
              else
                <a name={name} value={valueToStringConverter(page.firstElementNumber)} href="#">{page.title}</a>
                  .addClass(!isDisabled, "submit")
            }</li>
              .addClass(page.disabled, "disabled")
              .addClass(page.active, "active")
        }
      }</ul> ++ HiddenInputRenderer(fallbackName, string)

    override def defaultValues = 0L :: Nil

    override def inputAsElem(input: Input): Elem = <span></span>

    override def parse(request: Request): Unit =
      (request.parameters.getStringsOption(name) orElse request.parameters.getStringsOption(fallbackName)).foreach(parse)

    def fallbackName = name + "-fallback"
  }

  val pageNavigation = new Field("page-navigation") with PageNavigationField

  def limit = pagination.pageSize

  def offset = pagination.firstElementNumber

  def rows(element: ElementType): NodeSeq = row(element)

  def row(element: ElementType): Elem = <tr>{visibleColumns.map(column => <td>{column.value(element)}</td>)}</tr>

  case class StringColumn(name: String, title: NodeSeq, value: (ElementType) => NodeSeq, sortable: Boolean = true) extends Column {
    def searchableString: String = name
  }

  case class TransientColumn(title: NodeSeq, value: (ElementType) => NodeSeq) extends Column {
    def name: String = ""

    def searchableString: String = ""

    override def searchable: Boolean = false

    def sortable: Boolean = false
  }

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

  def toLikeClause(column: Column, likeString: String) = s"${column.searchableString} ILIKE '%$likeString%'"

  def selectedColumnsSql = selectedColumns.mkString(",")

  def selectedColumns: List[String] = columns.filterNot(_.name.isEmpty).map(_.name) ::: dataColumnNames

  def dataColumnNames: List[String] = Nil

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
