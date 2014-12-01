/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import scala.xml.NodeSeq

import net.twibs.util.JavaScript._

case class Message(text: NodeSeq, displayTypeString: String, importance: Int, dismissable: Boolean = true) extends DisplayType {
  def showNotification: JsCmd = showNotification(Map())

  def showNotification(options: Map[String, Any] = Map()): JsCmd = JsCmd("").call("new PNotify", Map("text" -> text, "type" -> displayTypeString.replace("danger", "error"), "nonblock" -> Map("nonblock" -> true)) ++ options)

  override def toString: String = s"$displayTypeString: $text"

  override def hashCode(): Int = text.hashCode + displayTypeString.hashCode

  override def equals(obj: Any): Boolean = obj match {case o: Message if o.text == text && o.displayTypeString == displayTypeString => true case _ => false }

  def permanent = copy(dismissable = false)
}

object Message {
  def success(text: NodeSeq): Message = new Message(text, "success", 1)

  def info(text: NodeSeq): Message = new Message(text, "info",2 )

  def warning(text: NodeSeq): Message = new Message(text, "warning", 3)

  def danger(text: NodeSeq): Message = new Message(text, "danger", 4)
}
