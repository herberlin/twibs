package twibs.form.base

import org.threeten.bp.LocalDateTime
import twibs.TwibsTest
import twibs.util.Translator

class ValuesTest extends TwibsTest {

  trait TestField extends Values {
    override def translator: Translator = Translator
  }

  test("Optional string validation") {
    val validator = new TestField with LongValues

    validator.validateString("") should be(validator.ValidInput("", "", None))
    validator.strings = "" :: Nil
    validator.validate() should beTrue
  }

  test("Required string validation") {
    val validator = new TestField with StringValues with Required with Untrimmed

    validator.validateString("") should be(validator.InvalidInput("", "", "This field is required.", None))
    validator.validateString(" ") should be(validator.ValidInput(" ", " ", Some(" ")))
  }

  test("Required string validation with trim") {
    val validator = new TestField with StringValues {
      override def required: Boolean = true
    }

    validator.validateString("") should be(validator.InvalidInput("", "", "This field is required.", None))
    validator.validateString(" ") should be(validator.InvalidInput("", " ", "This field is required.", None))
    validator.validateString(" r ") should be(validator.ValidInput("r", "r", Some("r")))
  }

  test("Minimal length validation") {
    val validator = new TestField with StringValues {
      override def minimumLength: Int = 2
    }

    validator.validateString("r") should be(validator.InvalidInput("r", "r", "Please enter at least 2 characters.", None))
    validator.validateString("rr") should be(validator.ValidInput("rr", "rr", Some("rr")))
  }

  test("Maximum length validation") {
    val validator = new TestField with StringValues {
      override def maximumLength: Int = 2
    }

    validator.validateString("rr") should be(validator.ValidInput("rr", "rr", Some("rr")))
    validator.validateString("rrr") should be(validator.InvalidInput("rrr", "rrr", "Please enter no more than 2 characters.", None))
  }

  test("Regex validation") {
    val validator = new TestField with StringValues {
      override def regex = "[0-9]+"
    }

    validator.validateString("0123") should be(validator.ValidInput("0123", "0123", Some("0123")))
    validator.validateString("r") should be(validator.InvalidInput("r", "r", "Please enter a string that matches '[0-9]+'.", None))
  }

  test("Email address validation") {
    val field = new TestField with EmailAddressValues

    field.validateString("info@example.com") should be(field.ValidInput("info@example.com", "info@example.com", Some("info@example.com")))
    field.validateString("info @example.com") should be(field.InvalidInput("info @example.com", "info @example.com", "Please enter a valid email address.", Some("info @example.com")))
    field.strings = "mb@example.com" :: "noemail" :: Nil
    field.strings should be("mb@example.com" :: "noemail" :: Nil)
    field.values should be("mb@example.com" :: Nil)
  }

  test("Web address validation") {
    val validator = new TestField with WebAddressValues

    validator.validateString("http://www.example.com") should be(validator.ValidInput("http://www.example.com", "http://www.example.com", Some("http://www.example.com")))
    validator.validateString("http://www") should be(validator.InvalidInput("http://www", "http://www", "Bitte geben Sie eine gültige URL ein.", Some("http://www")))
  }

  test("Long validation") {
    val field = new TestField with LongValues {
      override def minimum = 0L

      override def maximum = 9
    }

    field.strings = "a" :: "-1" :: "1" :: "10" :: "9" :: Nil
    field.values should be(1L :: 9L :: Nil)
  }

  test("Date time validation") {
    val field = new TestField with DateTimeValues {
      override def minimum = LocalDateTime.of(2000, 12, 1, 12, 12)

      override def maximum = LocalDateTime.of(2000, 12, 24, 12, 12)
    }

    field.strings = "01.12.2000 12:11:00" :: "01.12.2000 12:12:00" :: "xx" :: "24.12.2000 12:12:00" :: "24.12.2000 12:13:00" :: Nil
    field.values should have size 2
    field.strings should be("01.12.2000 12:11:00" :: "01.12.2000 12:12:00" :: "xx" :: "24.12.2000 12:12:00" :: "24.12.2000 12:13:00" :: Nil)
  }

  test("Options validation") {
    val field = new TestField with LongValues with Options with Required {
      override def initialOptions: List[OptionI] = toOptions(1L :: 3L :: 8L :: Nil)
    }

    field.strings = "1" :: "2" :: "3" :: Nil
    field.values should be(1L :: 3L :: Nil)

    field.useEmptyOption("2") should beTrue
    field.useEmptyOption("1") should beFalse
  }


  test("Options with titles") {
    val field = new TestField with LongValues with Options with Required with TranslatedOptions {
      override def initialOptions: List[OptionI] = toOptions(1L :: Nil)

      def optionShouldBe = OptionI("1", "One", 1L, true)
    }

    field.options should be(List(field.optionShouldBe))
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
}
