/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import scala.util.DynamicVariable

abstract class DynamicVariableWithDynamicDefault[T] extends DynamicVariableWithDefault[T] {
  def createFallback: T

  def default = actives.headOption getOrElse fallback

  private var actives: List[T] = Nil

  def activate(what: T) = actives = what :: actives

  def deactivate(what: T) = actives = actives.filterNot(_ == what)

  def fallback = cachedFallback

  private lazy val cachedFallback = createFallback

  override def toString(): String = "DynamicVariableWithDynamicDefault(" + current + ")"
}

abstract class DynamicVariableWithDefault[T] extends DynamicVariable[Option[T]](None) with UnwrapCurrent[T] {
  def current = value getOrElse default

  def use[R](newValue: T)(f: => R): R = withValue(Some(newValue))(f)

  def default: T

  override def toString(): String = "DynamicVariableWithDefault(" + current + ")"
}
