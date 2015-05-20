/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form.inheritance

import net.twibs.testutil.TwibsTest

trait Component

trait Focusable extends Component {
  def focus: String
}

trait Field extends Component with Focusable {
  def focus : String =  {println("Field: " + getClass); "field"}
}

trait SelectField extends Field

trait Chosen extends SelectField {
  override def focus : String =  {println("Chosend: " + getClass); "chosen"}
}

class Child extends Component

class InheritanceTest extends TwibsTest {
  test("Test enhanced inheritance") {
    val f = new SelectField with Chosen {
      override def focus : String =  {println("Instance: " + getClass); "instance"}
    }

    val descendants: List[Component] = List(f)

    println(descendants.collectFirst({ case f: Focusable => f.focus }) getOrElse "")

    println(f.focus)
  }
}
