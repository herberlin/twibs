/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import twibs.TwibsTest

class JsMinimizerTest extends TwibsTest {
  val pretty = new JsMinimizer()
  val compact = new JsMinimizer() {override def prettyPrint = false}

  test("Simple") {
    pretty.minimize("/a.js", """var x = function() {   window.alert("Hello"); }""") should be("var x = function() {\n  window.alert('Hello');\n};\n")
    compact.minimize("/a.js", """var x = function() {   window.alert("Hello"); }""") should be("var x=function(){window.alert('Hello')};")
  }

  test("With error") {
    intercept[JsMinimizerException] {
      pretty.minimize("/a.js", """var x = function() {   window.alert("Hello"); """)
    }.getMessage should be("/a.js:1: ERROR - Parse error. '}' expected\nvar x = function() {   window.alert(\"Hello\"); \n                                              ^\n")
  }

  test("Preserve conditional comment") {
    compact.minimize("/a.js",
      """(function(win) {
        |
        |	// If browser isn't IE, then stop execution! This handles the script
        |	// being loaded by non IE browsers because the developer didn't use
        |	// conditional comments.
        | var is_ie = eval("/*@cc_on!@*/false");
        | if (!is_ie) return;
        | window.alert("Ok");
        |})(window)""".stripMargin) should be( """(function(a){eval('/*@cc_on!@*/false')&&window.alert('Ok')})(window);""")
  }
}
