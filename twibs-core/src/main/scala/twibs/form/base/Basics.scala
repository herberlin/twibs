/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import scala.collection.mutable.ListBuffer
import scala.xml.{NodeSeq, Text}

import twibs.util.JavaScript.JsCmd
import twibs.util.Message
import twibs.web.Response

object ComponentState {

  class ComponentState(val id: Long) {
    def merge(desiredState: ComponentState): ComponentState = if (desiredState.id > id) desiredState else this

    def hideIf(condition: Boolean): ComponentState = if (condition && Hidden.id > id) Hidden else this

    def disableIf(condition: Boolean): ComponentState = if (condition && Disabled.id > id) Hidden else this

    def ignoreIf(condition: Boolean): ComponentState = if (condition && Ignored.id > id) Hidden else this

    def isEnabled = this == Enabled

    def isDisabled = this == Disabled

    def isHidden = this == Hidden

    def isIgnored = this == Ignored

    def disabled = merge(Disabled)

    def ignored = merge(Ignored)

    def hidden = merge(Hidden)
  }

  case object Enabled extends ComponentState(0)

  case object Disabled extends ComponentState(1)

  case object Hidden extends ComponentState(2)

  case object Ignored extends ComponentState(3)

}

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


class DisplayMessage(visible: => Boolean, message: => Message)(implicit val parent: Container) extends Component {
  def this(message: => Message)(implicit parent: Container) = this(true, message)(parent)

  override def state = super.state.ignoreIf(!visible)

  def html = form.renderer.renderMessage(message)
}

class DisplayHtml(visible: => Boolean, gethtml: => NodeSeq)(implicit val parent: Container) extends Component {
  def this(gethtml: => NodeSeq)(implicit parent: Container) = this(true, gethtml)(parent)

  override def state = super.state.ignoreIf(!visible)

  def html = gethtml
}

class DisplayText(visible: => Boolean, gettext: => String)(implicit val parent: Container) extends Component {
  def this(gettext: => String)(implicit parent: Container) = this(true, gettext)(parent)

  override def state = super.state.ignoreIf(!visible)

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
