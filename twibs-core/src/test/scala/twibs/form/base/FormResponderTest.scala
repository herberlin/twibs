/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import twibs.TwibsTest
import twibs.util.JavaScript.{JsCmd, JsEmpty}
import twibs.util.{Message, Parameters}
import twibs.web.{Request, RequestWrapper}

import scala.xml.NodeSeq

class FormResponderTest extends TwibsTest {
  test("Executor is called") {
    var submitted = ""

    val form = new BaseForm {
      new Executor("exec") with StringValues {
        override def execute(): Unit = submitted = strings(0)
      }

      override def displayJs: JsCmd = JsEmpty

      override def accessAllowed: Boolean = true

      override def computeName: String = "form"

      override def renderer: Renderer = new Renderer {
        override def renderMessage(message: Message): NodeSeq = message.text

        override def hiddenInput(name: String, value: String): NodeSeq = <input type="hidden" autocomplete="off" name={name} value={value} />
      }

      override def modalHtml: NodeSeq = NodeSeq.Empty
    }

    val request = new RequestWrapper(Request) {
      override def path: String = form.actionLink

      override def parameters: Parameters = Map("exec" -> Seq("1"))
    }

    new FormResponder(() => form).respond(request)
    submitted should be("1")
  }
}
