/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import twibs.TwibsTest
import twibs.util.XmlUtils._
import twibs.util.{Message, Translator}

import org.threeten.bp.LocalDateTime

class ValuesTest extends TwibsTest {

  trait TestField extends Values {
    override def translator: Translator = Translator
  }

  implicit def w(text: String): Option[Message] = Some(Message.warning(text))

  test("Optional string validation") {
    val field = new TestField with LongValues

    field.validateString("") should be(field.Input("", "", None, None, continue = false))
    field.strings = "" :: Nil
    field.validate() should beTrue
  }

  test("Required string validation") {
    val field = new TestField with StringValues with Required with Untrimmed

    field.validateString("") should be(field.Input("", "", None, "This field is required."))
    field.validateString(" ") should be(field.Input(" ", " ", Some(" ")))
  }

  test("Required string validation with trim") {
    val field = new TestField with StringValues {
      override def required: Boolean = true
    }

    field.validateString("") should be(field.Input("", "", None, "This field is required."))
    field.validateString(" ") should be(field.Input("", " ", None, "This field is required."))
    field.validateString(" r ") should be(field.Input("r", "r", Some("r")))
  }

  test("Minimal length validation") {
    val field = new TestField with StringValues {
      override def minimumLength: Int = 2
    }

    field.validateString("r") should be(field.Input("r", "r", None, "Please enter at least 2 characters."))
    field.validateString("rr") should be(field.Input("rr", "rr", Some("rr")))
  }

  test("Maximum length validation") {
    val field = new TestField with StringValues {
      override def maximumLength: Int = 2
    }

    field.validateString("rr") should be(field.Input("rr", "rr", Some("rr")))
    field.validateString("rrr") should be(field.Input("rrr", "rrr", None, "Please enter no more than 2 characters."))
  }

  test("Regex validation") {
    val field = new TestField with StringValues {
      override def regex = "[0-9]+"
    }

    field.validateString("0123") should be(field.Input("0123", "0123", Some("0123")))
    field.validateString("r") should be(field.Input("r", "r", None, "Please enter a string that matches '[0-9]+'."))
  }

  test("Email address validation") {
    val field = new TestField with EmailAddressValues

    field.validateString("info@example.com") should be(field.Input("info@example.com", "info@example.com", Some("info@example.com")))
    field.validateString("info @example.com") should be(field.Input("info @example.com", "info @example.com", Some("info @example.com"), "Please enter a valid email address."))
    field.strings = "mb@example.com" :: "noemail" :: Nil
    field.strings should be("mb@example.com" :: "noemail" :: Nil)
    field.values should be("mb@example.com" :: "noemail" :: Nil)
    field.validValues should be("mb@example.com" :: Nil)
  }

  test("Web address validation") {
    val field = new TestField with WebAddressValues

    field.validateString("http://www.example.com") should be(field.Input("http://www.example.com", "http://www.example.com", Some("http://www.example.com")))
    field.validateString("http://www") should be(field.Input("http://www", "http://www", Some("http://www"), "Bitte geben Sie eine gültige URL ein."))
  }

  test("Long validation") {
    val field = new TestField with LongValues {
      override def minimum = 0L

      override def maximum = 9
    }

    field.strings = "a" :: "-1" :: "1" :: "10" :: "9" :: Nil
    field.values should be(-1L :: 1L :: 10L :: 9L :: Nil)
    field.validValues should be(1L :: 9L :: Nil)
  }

  test("Date time validation") {
    val field = new TestField with DateTimeValues {
      override def minimum = LocalDateTime.of(2000, 12, 1, 12, 12)

      override def maximum = LocalDateTime.of(2000, 12, 24, 12, 12)
    }

    field.strings = "01.12.2000 12:11:00" :: "01.12.2000 12:12:00" :: "xx" :: "24.12.2000 12:12:00" :: "24.12.2000 12:13:00" :: Nil
    field.values should have size 4
    field.validValues should have size 2
    field.strings should be("01.12.2000 12:11:00" :: "01.12.2000 12:12:00" :: "xx" :: "24.12.2000 12:12:00" :: "24.12.2000 12:13:00" :: Nil)
  }

  test("Options validation") {
    val field = new TestField with LongValues with Options with Required {
      override def computeOptions: List[OptionI] = toOptions(1L :: 3L :: 8L :: Nil)
    }

    field.strings = "1" :: "2" :: "3" :: Nil
    field.values should be(1L :: 2L :: 3L :: Nil)
    field.validValues should be(1L :: 3L :: Nil)

    field.useEmptyOption("2") should beTrue
    field.useEmptyOption("1") should beFalse
  }

  test("Options with titles") {
    val field = new TestField with LongValues with Options with Required with TranslatedValueTitles {
      override def computeOptions: List[OptionI] = toOptions(1L :: Nil)

      def optionShouldBe = OptionI("1", "One", 1L)
    }

    field.options should be(List(field.optionShouldBe))
  }

  test("String options with string values") {
    val field = new TestField with StringValues with Options with Required {
      override def computeOptions: List[OptionI] =
        new OptionI("1", "One", "one", true) ::
          new OptionI("3", "Three", "three", true) :: Nil
    }
    field.value = "three"
    field.string should be("3")
    field.string = "1"
    field.value should be("one")
    field.titleForValue("one") should be("One")
    field.titleForValue("any") should be("any")
  }

  test("Empty strings are padded") {
    val input = new TestField with LongValues
    input.strings should have size 1
  }

  test("Default values are always padded to minimum value size") {
    val field = new TestField with DoubleValues {
      override def minimumNumberOfInputs: Int = 3
    }

    field.inputs.size should be(3)
    field.strings.size should be(3)
    field.values.size should be(0)

    field.strings = "" :: Nil
    field.strings.size should be(1)
    field.values.size should be(0)
  }

  test("Double value format") {
    val field = new TestField with DoubleValues

    field.string = "3,0"
    field.string should be("3,00")

    field.strings = "3,0" :: "4,0" :: Nil
    field.string should be("3,00")
  }

  test("Check enumeration values") {
    object X extends Enumeration {
      val A, B = Value
    }

    import X._

    val field = new TestField with EnumerationValues[X.type] {
      override def enumeration = X
    }

    field.values = A :: B :: Nil
    field.strings should be("0" :: "1" :: Nil)

    field.strings = "2" :: "1" :: Nil
    field.values should be(B :: Nil)
  }

  test("Single input value") {
    val input = new TestField with LongValues
    input.values = 3L :: 2L :: 1L :: Nil
    input.computeIsValid should beFalse
    input.values should be(3L :: 2L :: 1L :: Nil)

    input.values = 3L :: Nil
    input.computeIsValid should beTrue
    input.values should be(3L :: Nil)

    input.strings = "x" :: Nil
    input.computeIsValid should beFalse
    input.values should be('empty)
  }

  test("Modified input") {
    val input = new TestField with LongValues
    input.isModified should beFalse
    input.values should be('empty)
    input.isModified should beFalse

    input.strings = "" :: Nil
    input.isModified should beTrue

    input.values should be('empty)
    input.isModified should beTrue
    input.resetInputs()
    input.values should be('empty)
    input.isModified should beFalse
  }

  test("Value option") {
    val input = new TestField with LongValues
    input.valueOption = Some(1L)
    input.values should be(List(1L))
    input.valueOption = None
    input.values should be('empty)
  }
}
