/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.db

import scala.slick.driver.JdbcDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.slick.lifted.Query
import twibs.form.bootstrap3.SortOrder
import twibs.form.bootstrap3.SortOrder.SortOrder
import twibs.util.SqlUtils

object TableDataProducer {
  def apply[T <: NamedColumns, E](query: Query[T, E], queryString: String, offset: Long, limit: Int, sortBy: List[(String, SortOrder)], whereLike: (String) => Query[T, E]) = {
    val total = Query(query.length).first

    val (displayed, whereQuery) = if (!queryString.trim.isEmpty) {
      val ret = whereLike("%" + SqlUtils.escapeForLike(queryString.trim).toLowerCase + "%")
      (Query(ret.length).first, ret)
    } else {
      (total, query)
    }

    val start = Math.min(displayed - displayed % limit, offset)

    val end = Math.min(displayed, start + limit)

    var ret = whereQuery.drop(start.toInt).take(limit)

    sortBy.foreach {
      case (name, SortOrder.Ascending) => ret = ret.sortBy(_.findColumnByName(name).asc)
      case (name, SortOrder.Descending) => ret = ret.sortBy(_.findColumnByName(name).desc)
      case _ =>
    }

    new TableData(limit, start, end, displayed, total, ret.iterator)
  }
}
