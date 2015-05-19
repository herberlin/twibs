/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.webtest

import net.twibs.form._
import net.twibs.util._

class RadioTestForm extends Form("radio") with HorizontalForm {
  override def formTitleHtml = <h1>{formTitleString}</h1>

  val mode = new RadioField("mode") with StringInput with SubmitOnChange with RadioInlineLayout {
    override def options = "enabled" :: "disabled" :: "hidden" :: "ignored" :: Nil

    override def defaults: Seq[ValueType] = "enabled" :: Nil
  }

  val hl = new HorizontalLayout {
    override protected def selfIsDisabled: Boolean = mode.string == "disabled"

    override protected def selfIsHidden: Boolean = mode.string == "hidden"

    override protected def selfIsIgnored: Boolean = mode.string == "ignored"

    >> {<h3>Single Select Fields</h3>}
    new SingleSelectField("single-select-multiple-values") with StringInput with SubmitOnChange {
      override def options = "a" :: "b" :: Nil

      override def execute(): Seq[Result] =
        if (isSubmittedOnChange) AfterFormDisplay(info"pressed: Single select changed: $string".showNotification)
        else Ignored

      override def defaults: Seq[ValueType] = "" :: Nil

      override def minimumNumberOfEntries: Int = 1

      override def maximumNumberOfEntries: Int = 3
    }

    >> {<h3>Chosen select</h3>}
    new SingleSelectField("chosen-single-select-multiple-values") with StringInput with SubmitOnChange with Chosen {
      override def options = "a" :: "b" :: Nil

      override def execute(): Seq[Result] =
        if (isSubmittedOnChange) AfterFormDisplay(info"pressed: Single select changed: $string".showNotification)
        else Ignored

      override def defaults: Seq[ValueType] = "" :: Nil

      override def minimumNumberOfEntries: Int = 1

      override def maximumNumberOfEntries: Int = 3
    }

    >> {<h3>Single Line Fields</h3>}
    new SingleLineField("single-line-multiple-values") with StringInput with SubmitOnChange {
      override def execute(): Seq[Result] =
        if (isSubmittedOnChange) AfterFormDisplay(info"pressed: Single select changed: $string".showNotification)
        else Ignored

      override def defaults: Seq[ValueType] = "" :: Nil

      override def minimumNumberOfEntries: Int = 1

      override def maximumNumberOfEntries: Int = 3
    }

    >> {<h3>Multi Line Fields</h3>}
    new MultiLineField("multi-line-multiple-values") with StringInput with SubmitOnChange {
      override def maximumNumberOfEntries: Int = 3
    }

    >> {<h3>Html Fields</h3>}
    new HtmlField("html-multiple-values") with StringInput with SubmitOnChange {
      override def defaults: Seq[ValueType] = "" :: "" :: Nil

      override def maximumNumberOfEntries: Int = 3
    }

    >> {<h3>Radio Buttons</h3>}
    val multipleValues = new RadioField("radio-multiple-values") with StringInput with SubmitOnChange {
      override def options = "a" :: "b" :: Nil

      override def execute(): Seq[Result] =
        if (isSubmittedOnChange) AfterFormDisplay(info"pressed: Radio button changed: $string".showNotification)
        else Ignored

      override def defaults: Seq[ValueType] = "a" :: Nil

      override def minimumNumberOfEntries: Int = 1

      override def maximumNumberOfEntries: Int = 3
    }

    val singleValue = new RadioField("radio-single-value") with StringInput with SubmitOnChange {
      override def options = "a" :: "b" :: Nil

      override def execute(): Seq[Result] =
        if (isSubmittedOnChange) AfterFormDisplay(info"pressed: Radio button changed: $string".showNotification)
        else Ignored

      override def defaults: Seq[ValueType] = "a" :: "" :: Nil

      override def minimumNumberOfEntries: Int = 1

      override def maximumNumberOfEntries: Int = 1
    }

    val inlineLayout = new RadioField("radio-single-value-inline") with StringInput with SubmitOnChange with RadioInlineLayout {
      override def options = "a" :: "b" :: Nil

      override def execute(): Seq[Result] =
        if (isSubmittedOnChange) AfterFormDisplay(info"pressed: Radio button changed: $string".showNotification)
        else Ignored

      override def defaults: Seq[ValueType] = "a" :: "" :: Nil

      override def minimumNumberOfEntries: Int = 1

      override def maximumNumberOfEntries: Int = 1
    }

  }
  new Button("submit") with SimpleButton with PrimaryDisplayType with ExecuteValidated with DefaultButton
}


class TestForm extends Form("test") with HorizontalForm {
  val openModal = new Button("open-modal") with OpenModalLinkButton with PrimaryDisplayType

