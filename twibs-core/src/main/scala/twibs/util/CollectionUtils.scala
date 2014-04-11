/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

object CollectionUtils {
  def zipToMap[A, B](seq: Seq[(A, B)]) = seq.groupBy(_._1) mapValues (_.map(_._2).toSeq)
}
