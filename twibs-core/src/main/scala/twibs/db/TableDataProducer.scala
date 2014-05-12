/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.db

//TODO: Replace PostgresDriver with generic type once someone knows how ;)
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.slick.lifted.Query
import twibs.form.bootstrap3.SortOrder
import twibs.form.bootstrap3.SortOrder.SortOrder
import twibs.util.SqlUtils

object TableDataProducer {
  def apply[T <: NamedColumns, E, C[_]](query: Query[T, E, C], queryString: String, offset: Long, limit: Int, sortBy: List[(String, SortOrder)], whereLike: (String) => Query[T, E, C]) = {
    val total = Query(query.length).first

    val (displayed, whereQuery) = if (!queryString.trim.isEmpty) {
      val ret = whereLike("%" + SqlUtils.escapeForLike(queryString.trim).toLowerCase + "%")
      (Query(ret.length).first, ret)
    } else {
      (total, query)
    }

    val start = Math.min(displayed - displayed % limit, offset)

    val end = Math.min(displayed, start + limit)

    var ret = whereQuery

    sortBy.reverse.foreach {
      case (name, SortOrder.Ascending) => ret = ret.sortBy(_.findColumnByName(name).asc)
      case (name, SortOrder.Descending) => ret = ret.sortBy(_.findColumnByName(name).desc)
      case _ =>
    }

    ret = ret.drop(start.toInt).take(limit)

    new TableData(limit, start, end, displayed, total, ret.iterator)
  }
}
