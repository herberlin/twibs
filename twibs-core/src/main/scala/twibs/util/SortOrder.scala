/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

object SortOrder extends Enumeration {
  type SortOrder = Value
  val NotSortable = Value
  val Unsorted = Value
  val Ascending = Value
  val Descending = Value
}
