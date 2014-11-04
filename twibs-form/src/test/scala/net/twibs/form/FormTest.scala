package net.twibs.form

import scala.xml.PrettyPrinter

import net.twibs.testutil.TwibsTest
import net.twibs.util.{IdString, Message, PrimaryDisplayType}

class FormTest extends TwibsTest {
  test("Invalid name throws exception") {
    new Form("login") {
      intercept[IllegalArgumentException] {
        new SingleLineField("") with StringInput
      }
      intercept[IllegalArgumentException] {
        new SingleLineField("ab cd") with StringInput
      }
      intercept[IllegalArgumentException] {
        new SingleLineField("test-form-id") with StringInput
      }
      intercept[IllegalArgumentException] {
        new SingleLineField("test-form-modal") with StringInput
      }
      intercept[IllegalArgumentException] {
        new SingleLineField("application-name") with StringInput
      }
    }

    intercept[IllegalArgumentException] {
      new Form("")
    }
    intercept[IllegalArgumentException] {
      new Form("ab cd")
    }
    intercept[IllegalArgumentException] {
      new Form("test-form-id")
    }
    intercept[IllegalArgumentException] {
      new Form("test-form-modal")
    }
    intercept[IllegalArgumentException] {
      new Form("application-name")
    }
  }

  test("Form is not a child of itself") {
    val form = new Form("test")
    form.children.contains(form) should beFalse
  }

  test("Test unique names") {
    val form = new Form("test") {
      val field = new SingleLineField("field") with StringInput

      val container = new StaticContainer("level1") {
        val field = new SingleLineField("field") with StringInput

        val container = new StaticContainer("level2") {
          val field = new SingleLineField("field") with StringInput
        }
      }
    }

    form.field.name should not be form.container.field.name
    form.field.name should not be form.container.container.field.name
    form.container.field.name should not be form.container.container.field.name
    form.container.container.field.name should endWith("field2")
    form.container.container.field.id should be(IdString(s"${form.id.string}_level1_level2_field2"))
  }

  test("Validation on different states") {
    val f = new Form("f") {
      val enabled = new SingleLineField("enabled") with StringInput {
        override protected def computeValid: Boolean = false
      }
      val disabled = new SingleLineField("disabled") with StringInput {
        override protected def computeDisabled: Boolean = true

        override protected def computeValid: Boolean = false
      }
      val hidden = new SingleLineField("hidden") with StringInput {
        override protected def computeHidden: Boolean = true

        override protected def computeValid: Boolean = false
      }
      val ignored = new SingleLineField("ignored") with StringInput {
        override protected def computeIgnored: Boolean = true

        override protected def computeValid: Boolean = false
      }
    }

    f.enabled.isValid should beTrue
    f.disabled.isValid should beTrue
    f.hidden.isValid should beTrue
    f.ignored.isValid should beTrue

    f.process(
      Map(
        "enabled" -> Seq("e"),
        "disabled" -> Seq("d"),
        "hidden" -> Seq("h"),
        "ignored" -> Seq("i")
      ))

    f.validate()

    f.enabled.isValid should beFalse
    f.disabled.isValid should beTrue
    f.hidden.isValid should beTrue
    f.ignored.isValid should beTrue
  }

