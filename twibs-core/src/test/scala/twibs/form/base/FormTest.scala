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

      val enabled = new Field("enabled") with StringValues with SingleLineField {
        override def minimumLength: Int = 2
      }
      val disabled = new Field("disabled") with StringValues with SingleLineField {
        override def state = ComponentState.Disabled

        override def minimumLength: Int = 2
      }
      val hidden = new Field("hidden") with StringValues with SingleLineField {
        override def state = ComponentState.Hidden

        override def minimumLength: Int = 2
      }
      val ignored = new Field("ignored") with StringValues with SingleLineField {
        override def state = ComponentState.Ignored

        override def minimumLength: Int = 2
      }
    }

    f.enabled.isValid should beTrue
    f.disabled.isValid should beTrue
    f.hidden.isValid should beTrue
    f.ignored.isValid should beTrue

    f.respond(new RequestWrapper(Request) {
      override def parameters: Parameters =
        Map(
          "enabled" -> Seq("e"),
          "enabled-disabled" -> Seq("e-d"),
          "disabled" -> Seq("d"),
          "disabled-disabled" -> Seq("d-d"),
          "hidden" -> Seq("h"),
          "hidden-disabled" -> Seq("h-d"),
          "ignored" -> Seq("i"),
          "ignored-disabled" -> Seq("i-d")
        )
    })

    f.enabled.string should be("e")
    f.disabled.string should be("d-d")
    f.hidden.string should be("h-d")
    f.ignored.string should be("")

    f.validate()

    f.enabled.isValid should beFalse
    f.disabled.isValid should beTrue
    f.hidden.isValid should beTrue
    f.ignored.isValid should beTrue
  }
}