  >> {<h3>Buttons</h3>}
  >> {<h4>Four simple buttons with the states enabled, disabled, hidden and ignored (two are shown)</h4>}
  new Button("enabled") with SimpleButton with PrimaryDisplayType
  new Button("disabled") with SimpleButton with PrimaryDisplayType {
    override protected def selfIsDisabled: Boolean = true
  }
  new Button("hidden") with SimpleButton with PrimaryDisplayType {
    override protected def selfIsHidden: Boolean = true
  }
  new Button("ignored") with SimpleButton with PrimaryDisplayType {
    override protected def selfIsIgnored: Boolean = true
  }

  >> {<h4>One button with three different values</h4>}
  >> {<p>There is a control label, the first button takes its label, icon and display-type from the values of application.conf,
     the second and third take the defaults.</p>}
  new Button("button-with-three-values") with StringInput with Options with DefaultDisplayType {
    override def options: Seq[String] = "a" :: "b" :: "c" :: Nil

    override def execute(): Seq[Result] =
      if (validate()) AfterFormDisplay(info"pressed: Pressed ''$value''".showNotification)
      else AfterFormDisplay(warn"invalid: Invalid Value selected ''$string''".showNotification)
  }

  >> {<h4>A button row containing two buttons.</h4>}
  >> {<p>Clicking the first produces an Internal Server Error, clicking the second
      waits 2 seconds before returning. Transfer modal should show up.</p>}
  new ButtonRow {
    new Button("internal-server-error") with SimpleButton with DangerDisplayType {
      override def execute(): Seq[Result] = throw new RuntimeException("Internal Server Error")
    }
    new Button("wait-2-seconds") with SimpleButton with WarningDisplayType {
      override def execute(): Seq[Result] = Thread.sleep(2000)
    }
  }

  val floatingButton = new Button("floating") with StringInput with DefaultDisplayType with Floating with DynamicOptions {
    override def execute(): Seq[Result] = AfterFormDisplay(info"pressed: Pressed $string".showNotification)
  }

  >> {<h4>Use floating buttons to display inside html</h4>}
  >> {<p>First Button with value 1 {floatingButton.withOption("1")(_.html)}. Second {floatingButton.withOption("2")(_.html)} has value 2</p>}

  >> {<h3>Popover</h3>}
  >> {<h4>Clicking the next button shows a popover containing another button</h4>}


  // TODO: Enabled Popover again
  //    new Popover("popover") with WarningDisplayType {
  //      new Button("popover-button") with PrimaryDisplayType with StringInput {
  //        override def execute(): Seq[Result] = AfterFormDisplay(info"pressed: Popover button pressed".showNotification)
  //      }
  //    }

  >> {<h3>Fields</h3>}
  >> {<h4>Four simple input fields (only two of them are shown) one is rendered hidden</h4>}

  val enabled = new SingleLineField("enabled") with StringInput
  enabled.strings = "" :: "" :: Nil
  //    enabled.validate()

  >> {<h5>Even though the disabled field has values and is validated no validation information is displayed</h5>}
  val disabled = new SingleLineField("disabled") with StringInput {
    override protected def computeDisabled: Boolean = true
  }
  disabled.strings = "" :: "" :: Nil
  disabled.validate()

  new SingleLineField("hidden") with StringInput {
    override protected def computeHidden: Boolean = true
  }
  new SingleLineField("ignored") with StringInput {
    override protected def computeIgnored: Boolean = true
  }

  new SingleSelectField("single-select") with StringInput with Chosen with Optional {
    override def options: Seq[ValueType] = "Dear" :: "Bear" :: "Lion" :: Nil
  }

  new MultiSelectField("multi-select") with StringInput with Chosen {
    override def options: Seq[ValueType] = "Dear" :: "Bear" :: "Lion" :: Nil
  }

  new MultiSelectField("multi-select") with StringInput with Chosen with Optional {
    override def options: Seq[ValueType] = "Dear" :: "Bear" :: "Lion" :: Nil
  }

  >> {<h4>Multiline</h4>}
  new MultiLineField("multiline") with StringInput

  new HtmlField("html") with StringInput

  >> {<h4>Checkboxes</h4>}
  >> {<p>Simple boolean checkbox with submit on change</p>}
  new CheckboxField("boolean-checkbox") with BooleanCheckboxField with SubmitOnChange

  >> {<p>Enabled with two options. First label configured in application.conf</p>}
  new CheckboxField("checkbox-enabled") with StringInput {
    override def options = "a" :: "b" :: Nil
  }

  >> {<p>Disabled with one option.</p>}
  new CheckboxField("checkbox-disabled") with StringInput {
    override def options = "a" :: Nil

    override protected def selfIsDisabled: Boolean = true
  }

  >> {<h3>Modal</h3>}
  >> {<h4>Open a copy of this form in a modal dialog</h4>}
  >> {new TestForm().openModal.html}
}
