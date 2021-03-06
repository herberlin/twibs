package net.twibs.form

import net.twibs.testutil.TwibsTest
import net.twibs.util._

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
        new SingleLineField("t-any") with StringInput
      }
    }

    intercept[IllegalArgumentException] {
      new Form("")
    }
    intercept[IllegalArgumentException] {
      new Form("ab cd")
    }
    intercept[IllegalArgumentException] {
      new Form("t-any")
    }
  }

  test("Form is not a child of itself") {
    val form = new Form("test")
    form.children.contains(form) shouldBe false
  }

  test("Test unique names") {
    val form = new Form("test") {
      val field = new SingleLineField("field") with StringInput

      val container = new ChildContainer("level1") {
        val field = new SingleLineField("field") with StringInput

        val container = new ChildContainer("level2") {
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
      val submit = new Button("submit") with SimpleButton with DefaultDisplayType with ExecuteValidated
    }

    f.enabled.isValid shouldBe true
    f.disabled.isValid shouldBe true
    f.hidden.isValid shouldBe true
    f.ignored.isValid shouldBe true

    f.process(
      Map(
        "enabled" -> Seq("e"),
        "disabled" -> Seq("d"),
        "hidden" -> Seq("h"),
        "ignored" -> Seq("i"),
        "submit" -> Seq("")
      ))

    f.enabled.isValid shouldBe false
    f.disabled.isValid shouldBe true
    f.hidden.isValid shouldBe true
    f.ignored.isValid shouldBe true
  }

  test("Dynamic values validation") {
    val form = new Form("test") {
      val dynamics = new ChildContainer("uploads") with DynamicParent {

        class UserContainer extends ChildContainer("user") with DynamicChild {
          val username = new SingleLineField("username") with StringInput

          val password = new SingleLineField("password") with StringInput
        }

        type T = UserContainer

        override def minimumNumberOfDynamics: Int = 1

        override def maximumNumberOfDynamics: Int = 2

        override def createChild(): UserContainer = new UserContainer with Detachable
      }
    }

    form.dynamics.isValid shouldBe true
    form.dynamics.validationMessageOption should be('empty)

    form.process(Map("uploads" -> Seq("e", "b", "c")))
    form.dynamics.validate() shouldBe false
    form.dynamics.validationMessageOption.get.toString should be("danger: Please provide no more than 2 children")

    form.process(Map("uploads" -> Seq()))
    form.dynamics.validate() shouldBe false
    form.dynamics.validationMessageOption.get.toString should be("danger: Please provide at least one child")

    form.reset()
    form.dynamics.isValid shouldBe true
  }

  test("Execution called") {
    var submitted = false

    val form = new Form("test") {
      new SingleLineField("exec") with StringInput {
        override def execute() = submitted = true
      }
    }

    form.process(Map("any" -> Seq("1")))
    submitted shouldBe false

    form.process(Map("exec" -> Seq("1")))
    submitted shouldBe true
  }

  test("Demonstrate implicit hierarchy") {
    trait Item {
      def name: String

      override def toString = name
    }

    trait Parent extends Item {
      implicit def thisAsParent: Parent = this
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

      class CustomContainer extends ChildContainer("custom")

      val container = new ChildContainer("level1") {
        val container = new ChildContainer("level2") {
          val field = new SingleLineField("field") with StringInput

          val dynamics = new ChildContainer("uploads") with DynamicParent {

            class UserContainer extends ChildContainer("user") with DynamicChild {
              val username = new SingleLineField("username") with StringInput

              val password = new SingleLineField("password") with StringInput

              def check2(): Unit = children should have size (2 + 4) // (+4) One add and one remove button per field
            }

            type T = UserContainer

            override def createChild(): UserContainer = new UserContainer

            def check0(): Unit = children should have size 2

            def check2(): Unit = children should have size 4
          }

          def check(): Unit = children should have size (2 + 2) // (+2) One add and one remove button per field
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

    form.descendants should have size (6 + 4) // (+4) One add and one remove button per field

    DynamicID.use("a") {form.container.container.dynamics.createChild()}
    DynamicID.use("b") {form.container.container.dynamics.createChild()}

    form.descendants should have size (12 + 12) // (+10) One add and one remove button per field

    form.container.container.dynamics.dynamics.foreach(_.check2())
    form.container.container.dynamics.check2()

    form.container.container.dynamics.reset()
    form.container.container.dynamics.check0()

    form.descendants should have size (6 + 4) // (+2) One add and one remove button per field

    form.container.container.check()
  }

  test("Focus") {

    val f = Request.copy(parameters = Map("t-id" -> Seq("a"), "t-modal" -> Seq("false"))).use {
      new Form("test") {
        val hl = new HorizontalLayout {
          val f = new SingleLineField("field") with StringInput
        }
      }
    }

    f.hl.f.strings = "a" :: "" :: Nil
    f.validate()
    f.hl.f.needsFocus shouldBe true
    f.focusJs.toString should be("$('#a_hl_field_1').focus()")

    // Entries are valid but field must only contain one value!
    f.hl.f.strings = "a" :: "b" :: Nil
    f.validate()
    f.hl.f.needsFocus shouldBe true
    f.focusJs.toString should be("$('#a_hl_field').focus()")
  }

//  test("Form renderer") {
//    val out = Request.copy(parameters = Map("t-id" -> Seq("a"), "t-modal" -> Seq("false"))).use {
//      new Form("test") {
//
//        new DisplayText("<h3>Display text</h3>")
//
//        val dynamics = new ChildContainer("users") with DynamicParent {
//
//          class UserContainer extends ChildContainer("user") with DynamicChild {
//            val username = new SingleLineField("username") with StringInput
//
//            val password = new SingleLineField("password") with StringInput
//          }
//
//          type T = UserContainer
//
//          override def minimumNumberOfDynamics: Int = 1
//
//          override def maximumNumberOfDynamics: Int = 2
//
//          override def createChild(): UserContainer = new UserContainer with Detachable
//        }
//
//        new ChildContainer("easy") with Detachable {
//          new Button("wait") with SimpleButton with PrimaryDisplayType with DefaultButton
//          new Hidden("hidden") with StringInput
//        }
//
//        DynamicID.use("admin") {dynamics.createChild()}
//
//        //        override def messages: Seq[Message] = warn"invalid: Fill out form" +: warn"invalid: Fill out form".copy(dismissable = false) +: super.messages
//      }.html
//    }
//
//        println(new PrettyPrinter(2000, 4).format(out.head))
//    new PrettyPrinter(2000, 4).format(out.head) should be(
//      """<form id="a_form" name="test" class="t-form" action="/" method="post" enctype="multipart/form-data">
//        |    <input type="hidden" autocomplete="off" name="t-ilk" value="test"/>
//        |    <input type="hidden" autocomplete="off" name="t-id" value="a"/>
//        |    <input type="hidden" autocomplete="off" name="t-modal" value="false"/>
//        |    <input type="hidden" autocomplete="off" name="t-context" value="default"/>
//        |    <div class="modal transfer-modal">
//        |        <div class="modal-dialog">
//        |            <div class="modal-content">
//        |                <div class="modal-header">
//        |                    <h4 class="modal-title">Transfering data ...</h4>
//        |                </div>
//        |                <div class="modal-body">
//        |                    <div class="progress progress-striped active">
//        |                        <div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
//        |                            <span class="sr-only">
//        |                                <span class="transfer-percent">0</span>
//        |                                % Complete
//        |                            </span>
//        |                        </div>
//        |                    </div>
//        |                </div>
//        |            </div>
//        |        </div>
//        |    </div>
//        |    <header class="form-header">
//        |        <h3>test</h3>
//        |    </header>
//        |    <input type="submit" class="concealed" tabindex="-1" name="wait" value=""/>
//        |    <div id="a_shell" name="test" class="form-container-shell">
//        |        <div id="a" class="form-container">
//        |            <h3>Display text</h3>
//        |            <div id="a_users_shell" name="users" class="dynamic-parent-shell form-container">
//        |                <div id="a_users" class="form-container">
//        |                    <div class="dynamic-child-shell form-container has-actions has-sort-handle" id="a_users_user_shell" name="user">
//        |                        <div id="a_users_user" class="form-container">
//        |                            <div class="control-shell" id="a_users_user_f86dWwHBsJ1Uusername_shell">
//        |                                <div class="entry">
//        |                                    <input class="form-control can-be-disabled" type="text" name="f86dWwHBsJ1Uusername" id="a_users_user_f86dWwHBsJ1Uusername" placeholder="username" value=""/>
//        |                                </div>
//        |                            </div>
//        |                            <div class="control-shell" id="a_users_user_f86dWwHBsJ1Upassword_shell">
//        |                                <div class="entry">
//        |                                    <input class="form-control can-be-disabled" type="text" name="f86dWwHBsJ1Upassword" id="a_users_user_f86dWwHBsJ1Upassword" placeholder="password" value=""/>
//        |                                </div>
//        |                            </div>
//        |                        </div>
//        |                        <div class="actions">
//        |                            <span class="sort-handle fa fa-reorder"></span>
//        |                            <span class="disabled btn btn-default btn-xs">add-dynamic-button</span>
//        |                            <button type="submit" name="remove-dynamic-button" id="a_users_remove-dynamic-button" class="can-be-disabled btn btn-default btn-xs" value="f86dWwHBsJ1U">remove-dynamic-button</button>
//        |                        </div>
//        |                    </div>
//        |                    <input type="hidden" autocomplete="off" name="users" value="f86dWwHBsJ1U"/>
//        |                    <div class="dynamic-child-shell form-container has-actions has-sort-handle" id="a_users_user1_shell" name="user1">
//        |                        <div id="a_users_user1" class="form-container">
//        |                            <div class="control-shell" id="a_users_user1_adminusername_shell">
//        |                                <div class="entry">
//        |                                    <input class="form-control can-be-disabled" type="text" name="adminusername" id="a_users_user1_adminusername" placeholder="username" value=""/>
//        |                                </div>
//        |                            </div>
//        |                            <div class="control-shell" id="a_users_user1_adminpassword_shell">
//        |                                <div class="entry">
//        |                                    <input class="form-control can-be-disabled" type="text" name="adminpassword" id="a_users_user1_adminpassword" placeholder="password" value=""/>
//        |                                </div>
//        |                            </div>
//        |                        </div>
//        |                        <div class="actions">
//        |                            <span class="sort-handle fa fa-reorder"></span>
//        |                            <span class="disabled btn btn-default btn-xs">add-dynamic-button</span>
//        |                            <button type="submit" name="remove-dynamic-button" id="a_users_remove-dynamic-button" class="can-be-disabled btn btn-default btn-xs" value="admin">remove-dynamic-button</button>
//        |                        </div>
//        |                    </div>
//        |                    <input type="hidden" autocomplete="off" name="users" value="admin"/>
//        |                </div>
//        |                <div class="actions">
//        |                    <span class="disabled btn btn-default btn-xs">add-dynamic-button</span>
//        |                </div>
//        |            </div>
//        |            <div id="a_easy_shell" name="easy" class="form-container-shell">
//        |                <div id="a_easy" class="form-container">
//        |                    <button type="submit" name="wait" id="a_easy_wait" class="can-be-disabled btn btn-primary" value="">wait</button>
//        |                    <input type="hidden" autocomplete="off" name="hidden" value=""/>
//        |                </div>
//        |            </div>
//        |        </div>
//        |    </div>
//        |</form>
//        |""".stripMargin)
//  }

  test("Form validation") {
    val f = new Form("a") {
      new SingleLineField("s") with StringInput

      new Button("b") with SimpleButton with PrimaryDisplayType {

      }
    }
    f.parse(Map("s" -> Seq("s"), "b" -> Seq("true")))

    f.validate() shouldBe true
  }

  test("No strings are also allowed") {
    val f = new Form("a") {
      val f = new SingleLineField("s") with StringInput
    }

    f.f.valueOption = None
    f.f.strings should be(Seq())
  }

  test("Link parameters") {
    val f = new Form("a") {
      val field = new SingleLineField("s") with StringInput
      val ignored = new SingleLineField("i") with StringInput {
        override protected def selfIsIgnored: Boolean = true
      }
      val button = new Button("s") with SimpleButton with PrimaryDisplayType
      val link = new Button("open-modal") with OpenModalLinkButton with DefaultDisplayType
    }

    f.field.strings = "1" :: "2" :: Nil
    f.ignored.strings = "5" :: "6" :: Nil
    f.button.strings = "3" :: "4" :: Nil
    f.link.link(Seq("a" -> "b")) should be("/?s=1&s=2&a=b")
  }
}
