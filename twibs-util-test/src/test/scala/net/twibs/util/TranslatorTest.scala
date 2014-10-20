/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import scala.collection.mutable.ListBuffer
import net.twibs.testutil.TwibsTest
import net.twibs.util.Translator._

import com.ibm.icu.util.ULocale

class TranslatorTest extends TwibsTest {
  test("Translation") {
    val checkedKeys = ListBuffer[String]()
    val unresolvedKeys = ListBuffer[String]()

    val root = new TranslatorResolver(ULocale.ENGLISH, Configuration) {
      override def resolve(key: String) = {
        checkedKeys += key
        None
      }

      override def unresolved(key: String, default: String): Unit =
        unresolvedKeys += key + " " + default
    }.root

    root.usage("FORM").usage("login").kind("INPUT").usage("username", "", "name", "name").kind("TEXT").usage("required").translate("message", "This field is required") should be("This field is required")
    checkedKeys.toList.mkString("\n") should be(
      """FORM.login.username.required.message
        |FORM.login.name.required.message
        |login.username.required.message
        |login.name.required.message
        |FORM.username.required.message
        |FORM.name.required.message
        |username.required.message
        |name.required.message
        |FORM.login.required.message
        |login.required.message
        |FORM.required.message
        |required.message
        |FORM.login.username.message
        |FORM.login.name.message
        |login.username.message
        |login.name.message
        |FORM.username.message
        |FORM.name.message
        |username.message
        |name.message
        |FORM.login.message
        |login.message
        |FORM.message
        |message
        |TEXT.required.message
        |INPUT.username.required.message
        |INPUT.name.required.message
        |INPUT.required.message
        |TEXT.message
        |INPUT.username.message
        |INPUT.name.message
        |INPUT.message""".stripMargin
    )
    unresolvedKeys.toList should be(List("FORM.login.username.required.message This field is required"))
  }

  test("Translation with parameter") {
    val root = new TranslatorResolver(ULocale.ENGLISH, Configuration) {
      override def resolve(key: String) = None
    }.root

    root.translate("key", "{0} -> {1}", "Me", 12.05d) should be("Me -> 12.05")
  }

  test("Translators are cached") {
    val t1 = Translator.usage("FORM").usage("login").kind("INPUT").usage("username", "", "name", "name").kind("TEXT")
    val t2 = Translator.usage("FORM").usage("login").kind("INPUT").usage("username", "", "name", "name").kind("TEXT")

    t1 should be theSameInstanceAs t2
  }

  test("Reuse same instance") {
    val t1 = Translator.usage("u").kind("a")

    t1 should be theSameInstanceAs t1.usage("", "")
    t1 should be theSameInstanceAs t1.kind("")
  }

  test("Translation with parameter and implicit conversion") {
    implicit val root = new TranslatorResolver(ULocale.ENGLISH, Configuration) {
      override protected def resolve(key: String) = None
    }.root

    val k = "Me"
    val v = 12.06d

    t"key: $k -> $v" should be("Me -> 12.06")

    val defaultTitle = "Default title"

    t"title: #$defaultTitle: $v a" should be("Default title: 12.06 a")

    t"button-icon:" should be("")

    val regex = ".*"

    t"regex-message: Please enter a string that matches ''$regex''." should be("Please enter a string that matches '.*'.")

    val string = ""
    t"format-message: Invalid string ''$string''." should be("Invalid string ''.")
  }

  test("Translation with fallback") {
    implicit val root: Translator = ApplicationSettings.translators(ULocale.GERMAN)

    t"label: L" should be("Herr")
    t"sub.label: S" should be("Du")
    t"sub2.label: N" should be("Herr")
  }

  test("Translation with named replacement") {
    ApplicationSettings.translators(ULocale.GERMAN).translate("aged", "{name} is {age} years old", Map("name" -> "Frank", "age" -> 73)) should be("Frank is 73 years old")
  }

  test("Translation with implicit formatting") {
    val x = 1
    val y = 2
    t"fm1: ONE: {$x, plural, =1{one}other{#}}" should be("ONE: one")
    t"fm2: TWO: {$y, plural, =1{one}other{#}}" should be("TWO: 2")
  }
}