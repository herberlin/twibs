/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

object CollectionUtils {
  def seqToMap[A, B](seq: Seq[(A, B)]) = seq.groupBy(_._1) mapValues (_.map(_._2).toSeq)

  def mapToSeq[A, B](map: Map[A, Seq[B]]) = map.toSeq.flatMap { case (k, vs) => vs.map(k -> _) }

  def mapArrayToSeq[A, B](map: Map[A, Array[B]]) = map.toSeq.flatMap { case (k, vs) => vs.map(k -> _) }
}
