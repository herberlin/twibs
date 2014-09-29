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
          "disabled" -> Seq("d"),
          "hidden" -> Seq("h"),
          "ignored" -> Seq("i")
        )
    })

    f.enabled.string should be("e")
    f.disabled.string should be("d")
    f.hidden.string should be("h")
    f.ignored.string should be("")

    f.validate()

    f.enabled.isValid should beFalse
    f.disabled.isValid should beTrue
    f.hidden.isValid should beTrue
    f.ignored.isValid should beTrue
  }

  test("Component state") {
    def check(cs: ComponentState, enabled: Boolean, disabled: Boolean, hidden: Boolean, ignored: Boolean) = {
      cs.isEnabled should be(enabled)
      cs.isDisabled should be(disabled)
      cs.isHidden should be(hidden)
      cs.isIgnored should be(ignored)
    }

    check(ComponentState.Enabled, enabled = true, disabled = false, hidden = false, ignored = false)
    check(ComponentState.Disabled, enabled = false, disabled = true, hidden = false, ignored = false)
    check(ComponentState.Hidden, enabled = false, disabled = true, hidden = true, ignored = false)
    check(ComponentState.Ignored, enabled = false, disabled = true, hidden = true, ignored = true)

    check(ComponentState.Enabled ~ ComponentState.Ignored, enabled = false, disabled = true, hidden = true, ignored = true)
    check(ComponentState.Enabled ~ ComponentState.Hidden, enabled = false, disabled = true, hidden = true, ignored = false)
    check(ComponentState.Enabled ~ ComponentState.Disabled, enabled = false, disabled = true, hidden = false, ignored = false)
    check(ComponentState.Enabled.ignoreIf(condition = true), enabled = false, disabled = true, hidden = true, ignored = true)
    check(ComponentState.Enabled.hideIf(condition = true), enabled = false, disabled = true, hidden = true, ignored = false)
    check(ComponentState.Enabled.disableIf(condition = true), enabled = false, disabled = true, hidden = false, ignored = false)
    check(ComponentState.Enabled.ignoreIf(condition = false), enabled = true, disabled = false, hidden = false, ignored = false)
    check(ComponentState.Enabled.hideIf(condition = false), enabled = true, disabled = false, hidden = false, ignored = false)
    check(ComponentState.Enabled.disableIf(condition = false), enabled = true, disabled = false, hidden = false, ignored = false)
    check(ComponentState.Disabled.disableIf(condition = throw new RuntimeException()), enabled = false, disabled = true, hidden = false, ignored = false)
    intercept[RuntimeException] {
      check(ComponentState.Enabled.disableIf(condition = throw new RuntimeException()), enabled = false, disabled = true, hidden = false, ignored = false)
    }
  }
}
