/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import scala.xml.NodeSeq

import twibs.util.JavaScript._

trait Message extends DisplayType {
  def text: NodeSeq

  def dismissable = true

  def showNotification: JsCmd = showNotification(Map())

  def showNotification(options: Map[String, Any] = Map()): JsCmd = JsCmd("").call("new PNotify", Map("text" -> text, "type" -> displayTypeString.replace("danger", "error"), "nonblock" -> Map("nonblock" -> true)) ++ options)

  override def toString: String = s"$displayTypeString: $text"

  override def hashCode(): Int = text.hashCode + displayTypeString.hashCode

  override def equals(obj: Any): Boolean = obj match {case o: Message if o.text == text && o.displayTypeString == displayTypeString => true case _ => false }
}

abstract class MessageImpl(val text: NodeSeq) extends Message

class MessageWrapper(delegatee: Message) extends Message {
  def text: NodeSeq = delegatee.text

  def displayTypeString: String = delegatee.displayTypeString
}

object Message {
  def info(text: NodeSeq): Message = new MessageImpl(text) with InfoDisplayType

  def warning(text: NodeSeq): Message = new MessageImpl(text) with WarningDisplayType

  def danger(text: NodeSeq): Message = new MessageImpl(text) with DangerDisplayType

  def success(text: NodeSeq): Message = new MessageImpl(text) with SuccessDisplayType
}
