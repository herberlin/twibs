/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import xml._

trait XmlUtils {
  implicit def enhanceElemWithMethods(elem: Elem) = new {
    def addClass(condition: Boolean, cssClass: => String): Elem = if (condition) addClasses(cssClass :: Nil) else elem

    def addClasses(condition: Boolean, cssClasses: => List[String]): Elem = if (condition) addClasses(cssClasses) else elem

    def addClass(cssClass: String): Elem = addClasses(cssClass :: Nil)

    def addClasses(cssClasses: List[String]): Elem = {
      val was = elem.attribute("class").fold("")(_.toString())
      val value = merge(was :: cssClasses)
      if (value.isEmpty)
        elem.copy(attributes = elem.attributes.remove("class"))
      else
        set("class", value)
    }

    def removeClass(cssClass: String) = set("class", merge(elem.attribute("class").fold("")(_.toString()).split(" ").toList.filterNot(_ == cssClass)))

    def merge(cssClasses: List[String]) = cssClasses.filterNot(_.isEmpty).distinct.mkString(" ")

    def set(name: String, value: String): Elem = elem % Attribute(name, Text(value), Null)

    def set(name: String): Elem = set(name, name)

    def set(condition: Boolean, name: => String, value: => String): Elem = if (condition) set(name, value) else elem

    def set(condition: Boolean, name: => String): Elem = set(condition, name, name)

    def add(name: String, value: String): Elem = if (elem.attribute(name).isEmpty) elem % Attribute(name, Text(value), Null) else elem

    def add(condition: Boolean, name: => String, value: => String): Elem = if (condition) add(name, value) else elem
  }

  implicit def cssClassesToAttributeValue(cssClasses: List[String]): Seq[Node] = Text(cssClasses.map(_.trim).filterNot(_.isEmpty).distinct.mkString(" "))

  implicit def toXmlText(string: String): Text = Text(string)

  implicit def toCssClasses(baseCssClasses: List[String]) = new {
    def addClass(condition: Boolean, cssClass: => String): List[String] = if (condition) addClasses(cssClass :: Nil) else baseCssClasses

    def addClasses(condition: Boolean, cssClasses: => List[String]): List[String] = if (condition) addClasses(cssClasses) else baseCssClasses

    def addClass(cssClass: String): List[String] = cleanup(cssClass :: baseCssClasses)

    def addClasses(cssClasses: List[String]): List[String] = cleanup(cssClasses ::: baseCssClasses)

    private def cleanup(cssClasses: List[String]) = cssClasses.filterNot(_.isEmpty).distinct
  }
}

object XmlUtils extends XmlUtils
