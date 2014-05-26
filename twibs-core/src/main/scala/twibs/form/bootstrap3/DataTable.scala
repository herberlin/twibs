/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import scala.xml.{Elem, Unparsed, NodeSeq}
import twibs.db.TableData
import twibs.form.base._
import twibs.util.JavaScript._
import twibs.util.SortOrder
import twibs.util.SortOrder._
import twibs.util.{Pagination, Translator}
import twibs.web.Request

trait DataTable[T] extends ItemContainer {
  def columns: List[Column]

  override def translator: Translator = super.translator.kind("TABLE")

  override def html: NodeSeq =
    <div>
      <nav class="form-inline table-controls-top">
        {pageSizeField.enrichedHtml}
        <span class="display-text">{pageSizeField.formGroupTitle}</span>{queryStringField.enrichedHtml}
      </nav>
      {tableHtml}
    </div>

  private val pageSizeField = new Field("page-size") with SingleSelectField with IntValues with Required with Inline with SubmitOnChange {
    override def computeOptions: List[OptionI] = toOptions(pageSizes)

    override def defaultValues = optionValues(1) :: Nil

    override def titleForValue(value: ValueType): String = value match {
      case Int.MaxValue => t"all-table-items: All"
      case _ => super.titleForValue(value)
    }
  }

  def pageSizes = 10 :: 25 :: 50 :: 100 :: Int.MaxValue :: Nil

  private val queryStringField = new Field("search") with StringValues with SearchField with Inline with Result {
    override def inputCssClasses = "submit-while-typing" :: super.inputCssClasses

    override def formGroupCssClasses: List[String] = "pull-right" :: super.formGroupCssClasses

    override def execute(request: Request): Unit =
      if (request.parameters.getStringsOption(name + "-submit-while-typing").isDefined)
        result = InsteadOfFormDisplay(refreshTableData)

    override def itemIsVisible: Boolean = searchable
  }

  def searchable = true

  def refreshTableData = jQuery(tableId).call("html", NodeSeq.seqToNodeSeq(tableHtml.child))

  private val offsetField = new Field("page-navigation") with LongValues {
    override def html: NodeSeq = inputsAsHtml

    override def inputAsEnrichedHtml(input: Input, index: Int): NodeSeq =
      <div class="pagination">
        {displayedElementsText}
      </div> ++
        <ul class="pagination pull-right">
          {pagination.pages.map {
          page =>
            <li>
              {if (page.disabled || isDisabled)
              <span>
                {page.title}
              </span>
            else
              <a name={name} value={valueToString(page.firstElementNumber)} href="#">
                {page.title}
              </a>
                .addClass(!isDisabled, "submit")}
            </li>
              .addClass(page.disabled, "disabled")
              .addClass(page.active, "active")
        }}
        </ul> ++ HiddenInputRenderer(fallbackName, string)

    private def displayedElementsText: String =
      if (pagination.displayedElementCount < pagination.totalElementCount)
        if (pagination.displayedElementCount == 0)
          t"filtered-empty: Showing no entries (filtered from ${pagination.totalElementCount})"
        else
          t"filtered: Showing ${pagination.firstElementNumber + 1} to ${pagination.lastElementNumber + 1} of ${pagination.displayedElementCount} (filtered from ${pagination.totalElementCount})"
      else if (pagination.displayedElementCount == 0)
        t"empty: Showing no entries"
      else
        t"unfiltered: Showing ${pagination.firstElementNumber + 1} to ${pagination.lastElementNumber + 1} of ${pagination.displayedElementCount}"

    override def defaultValues = 0L :: Nil

    override def inputAsElem(input: Input): Elem = <span></span>

    override def parse(request: Request): Unit =
      (request.parameters.getStringsOption(name) orElse request.parameters.getStringsOption(fallbackName)).foreach(parse)

    def fallbackName = name + "-fallback"

    def pagination = new Pagination(value, displayedElementCount, totalElementCount, limit)
  }

