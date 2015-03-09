/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.io.{IOException, ObjectInputStream}

import scala.concurrent.duration._
import scala.ref.WeakReference

trait Memo[T] extends Serializable {
  def apply(): T

  def asOption: Option[T]

  def reset(): Unit

  def recomputeAfter(duration: Duration): Memo[T]
}

object Memo {
  implicit def apply[T](f: => T): Memo[T] = new ConcreteMemo(f)
}

object WeakMemo {
  implicit def apply[T <: AnyRef](f: => T): Memo[T] = new WeakMemo(f)
}

private trait CachedMemo[T] extends Memo[T] {
  def apply() = synchronized {if (isComputeCacheNeeded) computeCache() else asOption.get}

  def isComputeCacheNeeded = asOption.isEmpty

  def computeCache(): T
}

private class ConcreteMemo[T](compute: => T) extends CachedMemo[T] {
  @transient
  var cache: Option[T] = None

  def reset(): Unit = synchronized {cache = None}

  def computeCache() = {
    val ret = compute
    cache = Some(ret)
    ret
  }

  def asOption = cache

  override def recomputeAfter(duration: Duration): Memo[T] =
    new ConcreteMemo(compute) with RecomputingMemo[T] {
      override def recomputeAfter: Duration = duration
    }

  @throws(classOf[IOException])
  @throws(classOf[ClassNotFoundException])
  def readObject(in: ObjectInputStream): Unit = {
    in.defaultReadObject()
    cache = None
  }
}

private trait RecomputingMemo[T] extends CachedMemo[T] {
  def recomputeAfter: Duration

  private var timeoutTimestamp = 0L

  override def isComputeCacheNeeded: Boolean = super.isComputeCacheNeeded || System.currentTimeMillis > timeoutTimestamp

  abstract override def computeCache(): T = {
    timeoutTimestamp = System.currentTimeMillis + recomputeAfter.toMillis
    super.computeCache()
  }
}

private class WeakMemo[T <: AnyRef](compute: => T) extends CachedMemo[T] {
  @transient
  var cache: Option[WeakReference[T]] = None

  def reset(): Unit = synchronized {cache = None}

  def computeCache() = {
    val ret = compute
    cache = Some(WeakReference(ret))
    ret
  }

  def asOption = cache.flatMap(_.get)

  override def recomputeAfter(duration: Duration): Memo[T] =
    new WeakMemo(compute) with RecomputingMemo[T] {
      override def recomputeAfter: Duration = duration
    }

  @throws(classOf[IOException])
  @throws(classOf[ClassNotFoundException])
  def readObject(in: ObjectInputStream): Unit = {
    in.defaultReadObject()
    cache = None
  }
}
