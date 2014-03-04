package twibs.web

trait AttributeContainer {
  def removeAttribute(name: String): Unit

  def setAttribute(name: String, value: Any): Unit

  def getAttribute(name: String): Option[Any]

  def getAttribute[T <: Any](attributeName: String, default: => T): T =
    getAttribute(attributeName).map(_.asInstanceOf[T]) getOrElse default

  def getAttributeOrStoreDefault[T <: Any](attributeName: String, default: => T): T = {
    getAttribute(attributeName).map(_.asInstanceOf[T]) getOrElse {
      val ret = default
      setAttribute(attributeName, ret)
      ret
    }
  }
}
