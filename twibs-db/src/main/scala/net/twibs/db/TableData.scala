/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.db

trait TableData[T] {
  def pageSize: Int

  def start: Long

  def end: Long

  def displayed: Long

  def total: Long

  def rows: Iterator[T]
}

object TableData {
  def apply[T](pageSize: Int, start: Long, end: Long, displayed: Long, total: Long, rows: Iterator[T]): TableData[T] =
    new TableDataImpl(pageSize, start, end, displayed, total, rows)

  private case class TableDataImpl[T](pageSize: Int, start: Long, end: Long, displayed: Long, total: Long, rows: Iterator[T]) extends TableData[T]

}