  def queryString: String = queryStringField.string

  def limit: Int = pageSizeField.value

  def offset: Long = offsetField.value

  final def tableId = id + "-table"

  def tableHtml =
    <div id={tableId}>
      <table class={tableCssClasses}>
        <thead>{tableHead}</thead>
        <tbody>{tableBody}</tbody>
      </table>
      <style>{Unparsed(columnsStyle)}</style>
      <nav class="form-inline clearfix">
        {offsetField.enrichedHtml}
      </nav>
      {columns.map(_.sortField.enrichedHtml)}
    </div>

  def tableCssClasses = "table" :: "table-bordered" :: "table-striped" :: "data-table" :: Nil

  def tableHead: NodeSeq = <tr>{visibleColumns.map(_.tableHeader)}</tr>

  def columnsStyle: String = visibleColumns.zipWithIndex.map(e => e._1.style(e._2)).mkString("")

  def visibleColumns = columns.filter(_.visible)

  def tableBody: NodeSeq = tableData.rows.map(tableRow).toList.flatten

  trait Column {
    def name: String

    def visible = true

    def wrap = true

    def sortable = false

    def tableHeader = <th class={cssClasses}>{title}</th>
      .set(sortable, "name", setSort.name)
      .set(sortable, "value", sortField.string)
      .addClass(sortable, "submit")

    def title: NodeSeq = titleString

    def titleString = translator.usage("column").usage(name).translate("column-title", name)

    private def cssClasses = sortCssClass :: Nil

    private def sortCssClass = sortField.value match {
      case NotSortable => ""
      case Ascending => "sort-asc"
      case Descending => "sort-desc"
      case Unsorted => "sort"
    }

    def style(index: Int) = wrapStyle(index) + (sortField.value match {
      case Ascending | Descending =>
        s"#${tableId.string} > table > tbody > tr:nth-child(odd) > td:nth-child(${index + 1}) { background-color: #eaebff; }" +
          s"#${tableId.string} > table > tbody > tr:nth-child(even) > td:nth-child(${index + 1}) { background-color: #d3d6ff; }"
      case _ => ""
    })

    def wrapStyle(index: Int) = if (wrap) "" else s"#${tableId.string} > table > tbody > tr > td:nth-child(${index + 1}) { white-space: nowrap; }"

    def sort: SortOrder = sortField.value

    def sort_=(sortOrder: SortOrder) = sortField.value = sortOrder

    private[DataTable] val sortField = new HiddenInput("sort") with EnumerationValues[SortOrder.type] {
      override def enumeration = SortOrder

      override def defaultValues = (if (sortable) Unsorted else NotSortable) :: Nil

      def toggle(): Unit = {
        value match {
          case NotSortable => // Ignored
          case Ascending => value = Descending
          case _ => value = Ascending
        }
      }
    }

    private val setSort = new Executor("set-sort") {
      override def execute(parameters: Seq[String]): Unit = {
        columns.filterNot(_ == Column.this).foreach(_.sortField.reset())
        sortField.toggle()
      }
    }
  }

  case class NamedColumn(name: String) extends Column

  trait NormalWhiteSpace extends Column {
    override def style(index: Int): String = super.style(index) + s"#${tableId.string} > table > tbody > tr > td:nth-child(${index + 1}) { white-space: normal; }"
  }

  def tableData: TableData[T]

  def tableRow(entry: T): NodeSeq

  def displayedElementCount = tableData.displayed

  def totalElementCount = tableData.total

  def sortBy: List[(String, SortOrder)] = columns.collect { case c: DataColumn => (c.sortName, c.sort)}

  case class DataColumn(name: String, sortName: String) extends Column {
    def this(name: String) = this(name, name)

    override def sortable: Boolean = true
  }

  object DataColumn {
    def apply(name: String): DataColumn = DataColumn(name, name)
  }

}