  test("Dynamic values validation") {
    val form = new Form("test") {
      val dynamics = new DynamicContainer("uploads") {

        trait UserContainer extends Dynamic {
          val username = new SingleLineField("username") with StringInput

          val password = new SingleLineField("password") with StringInput
        }

        type T = UserContainer

        override def minimumNumberOfDynamics: Int = 1

        override def maximumNumberOfDynamics: Int = 2

        override def create(dynamicId: String): UserContainer with Dynamic = new Dynamic("user", dynamicId) with UserContainer with Detachable
      }
    }

    form.dynamics.isValid should beTrue
    form.dynamics.messages should be('empty)

    form.process(Map("uploads" -> Seq("e", "b", "c")))
    form.dynamics.validate() should beFalse
    form.dynamics.messages(0).toString should be("warning: Please provide no more than 2 children")

    form.process(Map("uploads" -> Seq()))
    form.dynamics.validate() should beFalse
    form.dynamics.messages(0).toString should be("warning: Please provide at least one child")

    form.reset()
    form.dynamics.isValid should beTrue
  }

  test("Execution called") {
    var submitted = false

    val form = new Form("test") {
      new SingleLineField("exec") with StringInput {
        override def execute() = submitted = true
      }
    }

    form.process(Map("any" -> Seq("1")))
    submitted should beFalse

    form.process(Map("exec" -> Seq("1")))
    submitted should beTrue
  }

  test("Demonstrate implicit hierarchy") {
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
  test("Validate hierarchy is correct") {
    val form = new Form("test") {

      class CustomContainer extends StaticContainer("custom")

      val container = new StaticContainer("level1") {
        val container = new StaticContainer("level2") {
          val field = new SingleLineField("field") with StringInput

          val dynamics = new DynamicContainer("uploads") {

            trait UserContainer extends Dynamic {
              val username = new SingleLineField("username") with StringInput

              val password = new SingleLineField("password") with StringInput

              def check2(): Unit = children should have size 2
            }

            type T = UserContainer

            override def create(dynamicId: String): UserContainer with Dynamic = new Dynamic("user", dynamicId) with UserContainer

            def check0(): Unit = children should have size 0

            def check2(): Unit = children should have size 2
          }

          def check(): Unit = children should have size 2
        }

        def check(): Unit = children should have size 1
      }

      val custom = new CustomContainer()

      def check(): Unit = children should have size 2
    }

    form.check()
    form.container.check()
    form.container.container.check()
    form.container.container.dynamics.check0()

    form.components should have size 6

    form.container.container.dynamics.create("a")
    form.container.container.dynamics.create("b")

    form.components should have size 12

    form.container.container.dynamics.dynamics.foreach(_.check2())
    form.container.container.dynamics.check2()

    form.container.container.dynamics.reset()
    form.container.container.dynamics.check0()

    form.components should have size 6

    form.container.container.check()
  }

  test("Focus") {
    val f = new Form("test", Map("test-form-id" -> Seq("a"), "test-form-modal" -> Seq("false"))) with Bootstrap3Form {
      val hl = new HorizontalLayout {
        val f = new SingleLineField("field") with StringInput
      }
    }

    f.hl.f.strings = "a" :: "" :: Nil
    f.validate()
    f.hl.f.needsFocus should beTrue
    f.focusJs.toString should be("$('#a_hl_field_1').focus()")

    // Entries are valid but field must only contain one value!
    f.hl.f.strings = "a" :: "b" :: Nil
    f.validate()
    f.hl.f.needsFocus should beTrue
    f.focusJs.toString should be("$('#a_hl_field').focus()")
  }

  test("Form renderer") {
    val out = new Form("test", Map("test-form-id" -> Seq("a"), "test-form-modal" -> Seq("false"))) with Bootstrap3Form {

      new DisplayText("<h3>Display text</h3>")

      val dynamics = new DynamicContainer("users") {

        trait UserContainer extends Dynamic {
          val username = new SingleLineField("username") with StringInput

          val password = new SingleLineField("password") with StringInput
        }

        type T = UserContainer

        override def minimumNumberOfDynamics: Int = 1

        override def maximumNumberOfDynamics: Int = 2

        override def create(dynamicId: String): UserContainer with Dynamic = new Dynamic("user", dynamicId) with UserContainer with Detachable
      }

      new StaticContainer("easy") with Detachable {
        new Button("wait") with StringInput with PrimaryDisplayType with DefaultButton
        new Hidden("hidden") with StringInput
      }

      dynamics.create("admin")

      override def messages: Seq[Message] = warn"invalid: Fill out form" +: warn"invalid: Fill out form".copy(dismissable = false) +: super.messages
    }.html

    println(new PrettyPrinter(2000, 4).format(out.head))
    new PrettyPrinter(2000, 4).format(out.head) should be(
      """<form id="a_form" name="test" class="twibs-form" action="/forms/net/twibs/form/Form" method="post" enctype="multipart/form-data">
        |    <input type="hidden" autocomplete="off" name="test-form-id" value="a"/>
        |    <input type="hidden" autocomplete="off" name="test-form-modal" value="false"/>
        |    <input type="hidden" autocomplete="off" name="application-name" value="default"/>
        |    <div class="modal transfer-modal">
        |        <div class="modal-dialog">
        |            <div class="modal-content">
        |                <div class="modal-header">
        |                    <h4 class="modal-title">Transfering data ...</h4>
        |                </div>
        |                <div class="modal-body">
        |                    <div class="progress progress-striped active">
        |                        <div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
        |                            <span class="sr-only">
        |                                <span class="transfer-percent">0</span>
        |                                % Complete
        |                            </span>
        |                        </div>
        |                    </div>
        |                </div>
        |            </div>
        |        </div>
        |    </div>
        |    <header class="form-header">
        |        <h3>test</h3>
        |    </header>
        |    <input type="submit" class="concealed" tabindex="-1" name="wait" value=""/>
        |    <div id="a_shell" class="form-container-shell">
        |        <div id="a" class="form-container">
        |            <div class="alert alert-warning alert-dismissable">
        |                <button type="button" class="close" data-dismiss="alert">×</button>
        |                Fill out form
        |            </div>
        |            <div class="alert alert-warning">Fill out form</div>
        |            <h3>Display text</h3>
        |            <div id="a_users_shell" class="form-container-shell">
        |                <div id="a_users" class="form-container">
        |                    <div id="a_users_user_shell" class="detachable form-container-shell">
        |                        <button type="button" class="close" data-toggle="popover" data-html="true" data-placement="auto left" data-title="Delete component?" data-content='<button type="button" class="btn btn-danger" data-dismiss="detachable">Delete</button>'>&times;</button>
        |                        <div id="a_users_user" class="form-container">
        |                            <input class="can-be-disabled" type="text" name="adminusername" id="a_users_user_adminusername" placeholder="username" value=""/>
        |                            <input class="can-be-disabled" type="text" name="adminpassword" id="a_users_user_adminpassword" placeholder="password" value=""/>
        |                        </div>
        |                    </div>
        |                    <input type="hidden" autocomplete="off" name="users" value="admin"/>
        |                </div>
        |            </div>
        |            <div id="a_easy_shell" class="detachable form-container-shell">
        |                <button type="button" class="close" data-toggle="popover" data-html="true" data-placement="auto left" data-title="Delete component?" data-content='<button type="button" class="btn btn-danger" data-dismiss="detachable">Delete</button>'>&times;</button>
        |                <div id="a_easy" class="form-container">
        |                    <button type="submit" name="wait" id="a_easy_wait" class="can-be-disabled btn btn-primary" value="">wait</button>
        |                    <input type="hidden" autocomplete="off" name="hidden" value=""/>
        |                </div>
        |            </div>
        |        </div>
        |    </div>
        |</form>""".stripMargin)
  }

  test("Inheritance") {
    class C {
      class F

      def check(any: Any) = any.isInstanceOf[F]
    }

    class CC extends C {
      class F extends super.F
    }

    class CCC extends CC {
      class F extends super.F
    }

    new C {
      check(new F) should beTrue
    }
    new CC {
      check(new F) should beTrue
    }
    new CCC {
      check(new F) should beTrue
    }
  }
}
