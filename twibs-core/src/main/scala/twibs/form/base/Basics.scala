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
}

object Result {

  trait Value

  case object Ignored extends Value

  case class AfterFormDisplay(js: JsCmd) extends Value

  case class BeforeFormDisplay(js: JsCmd) extends Value

  case class InsteadOfFormDisplay(js: JsCmd) extends Value

  case class UseResponse(response: Response) extends Value

}

trait Rendered extends Component {
  def html: NodeSeq

  final def enrichedHtml: NodeSeq = selfIsVisible match {
    case false => NodeSeq.Empty
    case true => isRevealed match {
      case true => html
      case false =>
        html match {
          case s@NodeSeq.Empty => s
          case n: Elem => n.addClass(selfIsConcealed, "concealed")
          case n: NodeSeq => <div>{n}</div>.addClass(selfIsConcealed, "concealed")
        }
    }
  }
}

trait Validatable {
  def isValid: Boolean
}

class DisplayMessage(condition: => Boolean, message: => Message)(implicit val parent: Container) extends Component with Rendered {
  def this(message: => Message)(implicit parent: Container) = this(true, message)(parent)

  override def selfIsVisible: Boolean = condition

  def html = form.renderer.renderMessage(message)
}

class DisplayHtml(condition: => Boolean, gethtml: => NodeSeq)(implicit val parent: Container) extends Component with Rendered {
  def this(gethtml: => NodeSeq)(implicit parent: Container) = this(true, gethtml)(parent)

  override def selfIsVisible: Boolean = condition

  def html = gethtml
}

class DisplayText(condition: => Boolean, gettext: => String)(implicit val parent: Container) extends Component with Rendered {
  def this(gettext: => String)(implicit parent: Container) = this(true, gettext)(parent)

  override def selfIsVisible: Boolean = condition

  override def html = Text(gettext)
}

class Messages()(implicit val parent: Container) extends Component with Rendered {
  private val _messages = ListBuffer[Message]()

  def messages = _messages.toList

  def append(message: Message): Unit = _messages += message

  override def reset(): Unit = {
    super.reset()
    _messages.clear()
  }

  override def html = <div>{messages.map(form.renderer.renderMessage)}</div>
}

abstract class HiddenInput(override val ilk: String)(implicit val parent: Container) extends BaseField with Rendered {
  override def html = inputs.map(input => HiddenInputRenderer(name, input.string))

  def executionLink(value: ValueType) = form.actionLinkWithContextPath + "?" + name + "=" + valueToString(value)
}

abstract class InitInput()(implicit val parent: Container) extends BaseField {
  override def ilk: String = "init"

  def link(value: ValueType): String = form.actionLinkWithContextPath + "?" + name + "=" + valueToString(value)

  override def prepare(request: Request): Unit = {
    parse(request)
    valueOption.map(initWithVar)
  }

  def <<(initWith: (ValueType) => Unit) : (ValueType) => String = {
    initWithVar = initWith
    link
  }

  private var initWithVar:(ValueType) => Unit = (ValueType) => Unit
}

abstract class ActionButton(override val ilk: String)(implicit val parent: Container) extends ButtonValues {
  def link(value: ValueType): String = form.actionLinkWithContextPath + "?" + name + "=" + valueToString(value)

  override def execute(request: Request): Unit = values.headOption.map(execute)

  def execute(value: ValueType): Unit
}

trait Renderer {
  def renderMessage(message: Message): NodeSeq
}

object HiddenInputRenderer {
  def apply(name: String, value: String) = <input type="hidden" autocomplete="off" name={name} value={value} />
}
