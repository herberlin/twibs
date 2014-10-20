/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest
import net.twibs.util.JavaScript._

class JavaScriptTest extends TwibsTest {
  test("Generate Ids") {
    jQuery("one two").toString should equal("$('one two')")
  }

  test("JQuery") {
    jQuery("one two").call("focus").toString should equal("$('one two').focus()")
  }

  test("Html generation") {
    jQuery(<div>
      <h3>t\'es't</h3>
    </div>).call("twibsModal").toString should equal( """$('<div>\n      <h3>t\\\'es\'t</h3>\n    </div>').twibsModal()""")
  }

  test("Concatenation") {
    (jQuery("a") ~ JsEmpty ~ jQuery("b")).toString should equal( """$('a');$('b')""")
  }

  test("Concatenation with sequences") {
    (jQuery("a") ~ Seq(jQuery("b"), jQuery("c")) ~ Seq(jQuery("d")) ~ jQuery("e")).toString should equal( """$('a');$('b');$('c');$('d');$('e')""")
  }

  test("Json") {
    jQuery(IdString("i")).call("datetimepicker", Map("startDate" -> """1!"§$%""", "endDate" -> 12)).toString should equal( """$('#i').datetimepicker({"startDate":"1!\"§$%","endDate":12})""")
  }

  test("Escape") {
    JavaScript.escape("<div>\n\r'</div>") should equal( """<div>\n\r\'</div>""")
  }

}
