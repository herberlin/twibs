/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.form

import net.twibs.testutil.TwibsTest
import net.twibs.util.{IdGenerator, Translator}

class InputTest extends TwibsTest {

  trait CleanTranslator extends Input {
    override final val translator: Translator = Translator.current.kind(IdGenerator.next())
  }

  trait TestLongInput extends LongInput with CleanTranslator

  def createInput = new TestLongInput {
    override def maximumNumberOfEntries = 2
  }

  test("Default entries") {
    val input = createInput
    input.entries should have size 1
    input.valid should beFalse
  }

  test("Set values is always valid") {
    val input = createInput
    input.values = 1L :: 2L :: Nil
    input.valid should beTrue
  }

  test("Set strings is always validating") {
    val input = createInput
    input.strings = "1" :: "2" :: "3" :: Nil
    input.valid should beFalse
    input.entries.forall(_.valid) should beTrue
    input.messageOption should not be 'empty
  }

  test("Test minimum number of entries") {
    val input = createInput
    input.strings = Nil
    input.valid should beFalse
    input.messageOption.get.toString should be("warning: Please enter at least one value")
  }

  test("Test maximum number of entries") {
    val input = createInput
    input.strings = "1" :: "2" :: "3" :: Nil
    input.valid should beFalse
    input.messageOption.get.toString should be("warning: Please enter no more than 2 values")
  }

  test("Filling defaults with empty strings") {
    new TestLongInput {
      override def minimumNumberOfEntries = 2
    }.entries should have size 2
  }

  test("Changed") {
    val input = new TestLongInput {}
    input.isChanged should beFalse

    input.values = 1L :: Nil
    input.isChanged should beTrue

    input.values = input.defaults
    input.isChanged should beFalse

    input.strings = Nil
    input.isChanged should beFalse
  }

  test("Modified by values") {
    val input = new TestLongInput {}
    input.isModified should beFalse

    input.values = 1L :: Nil
    input.isModified should beTrue
  }

  test("Modified by strings") {
    val input = new TestLongInput {}
    input.strings = "1" :: Nil
    input.isModified should beTrue
  }

  test("Options") {
    val input = new TestLongInput with Options {
      override def options = 2L :: 4L :: Nil
    }

    intercept[FormException] {input.value = 3L}
    input.value = 2L

    input.string = "2"
    input.valid should beTrue

    input.string = "failure"
    input.valid should beFalse
    input.entries.head.messageOption.get.toString should be("warning: 'failure' is not an option")
  }

  test("Required by default") {
    val input = new TestLongInput {}

    input.string = ""
    input.entries(0).valid should beFalse
    input.entries(0).messageOption.get.toString should be("warning: Please enter a value")

    input.string = "x"
    input.valid should beFalse
    input.entries(0).messageOption.get.toString should be("warning: Invalid format for string 'x'")
  }

  test("Optional") {
    val input = new TestLongInput with Optional

    input.string = ""
    input.entries(0).messageOption should be(None)
    input.entries(0).valid should beTrue

    input.string = "x"
    input.valid should beFalse
    input.entries(0).messageOption.get.toString should be("warning: Invalid format for string 'x'")
  }

  test("Trimmed by default") {
    val input = new TestLongInput {}

    input.string = " 1 "
    input.valid should beTrue
    input.value should be(1L)
    input.string should be("1")

    input.string = " x "
    input.valid should beFalse
    input.string should be("x")
  }

  test("Untrimmed") {
    val input = new TestLongInput with Untrimmed

    input.string = " 1 "
    input.string should be(" 1 ")
  }

  test("Single line input does not contain line breaks") {
    val input = new SingleLineInput() with CleanTranslator
    input.string = "ab \n\r "
    input.valid should beTrue
    input.value should be("ab")

    input.string = "a\nb"
    input.valid should beFalse
  }

  test("Untrimmed Single line input does not remove trailing line breaks") {
    val input = new SingleLineInput() with Untrimmed with CleanTranslator
    input.string = " ab \n\r "
    input.valid should beFalse
    input.string should be(" ab \n\r ")

    input.string = "ab\n\r"
    input.valid should beFalse
    input.string should be("ab\n\r")
  }

  test("Minimum and maximum length") {
    val input = new SingleLineInput() with CleanTranslator {
      override def minimumLength: Int = 2

      override def maximumLength: Int = 4
    }

    input.strings = "a" :: "ab" :: "abcd" :: "abcde" :: Nil
    input.entries(0).messageOption.get.toString should be("warning: Please enter at least 2 characters")
    input.entries(1).messageOption should be('empty)
    input.entries(2).messageOption should be('empty)
    input.entries(3).messageOption.get.toString should be("warning: Please enter no more than 4 characters")
  }

  test("Regex") {
    val input = new SingleLineInput() with CleanTranslator {
      override def regex = "[0-9]+"
    }

    input.strings = "0123" :: "r" :: Nil
    input.entries(0).messageOption should be('empty)
    input.entries(1).messageOption.get.toString should be("warning: Please enter a string that matches '[0-9]+'")
  }

  test("Long validation") {
    val input = new LongInput with CleanTranslator

    input.strings = "a" :: "-1" :: "1" :: "10" :: "9" :: Nil
    input.values should be(-1L :: 1L :: 10L :: 9L :: Nil)
  }

  test("Int validation") {
    val input = new IntInput with CleanTranslator

    input.strings = "a" :: "-1" :: "1" :: "10" :: "9" :: Nil
    input.values should be(-1 :: 1 :: 10 :: 9 :: Nil)
  }

  test("Email address validation") {
    val input = new Input with EmailAddressInput with CleanTranslator

    input.strings = " info @example.com " :: "mb@example.com" :: "noemail" :: Nil
    input.strings should be("info @example.com" :: "mb@example.com" :: "noemail" :: Nil)
    input.values should be("mb@example.com" :: Nil)
    input.entries(0).messageOption.get.toString should be("warning: 'info @example.com' is not a valid email address")
  }

  test("Web address validation") {
    val input = new Object with WebAddressInput with CleanTranslator

    input.strings = "http://www.example.com" :: "http://www" :: Nil
    input.strings should be("http://www.example.com" :: "http://www" :: Nil)
    input.values should be("http://www.example.com" :: Nil)
    input.entries(1).messageOption.get.toString should be("warning: 'http://www' is not a valid web address")
  }

}
