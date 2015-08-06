/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import scala.collection.mutable

object GraphUtils {
  def breadthFirstSearch[T](root: T)(traverse: T => Seq[T]): Stream[Seq[T]] = {
    val visited = mutable.Set[T]()
    breadthFirstSearchVisited(root)(traverse)((node: T) => !visited.add(node))
  }

  def breadthFirstSearchVisited[T](root: T)(traverse: T => Seq[T])(visited: T => Boolean): Stream[Seq[T]] = {
    def rec(items: Seq[Seq[T]]): Stream[Seq[T]] = items match {
      case s if s.isEmpty => Stream[Seq[T]]()
      case node +: nodes =>
        if (visited(node.head)) rec(nodes)
        else node #:: rec(nodes ++ traverse(node.head).map(_ +: node))
    }
    rec(Seq(Seq(root)))
  }

  def depthFirstSearch[T](root: T)(traverse: T => Seq[T]): Stream[Seq[T]] = {
    val visited = mutable.Set[T]()
    depthFirstSearchVisited(root)(traverse)((node: T) => !visited.add(node))
  }

  def depthFirstSearchVisited[T](root: T)(traverse: T => Seq[T])(visited: T => Boolean): Stream[Seq[T]] = {
    def rec(items: Seq[Seq[T]]): Stream[Seq[T]] = items match {
      case s if s.isEmpty => Stream[Seq[T]]()
      case node +: nodes =>
        if (visited(node.head)) rec(nodes)
        else rec(traverse(node.head).map(_ +: node)) #::: node #:: rec(nodes)
    }
    rec(Seq(Seq(root)))
  }
}
