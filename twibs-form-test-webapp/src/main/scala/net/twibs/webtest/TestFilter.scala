/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.webtest

import javax.servlet.annotation.WebFilter

import net.twibs.form.FormResponder
import net.twibs.web._

@WebFilter(urlPatterns = Array("*.html"))
class TestFilter extends Filter {
  override def createCombiningResponder() = new FilterResponder(this) {
    override def contentResponders(): List[Responder] =
      new FormResponder(() => new TestForm()) ::
        new Page() ::
        super.contentResponders()
  }
}
