/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import scala.xml._

trait XmlUtils {

  implicit class RichElem(elem: Elem) {
    def addClass(condition: Boolean, cssClass: => String): Elem = if (condition) addClasses(cssClass :: Nil) else elem

    def addClasses(condition: Boolean, cssClasses: => Seq[String]): Elem = if (condition) addClasses(cssClasses) else elem

    def addClass(cssClass: String): Elem = addClasses(cssClass :: Nil)

    def addClasses(cssClasses: Seq[String]): Elem = {
      val was = elem.attribute("class").fold("")(_.toString())
      val value = merge(was +: cssClasses)
      if (value.isEmpty)
        elem.copy(attributes = elem.attributes.remove("class"))
      else
        set("class", value)
    }

    def removeClass(condition: Boolean, cssClass: => String): Elem = if (condition) removeClass(cssClass) else elem

    def removeClass(cssClass: String): Elem = set("class", merge(elem.attribute("class").fold("")(_.toString()).split(" ").toList.filterNot(_ == cssClass)))

    def merge(cssClasses: Seq[String]) = cssClasses.filterNot(_.isEmpty).distinct.mkString(" ")

    def setNotEmpty(name: String, value: String): Elem = set(!value.isEmpty, name, value)

    def set(name: String, value: String): Elem = elem % Attribute(name, Text(value), Null)

    def set(name: String): Elem = set(name, name)

    def set(condition: Boolean, name: => String, value: => String): Elem = if (condition) set(name, value) else elem

    def set(condition: Boolean, name: => String): Elem = set(condition, name, name)

    def setIfMissing(name: String, value: String): Elem = if (elem.attribute(name).isEmpty) elem % Attribute(name, Text(value), Null) else elem

    def setIfMissing(name: String, value: Option[String]): Elem = setIfMissing(value.isDefined, name, value.get)

    def setIfMissing(condition: Boolean, name: => String, value: => String): Elem = if (condition) setIfMissing(name, value) else elem

    def removeAttribute(name: String): Elem = elem.copy(attributes = elem.attributes.filter(_.key != name))

    def surround(ns: NodeSeq) = ns match {case NodeSeq.Empty => ns case _ => elem.copy(child = ns) }

    def unwrapIfEmpty: NodeSeq = if(elem.child.isEmpty) NodeSeq.Empty else elem
  }

  implicit def cssClassesToAttributeValue(cssClasses: Seq[String]): Seq[Node] = Text(cssClasses.map(_.trim).filterNot(_.isEmpty).distinct.mkString(" "))

  implicit def toXmlText(string: String): Text = Text(string)

  implicit def toXmlAttribute(nodeSeq: NodeSeq): Text = nodeSeq.toString()

  implicit class CssClasses(baseCssClasses: Seq[String]) {
    def addClass(condition: Boolean, cssClass: => String): Seq[String] = if (condition) addClasses(cssClass :: Nil) else baseCssClasses

    def addClasses(condition: Boolean, cssClasses: => Seq[String]): Seq[String] = if (condition) addClasses(cssClasses) else baseCssClasses

    def addClass(cssClass: String): Seq[String] = cleanup(cssClass +: baseCssClasses)

    def addClasses(cssClasses: Seq[String]): Seq[String] = cleanup(cssClasses ++ baseCssClasses)

    private def cleanup(cssClasses: Seq[String]) = cssClasses.filterNot(_.isEmpty).distinct
  }

}

object XmlUtils extends XmlUtils
