package twibs.util

import JsonUtils._
import twibs.TwibsTest
import twibs.util.JavaScript.JsCmd

class JsonUtilsTest extends TwibsTest {

  class Simple extends Json {
    def toJsonString = """["simple"]"""

    override def toString = "simple"
  }

  test("Map to json") {
    Map("a" -> "b", "c" -> 1, "d" -> true, "f" -> JsCmd("function(){}")).toJsonString should equal( """{"a":"b","c":1,"d":true,"f":function(){}}""")
  }
  test("Test WithToJson trait") {
    Map("a" -> "b", new Simple -> new Simple).toJsonString should equal( """{"a":"b","simple":["simple"]}""")
  }
  test("Sequence to json") {
    List("a", "b", "c", 1.001).toJsonString should equal( """["a","b","c",1.001]""")
  }
  test("Complex to json") {
    List("a", Map("b" -> List("c"))).toJsonString should equal( """["a",{"b":["c"]}]""")
  }
  test("Complex to json with tuple list") {
    List("a", null, List("b" -> List("c"))).toJsonString should equal( """["a",null,{"b":["c"]}]""")
  }
  test("Html") {
      <i class="icon-trash icon-white"/><u>{ "\r\n\r \n" }u</u>.toJsonString should equal(
      """"<i class=\"icon-trash icon-white\"/><u>\n\n \nu</u>"""")
  }
  test("Create JSON") {
    val json = Map(
      "sEcho" -> 1,
      "iTotalRecords" -> "57",
      "iTotalDisplayRecords" -> "57",
      "aaData" -> (Map(
        "0" -> """<img src="../examples_support/details_open.png">""",
        "1" -> "Other browsers",
        "2" -> "All others",
        "3" -> "-",
        "4" -> "-",
        "5" -> "U",
        "extra" -> "hrmll") :: Nil)
    )

    json.toJsonString should equal( """{"sEcho":1,"iTotalRecords":"57","iTotalDisplayRecords":"57","aaData":[{"4":"-","5":"U","1":"Other browsers","0":"<img src=\"../examples_support/details_open.png\">","2":"All others","extra":"hrmll","3":"-"}]}""")
  }
}
