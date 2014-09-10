/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import twibs.TwibsTest
import twibs.form.bootstrap3.{Field, Form, SingleLineField}
import twibs.util.Parameters
import twibs.web.{Request, RequestWrapper}

class FormTest extends TwibsTest {
  test("Unique names") {
    val form = new Form("me") {
      val a = new Field("a") with StringValues with SingleLineField

      new Field("a1") with StringValues with SingleLineField
      val b = new Field("a") with StringValues with SingleLineField

      val c = new Field("a") with StringValues with SingleLineField

      override def accessAllowed: Boolean = true
    }

    form.a.name should be("a")
    form.b.name should be("a2")
    form.c.name should be("a3")
  }

  test("Simple") {
    val f = new Form("f") {
      override def accessAllowed: Boolean = true

      val normal = new Field("n") with StringValues with SingleLineField {
        override def minimumLength: Int = 2
      }
      val disabled = new Field("d") with StringValues with SingleLineField {
        override def state = ComponentState.Disabled

        override def minimumLength: Int = 2
      }
      val hidden = new Field("h") with StringValues with SingleLineField {
        override def state = ComponentState.Hidden

        override def minimumLength: Int = 2
      }
      val ignored = new Field("i") with StringValues with SingleLineField {
        override def state = ComponentState.Ignored

        override def minimumLength: Int = 2
      }
    }

    f.normal.isValid should beTrue
    f.disabled.isValid should beTrue
    f.hidden.isValid should beTrue
    f.ignored.isValid should beTrue

    f.respond(new RequestWrapper(Request) {
      override def parameters: Parameters = Map("n" -> Seq("n"), "d" -> Seq("d"), "h" -> Seq("h"))
    })

    f.normal.string should be("n")
    f.disabled.string should be("d")
    f.hidden.string should be("h")
    f.ignored.string should be("")

    f.validate()

    f.normal.isValid should beFalse
    f.disabled.isValid should beTrue
    f.hidden.isValid should beTrue
    f.ignored.isValid should beTrue
  }
}
