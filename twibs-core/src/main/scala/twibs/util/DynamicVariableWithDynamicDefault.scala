package twibs.util

import scala.util.DynamicVariable

class DynamicVariableWithDynamicDefault[T](createFallback: => T) extends DynamicVariableWithDefault[T] {
  def default = actives.headOption getOrElse fallback

  private var actives: List[T] = Nil

  def activate(what: T) = actives = what :: actives

  def deactivate(what: T) = actives = actives.filterNot(_ == what)

  def fallback = cachedFallback

  private lazy val cachedFallback = createFallback

  override def toString(): String = "DynamicVariableWithDynamicDefault(" + current + ")"
}

abstract class DynamicVariableWithDefault[T] extends DynamicVariable[Option[T]](None) {
  implicit def unwrap(companion: DynamicVariableWithDefault[T]): T = current

  def current = value getOrElse default

  def use[R](newValue: T)(f: => R): R = withValue(Some(newValue))(f)

  def default: T

  override def toString(): String = "DynamicVariableWithDefault(" + current + ")"
}
