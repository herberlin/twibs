/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import JsonUtils._
import xml.NodeSeq

object JavaScript {

  case class JsParameter(string: String) {
    override def toString = string
  }

  case class JsCmd(string: String) extends Json {
    override def toString: String = string

    def ~(right: JsCmd): JsCmd = joinJsCmds(this :: right :: Nil)

    def ~(right: List[JsCmd]): JsCmd = joinJsCmds(this :: right)

    def call(method: String, parameters: JsParameter*) = new JsCmd(withDot + method + "(" + parameters.mkString(",") + ")")

    def get(property: String) = new JsCmd(withDot + property)
    def set(property: String, parameter: JsParameter) = new JsCmd(withDot + property + "=" + parameter)

    private def withDot = if (string.isEmpty) "" else string + "."

    def toJsonString: String = string
  }

  val JsEmpty = new JsCmd("")

  val undefined: JsParameter = new JsParameter("undefined")

  def jQuery(selector: String) = new JsCmd("$(" + toParameter(selector) + ")")

  def jQuery(id: IdString) = new JsCmd("$(" + toParameter(id.toCssId) + ")")

  def jQuery(nodeSeq: NodeSeq) = new JsCmd("$(" + toParameter(nodeSeq) + ")")

  implicit def toParameter(nodeSeq: NodeSeq): JsParameter = toParameter(nodeSeq.toString())

  implicit def toParameter(boolean: Boolean): JsParameter = new JsParameter(boolean.toString)

  implicit def toParameter(value: Int): JsParameter = new JsParameter(value.toString)

  implicit def toParameter(value: Long): JsParameter = new JsParameter(value.toString)

  implicit def toParameter(string: String): JsParameter = new JsParameter("'" + escape(string) + "'")

  implicit def toParameter(javascript: JsCmd): JsParameter = new JsParameter("function(){" + javascript + "}")

  implicit def toParameter(map: Map[_, _]): JsParameter = new JsParameter(map.toJsonString)

  implicit def toParameterS(stringOption: Option[String]): JsParameter = stringOption.fold(undefined)(toParameter)

  implicit def toParameterL(longOption: Option[Long]): JsParameter = longOption.fold(undefined)(toParameter)

  implicit def toParameterN(valueOption: Option[NodeSeq]): JsParameter = valueOption.fold(undefined)(toParameter)

  implicit def joinJsCmds(seq: TraversableOnce[JsCmd]): JsCmd = new JsCmd(seq.filter(!_.toString.isEmpty).mkString(";"))

  def escape(string: String) = string.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r").replace("\'", "\\'").replace("<table", "<'+'table")
}
