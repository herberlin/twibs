package twibs.form.base

import scala.collection.mutable.ListBuffer
import scala.xml.{Text, Elem, NodeSeq}
import twibs.util.JavaScript.JsCmd
import twibs.util.Message
import twibs.util.XmlUtils._

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

}

trait Validatable {
  def isValid: Boolean
}

trait Rendered {
  def html: NodeSeq

  def enrichedHtml: NodeSeq = html
}

trait RenderedItem extends BaseItem with Rendered {
  final override def enrichedHtml: NodeSeq = itemIsVisible match {
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

  // TODO: Check that this works
  def executionLink(value: ValueType) = "" //bootstrapForm.actionLinkWithContextPath + "?" + parameterNameForExecution + "=" + valueToStringConverter(value)
}

trait Renderer {
  def renderMessage(message: Message): NodeSeq
}
