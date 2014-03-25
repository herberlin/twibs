package twibs.util

import Translator._
import collection.mutable.ListBuffer
import com.ibm.icu.util.ULocale
import twibs.TwibsTest

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
      override def resolve(key: String) = None
    }.root

    val k = "Me"
    val v = 12.06d

    t"key: $k -> $v" should be("Me -> 12.06")

    val defaultTitle = "Default title"

    t"title: #$defaultTitle: $v a" should be("Default title: 12.06 a")

    t"button-icon:" should be("")

    val regex = ".*"

    t"regex-message: Please enter a string that matches ''$regex''." should be("Please enter a string that matches '.*'.")
  }
}
