package twibs.form.base

import twibs.TwibsTest
import twibs.form.bootstrap3.{SingleLineField, Field, Form}

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
}
