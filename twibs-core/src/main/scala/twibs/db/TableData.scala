/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.db

import scala.slick.util.CloseableIterator

case class TableData[T](pageSize: Int,
                        start: Long,
                        end: Long,
                        displayed: Long,
                        total: Long,
                        rows: CloseableIterator[T])
