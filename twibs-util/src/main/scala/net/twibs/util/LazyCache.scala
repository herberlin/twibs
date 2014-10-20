/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import concurrent.duration._

trait LazyCache[T] {
  def valueOption: Option[T]

  def value: T

  def reset(): Unit
}

private class ConcreteLazyCache[T](calculate: => T) extends LazyCache[T] {
  private var state: Option[T] = None

  def value: T = synchronized {
    state getOrElse recalculate
  }

  def valueOption = state

  private def recalculate = {
    val ret = calculate
    state = Some(ret)
    ret
  }

  def reset(): Unit = synchronized {
    state = None
  }
}

private class LazyCacheWithTimeout[T](timeoutDuration: Duration = 60 seconds)(calculate: => T) extends LazyCache[T] {
  private var timeoutTimestamp = 0L

  private val cached = LazyCache(calculate)

  def valueOption = cached.valueOption

  def value = {
    if (System.currentTimeMillis > timeoutTimestamp) {
      timeoutTimestamp = System.currentTimeMillis + timeoutDuration.toMillis
      cached.reset()
    }
    cached.value
  }

  def reset(): Unit = cached.reset()
}

object LazyCache {
  def apply[T](timeoutDuration: Duration = 60 seconds)(calculate: => T): LazyCache[T] = new LazyCacheWithTimeout[T](timeoutDuration)(calculate)

  def apply[T](calculate: => T): LazyCache[T] = new ConcreteLazyCache[T](calculate)

  implicit def unwrap[T](lc: LazyCache[T]): T = lc.value
}
