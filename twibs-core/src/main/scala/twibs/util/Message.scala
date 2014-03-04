package twibs.util

import JavaScript._
import xml.NodeSeq

trait Message extends DisplayType {
  def text: NodeSeq

  def dismissable = true

  def showNotification = JsCmd("$").call("pnotify", Map("text" -> text, "type" -> displayTypeString, "nonblock" -> true, "nonblock_opacity" -> 0.2))

  override def toString: String = s"$displayTypeString: $text"
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
