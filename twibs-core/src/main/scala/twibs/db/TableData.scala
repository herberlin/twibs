/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.db

case class TableData[T](pageSize: Int,
                        start: Long,
                        end: Long,
                        displayed: Long,
                        total: Long,
                        rows: Iterator[T])
