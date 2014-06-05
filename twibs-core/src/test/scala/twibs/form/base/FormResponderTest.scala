/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import twibs.TwibsTest
import twibs.util.JavaScript.JsCmd
import twibs.util.JavaScript.JsEmpty
import twibs.util.Parameters
import twibs.web.{Request, RequestWrapper}

class FormResponderTest extends TwibsTest {
  test("Executor is called") {
    var submitted = ""

    val form = new BaseForm {
      new Executor("exec") {
        override def execute(parameters: Seq[String]): Unit = submitted = parameters(0)
      }

      override def displayJs: JsCmd = JsEmpty

      override def accessAllowed: Boolean = true

      override def computeName(): String = "form"
    }

    val request = new RequestWrapper(Request) {
      override def path: String = form.actionLink

      override def parameters: Parameters = Map("exec" -> Seq("1"))
    }

    new FormResponder(() => form).respond(request)
    submitted should be("1")
  }
}
