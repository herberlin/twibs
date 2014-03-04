package twibs.web

import javax.servlet.ServletContext

trait FilterResponderTrait extends CombiningResponder {
  override def staticContentResponders(): List[Responder] = servletContextResponder :: super.staticContentResponders()

  def servletContextResponder: ServletContextResponder = new ServletContextResponder(servletContext)

  def servletContext: ServletContext
}

class FilterResponder(val servletContext: ServletContext) extends FilterResponderTrait {
  def this(filter: Filter) {
    this(filter.servletContext)
  }
}
