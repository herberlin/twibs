package twibs.demo

import javax.servlet.annotation.WebFilter

import twibs.form.base.FormResponder
import twibs.web._

@WebFilter(urlPatterns = Array("/*"))
class DemoFilter extends Filter {
  override def createCombiningResponder() = new FilterResponder(this) {
    override def contentResponders(): List[Responder] =
      new FormResponder(() => new DemoForm()) ::
        new Page() ::
        super.contentResponders()
  }
}
