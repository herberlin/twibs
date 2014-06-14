/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import scala.collection.mutable.ListBuffer
import scala.xml.{Text, Elem, NodeSeq}
import twibs.util.JavaScript.JsCmd
import twibs.util.Message
import twibs.util.XmlUtils._
import twibs.web.{Request, Response}

trait Result {
  var result: Result.Value = Result.Ignored

  def AfterFormDisplay(js: JsCmd) = Result.AfterFormDisplay(js)

  def BeforeFormDisplay(js: JsCmd) = Result.AfterFormDisplay(js)

  def InsteadOfFormDisplay(js: JsCmd) = Result.InsteadOfFormDisplay(js)

  def UseResponse(response: Response) = Result.UseResponse(response)
}

object Result {

  trait Value

  case object Ignored extends Value

  case class AfterFormDisplay(js: JsCmd) extends Value

  case class BeforeFormDisplay(js: JsCmd) extends Value

  case class InsteadOfFormDisplay(js: JsCmd) extends Value

  case class UseResponse(response: Response) extends Value

}

trait Floating extends Component

trait Validatable {
  def isValid: Boolean
}

class DisplayMessage(condition: => Boolean, message: => Message)(implicit val parent: Container) extends Component {
  def this(message: => Message)(implicit parent: Container) = this(true, message)(parent)

  override def selfIsVisible: Boolean = condition

  def html = form.renderer.renderMessage(message)
}

class DisplayHtml(condition: => Boolean, gethtml: => NodeSeq)(implicit val parent: Container) extends Component {
  def this(gethtml: => NodeSeq)(implicit parent: Container) = this(true, gethtml)(parent)

  override def selfIsVisible: Boolean = condition

  def html = gethtml
}

class DisplayText(condition: => Boolean, gettext: => String)(implicit val parent: Container) extends Component {
  def this(gettext: => String)(implicit parent: Container) = this(true, gettext)(parent)

  override def selfIsVisible: Boolean = condition

  override def html = Text(gettext)
}

class Messages()(implicit val parent: Container) extends Component {
  private val _messages = ListBuffer[Message]()

  def messages = _messages.toList

  def append(message: Message): Unit = _messages += message

  override def reset(): Unit = {
    super.reset()
    _messages.clear()
  }

  override def html = <div>{messages.map(form.renderer.renderMessage)}</div>
}

abstract class HiddenField(override val ilk: String)(implicit val parent: Container) extends BaseField {
  override def html = inputs.map(input => form.renderer.hiddenInput(name, input.string)).flatten
}

trait Renderer {
  def renderMessage(message: Message): NodeSeq

  def hiddenInput(name: String, value: String): NodeSeq
}
