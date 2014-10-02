/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import twibs.TwibsTest
import twibs.form.bootstrap3._

class FormHierarchyTest extends TwibsTest {

  trait UserContainer extends Dynamic {
    val username = new Field("useranem") with StringValues with SingleLineField

    val password = new Field("password") with StringValues with PasswordField

    def check2(): Unit = children should have size 2
  }

  class CustomContainer private(parent: Container, unit: Unit = Unit) extends StaticContainer("custom")(parent) {
    def this()(implicit parent: Container) = this(parent)

    new DisplayHtml("Me")
  }

  test("Form is not a child of itself") {
    val form = new Form("test") {
      override def accessAllowed: Boolean = true
    }
    form.children.contains(form) should beFalse
  }

  test("Validate hierarchy is correct") {
    val form = new Form("test") {
      val container = new StaticContainer("level1") {
        val container = new StaticContainer("level2") {
          val field = new Field("field") with StringValues with SingleLineField

          val dynamics = new DynamicContainer[UserContainer]("uploads") {
            override def create(dynamicId: String): UserContainer with Dynamic = new Dynamic("user", dynamicId) with UserContainer with Detachable

            def check0(): Unit = children should have size 0

            def check2(): Unit = children should have size 2
          }

          def check(): Unit = children should have size 2
        }

        def check(): Unit = children should have size 1
      }

      val custom = new CustomContainer()

      def check(): Unit = children should have size 5

      override def accessAllowed: Boolean = true
    }

    form.check()
    form.container.check()
    form.container.container.check()
    form.container.container.dynamics.check0()

    form.components should have size 10

    form.container.container.dynamics.create("a")
    form.container.container.dynamics.create("b")

    form.components should have size 16

    form.container.container.dynamics.dynamics.foreach(_.check2())
    form.container.container.dynamics.check2()

    form.container.container.dynamics.reset()
    form.container.container.dynamics.check0()

    form.components should have size 10

    form.container.container.check()
  }

  test("Check implicit hierarchy") {
    trait Item {
      def name: String

      override def toString = name
    }

    trait Parent extends Item {
      implicit def thisAsParent = this
    }

    trait Child extends Item {
      def parent: Parent
    }

    trait Node extends Child with Parent

    class IC private(val name: String, val parent: Parent, padPrivateConstructor: Unit) extends Node {
      def this(name: String)(implicit parent: Parent) = this(name, parent, ())
    }

    class U private(parent: Parent, unit: Unit = Unit) extends IC("P")(parent) {
      def this()(implicit parent: Parent) = this(parent, ())

      val f = new F("F")
    }

    class F(val name: String)(implicit val parent: Parent) extends Child

    val root = new Parent {
      val name = "R"
      val ic = new IC("IC") {
        val u = new U {
          val u = new U {
          }
        }
      }
    }

    root.ic.parent should be theSameInstanceAs root
    root.ic.u.parent should be theSameInstanceAs root.ic
    root.ic.u.f.parent should be theSameInstanceAs root.ic.u
    root.ic.u.u.parent should be theSameInstanceAs root.ic.u
    root.ic.u.u.f.parent should be theSameInstanceAs root.ic.u.u
  }

  test("Test unique names") {
    val form = new Form("test") {
      val field = new Field("field") with StringValues with SingleLineField

      val container = new StaticContainer("level1") {
        val field = new Field("field") with StringValues with SingleLineField

        val container = new StaticContainer("level2") {
          val field = new Field("field") with StringValues with SingleLineField
        }
      }

      override def accessAllowed: Boolean = true
    }

    form.field.name should not be form.container.field.name
    form.field.name should not be form.container.container.field.name
    form.container.field.name should not be form.container.container.field.name
  }

  test("Dynamic values validation") {
    val form = new Form("test") {
      val dynamics = new DynamicContainer[UserContainer]("uploads") {
        override def minimumNumberOfDynamics: Int = 1

        override def maximumNumberOfDynamics: Int = 2

        override def create(dynamicId: String): UserContainer with Dynamic = new Dynamic("user", dynamicId) with UserContainer with Detachable
      }

      override def accessAllowed: Boolean = true
    }

    form.dynamics.isValid should beTrue
    form.dynamics.messageOption should be('empty)
    form.dynamics.validate() should beFalse
    form.dynamics.messageOption should not be 'empty
    form.reset()
    form.dynamics.isValid should beTrue
  }
}