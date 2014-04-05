/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import scala.xml.{Elem, NodeSeq}
import twibs.form.bootstrap3.SortOrder._
import twibs.util.Predef._
import twibs.util.TableData

trait TableWithData[T] extends Table {
  def tableBody: NodeSeq = tableData.rows.useAndClose {it => it.map(tableRow).toList}

  def tableData: TableData[T]

  def tableRow(entry: T): Elem

  override def displayedElementCount = tableData.displayed

  override def totalElementCount = tableData.total

  def sortBy: List[(String, SortOrder)] = columns.collect {case c: DataColumn => (c.sortName, c.sort)}

  case class DataColumn(name: String, sortName: String) extends Column {
    override def sortable: Boolean = true
  }

  object DataColumn {
    def apply(name: String): DataColumn = DataColumn(name, name)
  }

}
