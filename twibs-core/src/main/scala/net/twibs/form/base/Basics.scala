/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form.base

import scala.collection.mutable.ListBuffer
import scala.xml.{NodeSeq, Text}

import net.twibs.util.JavaScript.JsCmd
import net.twibs.util.Message
import net.twibs.web.Response

trait ComponentState {
  self =>

  final def isEnabled: Boolean = !isDisabled

  def isDisabled: Boolean

  def isHidden: Boolean

  def isIgnored: Boolean

  def merge(desiredState: ComponentState): ComponentState = new ComponentState() {
    override def isDisabled: Boolean = self.isDisabled || desiredState.isDisabled

    override def isHidden: Boolean = self.isHidden || desiredState.isHidden

    override def isIgnored: Boolean = self.isIgnored || desiredState.isIgnored
  }

  def ~(desiredState: ComponentState) = merge(desiredState)

  def disabled = merge(ComponentState.Disabled)

  def hidden = merge(ComponentState.Hidden)

  def ignored = merge(ComponentState.Ignored)

  def disableIf(condition: => Boolean): ComponentState = new ComponentState() {
    override def isDisabled: Boolean = self.isDisabled || condition

    override def isHidden: Boolean = self.isHidden

    override def isIgnored: Boolean = self.isIgnored
  }

  def hideIf(condition: => Boolean): ComponentState = new ComponentState() {
    override def isDisabled: Boolean = self.isDisabled || condition

    override def isHidden: Boolean = self.isHidden || condition

    override def isIgnored: Boolean = self.isIgnored
  }

  def ignoreIf(condition: => Boolean): ComponentState = new ComponentState() {
    override def isDisabled: Boolean = self.isDisabled || condition

    override def isHidden: Boolean = self.isHidden || condition

    override def isIgnored: Boolean = self.isIgnored || condition
  }
}

object ComponentState {

  object Enabled extends ComponentState {
    override def isDisabled: Boolean = false

    override def isHidden: Boolean = false

    override def isIgnored: Boolean = false
  }

  object Disabled extends ComponentState {
    override def isDisabled: Boolean = true

    override def isHidden: Boolean = false

    override def isIgnored: Boolean = false
  }

  object Hidden extends ComponentState {
    override def isDisabled: Boolean = true

    override def isHidden: Boolean = true

    override def isIgnored: Boolean = false
  }

  object Ignored extends ComponentState {
    override def isDisabled: Boolean = true

    override def isHidden: Boolean = true

    override def isIgnored: Boolean = true
  }

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

abstract class Display protected(visible: => Boolean, displayHtml: => NodeSeq, val parent: Container) extends Component {
  override def state = super.state.ignoreIf(!visible)

  override def asHtml = if (state.isHidden) NodeSeq.Empty else displayHtml
}

class DisplayHtml(visible: => Boolean, html: => NodeSeq)(implicit parent: Container) extends Display(visible, html, parent) {
  def this(html: => NodeSeq)(implicit parent: Container) = this(true, html)(parent)
}

class DisplayText(visible: => Boolean, text: => String)(implicit parent: Container) extends Display(visible, Text(text), parent) {
  def this(text: => String)(implicit parent: Container) = this(true, text)(parent)
}

class DisplayMessage(visible: => Boolean, message: => Message)(implicit parent: Container) extends Display(visible, parent.form.renderer.renderMessage(message), parent) {
  def this(message: => Message)(implicit parent: Container) = this(true, message)(parent)
}

class Messages()(implicit val parent: Container) extends Component {
  private val _messages = ListBuffer[Message]()

  def messages = _messages.toList

  def append(message: Message): Unit = _messages += message

  override def reset(): Unit = {
    super.reset()
    _messages.clear()
  }

  override def asHtml =
    if (state.isHidden) NodeSeq.Empty
    else <div>{messages.map(form.renderer.renderMessage)}</div>
}

abstract class HiddenField(override val ilk: String)(implicit val parent: Container) extends BaseField {
  override def asHtml =
    if (state.isIgnored) NodeSeq.Empty
    else inputs.flatMap(input => form.renderer.hiddenInput(name, input.string))
}

trait Renderer {
  def renderMessage(message: Message): NodeSeq

  def hiddenInput(name: String, value: String): NodeSeq

  def renderAsDefaultExecutable(executable: Executable): NodeSeq
}
