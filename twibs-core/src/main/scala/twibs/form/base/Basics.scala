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

trait Validatable {
  def isValid: Boolean
}

trait RenderedItem extends BaseItem {
  def html: NodeSeq

  final def enrichedHtml: NodeSeq = itemIsVisible match {
    case false => NodeSeq.Empty
    case true => isRevealed match {
      case true => html
      case false =>
        html match {
          case s@NodeSeq.Empty => s
          case n: Elem => n.addClass(itemIsConcealed, "concealed")
          case n: NodeSeq => <div>{n}</div>.addClass(itemIsConcealed, "concealed")
        }
    }
  }
}

class DisplayMessage(condition: => Boolean, message: => Message)(implicit val parent: BaseParentItem) extends BaseChildItem with RenderedItem {
  def this(message: => Message)(implicit parent: BaseParentItem) = this(true, message)(parent)

  override def itemIsVisible: Boolean = condition

  def html = parent.form.renderer.renderMessage(message)
}

class DisplayHtml(condition: => Boolean, gethtml: => NodeSeq)(implicit val parent: BaseParentItem) extends BaseChildItem with RenderedItem {
  def this(gethtml: => NodeSeq)(implicit parent: BaseParentItem) = this(true, gethtml)(parent)

  override def itemIsVisible: Boolean = condition

  def html = gethtml
}

class DisplayText(condition: => Boolean, gettext: => String)(implicit val parent: BaseParentItem) extends BaseChildItem with RenderedItem {
  def this(gettext: => String)(implicit parent: BaseParentItem) = this(true, gettext)(parent)

  override def itemIsVisible: Boolean = condition

  override def html = Text(gettext)
}

class Messages()(implicit val parent: BaseParentItem) extends BaseChildItem with RenderedItem {
  private val _messages = ListBuffer[Message]()

  def messages = _messages.toList

  def append(message: Message): Unit = _messages += message

  override def reset(): Unit = {
    super.reset()
    _messages.clear()
  }

  override def html = <div>{messages.map(parent.form.renderer.renderMessage)}</div>
}

abstract class HiddenInput(val ilk: String)(implicit val parent: BaseParentItem) extends BaseField with RenderedItem {
  override def html = inputs.map(input => HiddenInputRenderer(name, input.string))

  def executionLink(value: ValueType) = parent.form.actionLinkWithContextPath + "?" + name + "=" + valueToString(value)
}

abstract class InitInput()(implicit val parent: BaseParentItem) extends BaseField {
  def ilk: String = "init"

  def link(value: ValueType): String = parent.form.actionLinkWithContextPath + "?" + name + "=" + valueToString(value)

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

abstract class ActionButton(val ilk: String)(implicit val parent: BaseParentItem) extends ButtonValues {
  def link(value: ValueType): String = parent.form.actionLinkWithContextPath + "?" + name + "=" + valueToString(value)

  override def execute(request: Request): Unit = values.headOption.map(execute)

  def execute(value: ValueType): Unit
}

trait Renderer {
  def renderMessage(message: Message): NodeSeq
}

object HiddenInputRenderer {
  def apply(name: String, value: String) = <input type="hidden" autocomplete="off" name={name} value={value} />
}
