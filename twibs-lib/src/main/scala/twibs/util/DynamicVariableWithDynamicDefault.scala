package twibs.util

class DynamicVariableWithDynamicDefault[T](createFallback: => T) {
  implicit def unwrap(companion: DynamicVariableWithDynamicDefault[T]): T = current

  def current = threadLocal.get getOrElse default

  def use[R](newValue: T)(f: => R): R = {
    val oldValue = threadLocal.get
    threadLocal set Some(newValue)

    try f finally threadLocal set oldValue
  }

  private val threadLocal = new InheritableThreadLocal[Option[T]] {override def initialValue = None}

  def default = actives.headOption getOrElse fallback

  private var actives: List[T] = Nil

  def activate(what: T) = actives = what :: actives

  def deactivate(what: T) = actives = actives.filterNot(_ == what)

  def fallback = cachedFallback

  private lazy val cachedFallback = createFallback

  override def toString: String = "DynamicVariableWithDynamicDefault(" + current + ")"
}
