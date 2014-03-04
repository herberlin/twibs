package twibs.web

class StaticSession extends Session with StaticAttributeContainer {
  def invalidate(): Unit = Unit
}
