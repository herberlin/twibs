/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

object CollectionUtils {
  def zipToMap[A, B](seq: Seq[(A, B)]) = seq.groupBy(_._1) mapValues (_.map(_._2).toSeq)
}
