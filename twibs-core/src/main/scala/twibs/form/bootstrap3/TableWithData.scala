/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import scala.xml.NodeSeq
import twibs.db.TableData
import twibs.form.bootstrap3.SortOrder._

trait TableWithData[T] extends Table {
  def tableBody: NodeSeq = tableData.rows.map(tableRow).toList.flatten

  def tableData: TableData[T]

  def tableRow(entry: T): NodeSeq

  override def displayedElementCount = tableData.displayed

  override def totalElementCount = tableData.total

  def sortBy: List[(String, SortOrder)] = columns.collect {case c: DataColumn => (c.sortName, c.sort)}

  case class DataColumn(name: String, sortName: String) extends Column {
    def this(name: String) = this(name, name)

    override def sortable: Boolean = true
  }

  object DataColumn {
    def apply(name: String): DataColumn = DataColumn(name, name)
  }
}
