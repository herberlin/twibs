package twibs.util

import com.google.common.base.Charsets
import com.google.common.io.Files
import java.io.File
import twibs.TwibsTest

class LessCssParserFactoryTest extends TwibsTest {
  val dir = new File("src/test/webapp/www1")
  val parser = LessCssParserFactory.createParser(load)

  def load(path: String) = Files.toString(new File(dir, path), Charsets.UTF_8)

  test("Simple") {
    parser.parse("/simple.less", compress = false, 1) should be("div {\n  width: 3;\n}\n")
    parser.parse("/simple.less") should be("div{width:3}")
  }

  test("Does not exists") {
    (evaluating {
      parser.parse("/does-not-exists.less")
    } should produce[LessCssParserException]).getMessage should be("java.io.FileNotFoundException: /does-not-exists.less")
  }

  test("Syntax error") {
    (evaluating {
      parser.parse("/syntax-error.less")
    } should produce[LessCssParserException]).getMessage should be("Parse Error: missing closing `}` in '/syntax-error.less' (line 1, column 2) near\na {\n  color: red;")
    (evaluating {
      parser.parse("/import-syntax-error.less")
    } should produce[LessCssParserException]).getMessage should be("Parse Error: missing closing `}` in '/syntax-error.less' (line 1, column 2) near\na {\n  color: red;")
  }

  test("Missing variable") {
    (evaluating {
      parser.parse("/missing-variable.less")
    } should produce[LessCssParserException]).getMessage should be("Name Error: variable @color-red is undefined in '/missing-variable.less' (line 2, column 9) near\na {\n  color: @color-red; // Missing variable\n}")
    (evaluating {
      parser.parse("/import-missing-variable.less")
    } should produce[LessCssParserException]).getMessage should be("Name Error: variable @color-red is undefined in '/missing-variable.less' (line 2, column 9) near\na {\n  color: @color-red; // Missing variable\n}")
  }

  test("Import simple") {
    parser.parse("/import-simple.less") should be("div{width:3}")
  }
}
