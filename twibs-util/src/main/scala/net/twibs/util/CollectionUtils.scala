/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

object CollectionUtils {
  def group[A, B](seq: Seq[(A, B)]) = seq.groupBy(_._1) mapValues (_.map(_._2).toSeq)

  def ungroup[A, B](map: Map[A, Seq[B]]) = map.toSeq.flatMap { case (k, vs) => vs.map(k -> _) }

  def ungroupArray[A, B](map: Map[A, Array[B]]) = map.toSeq.flatMap { case (k, vs) => vs.map(k -> _) }
}
