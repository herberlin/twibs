/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form

import scala.collection.mutable.ListBuffer
import scala.xml.{NodeSeq, Unparsed}

import net.twibs.util.JavaScript._
import net.twibs.util.Translator._
import net.twibs.util._
import net.twibs.web._

import com.google.common.net.UrlEscapers

trait Result

case object Ignored extends Result

case class AfterFormDisplay(js: JsCmd) extends Result

case class BeforeFormDisplay(js: JsCmd) extends Result

case class InsteadOfFormDisplay(js: JsCmd) extends Result

case class UseResponse(response: Response) extends Result

trait Component extends TranslationSupport {
  def parent: Container

  def form: Form

  def ilk: String

  def name: String

  def id: IdString

  def shellId = id ~ "shell"

  def messages: Seq[Message] = _messages

  val _messages: ListBuffer[Message] = ListBuffer()

  def addMessage(message:Message) = _messages += message

  private var _validated = false

  private var _parsed = false

  private val disabledCache = LazyCache {isHidden || computeDisabled}

  private val hiddenCache = LazyCache {isIgnored || computeHidden}

  private val ignoredCache = LazyCache {computeIgnored}

  private val validCache = LazyCache {isDisabled || !validated || computeValid}

  def validated = isEnabled && _validated

  def parsed = _parsed

  def validate(): Boolean = {
    _validated = true
    isValid
  }

  def reset(): Unit = {
    disabledCache.reset()
    hiddenCache.reset()
    ignoredCache.reset()
    validCache.reset()
    _messages.clear()
    _validated = false
    _parsed = false
  }

  def javascript: JsCmd = JsEmpty

  def replaceContentJs: JsCmd = JsEmpty

  def indexId(index: Int) = id ~ (if (index > 0) index.toString else "")

  // Overridable
  protected def computeDisabled: Boolean = parent.isDisabled || selfIsDisabled

  protected def selfIsDisabled: Boolean = false

  protected def computeHidden: Boolean = parent.isHidden || selfIsHidden

  protected def selfIsHidden: Boolean = false

  protected def computeIgnored: Boolean = parent.isIgnored || selfIsIgnored

  protected def selfIsIgnored: Boolean = false

  protected def computeValid: Boolean = true

  def parse(parameters: Parameters): Unit = parameters.getStringsOption(name) match {
    case Some(parameterStrings) => parse(parameterStrings)
    case None => Unit
  }

  def parse(parameterStrings: Seq[String]): Unit = _parsed = true

  def linkParameters: Seq[(String, String)] = Seq()

  def html: NodeSeq = NodeSeq.Empty

  // Execution with result
  implicit def toResultSeq(unit: Unit): Seq[Result] = Ignored :: Nil

  implicit def toResultSeq(result: Result): Seq[Result] = result :: Nil

  implicit def toResultSeq(resultOption: Option[Result]): Seq[Result] = resultOption.map(_ :: Nil) getOrElse Nil

  implicit def toParameterSeq(parameters: (String, String)*) = parameters.toSeq

  implicit def toParameterSeq(parameter: (String, String)) = Seq(parameter)

  def callExecute(): Seq[Result] = if (isEnabled && parsed) execute() else Ignored

  def execute(): Seq[Result] = Ignored

  implicit def wrapMessage(message: Message) = new {
    def showNotificationAfterReload(session: Session = Session.current) = session.addNotificationToSession(message.showNotification.toString + ";")
  }

  // Accessors
  final def isEnabled: Boolean = !isDisabled

  final def isDisabled: Boolean = disabledCache.value

  final def isHidden: Boolean = hiddenCache.value

  final def isIgnored: Boolean = ignoredCache.value

  final def isValid: Boolean = validCache.value

  final def isFloating = this.isInstanceOf[Floating]

  private[form] def validateSettings(): Unit = {
    require(!ilk.isEmpty, "Empty ilk is not allowed")
    require(ilk matches "\\w+[\\w0-9-]*", "Ilk must start with character and contain only characters, numbers and -")
    require(!name.endsWith(FormConstants.PN_FORM_ID_SUFFIX), s"Suffix '${FormConstants.PN_FORM_ID_SUFFIX}' is reserved")
    require(!name.endsWith(FormConstants.PN_FORM_MODAL_SUFFIX), s"Suffix '${FormConstants.PN_FORM_MODAL_SUFFIX}' is reserved")
    require(name != ApplicationSettings.PN_NAME, s"'${ApplicationSettings.PN_NAME}' is reserved")
  }
}

trait ExecuteValidated extends Component {
  override def execute(): Seq[Result] = if (callValidation()) executeValidated() else super.execute()

  def callValidation() = form.validate()

  def executeValidated(): Seq[Result] = Ignored
}

trait Container extends Component with ValidateInTree {
  private[form] val _children = ListBuffer[Component]()

  def children: Seq[Component] = _children.seq

  def components: Iterator[Component] = (children map {
    case container: Container => container.components
    case component => Iterator.single(component)
  }).foldLeft(Iterator.single(this.asInstanceOf[Component]))(_ ++ _)

  override def reset(): Unit = {
    super.reset()
    children.foreach(_.reset())
  }

  override def validate(): Boolean = {
    children.collect { case v: ValidateInTree => v}.foreach(_.validate())
    super.validate()
  }

  override def parse(parameters: Parameters): Unit = {
    super.parse(parameters)
    children.foreach(_.parse(parameters))
  }

  override def callExecute(): Seq[Result] = children.map(_.callExecute()).flatten

  override protected def computeValid = children.forall(_.isValid)

  def prefixForChildNames: String

  def >>(nodeSeq: => NodeSeq) = new DisplayHtml(nodeSeq)

  def >>(visible: => Boolean, nodeSeq: => NodeSeq) = new DisplayHtml(visible, nodeSeq)

  override def html = children.map(child => if (child.isFloating) NodeSeq.Empty else renderChild(child)).flatten

  def renderChild(child: Component) = child.html

  def isDetachable = isInstanceOf[Detachable]

  abstract class ChildComponent(val ilk: String) extends Component {
    final val parent = Container.this

    final val form = parent.form

    final val name = computeName

    final val id = parent.id ~ name

    def translator = parent.translator.usage(ilk)

    private[form] def computeName = {
      val names = form.components.map(_.name).toSet
      def recursive(n: String, i: Int): String = {
        val ret = n + (if (i == 0) "" else i)
        if (!names.contains(ret)) ret
        else recursive(n, i + 1)
      }
      recursive(parent.prefixForChildNames + ilk, 0)
    }

    validateSettings()

    parent._children += this
  }

  abstract class InputComponent(ilk: String) extends ChildComponent(ilk) with Input {
    override def parse(parameterStrings: Seq[String]): Unit = {
      super.parse(parameterStrings)
      strings = parameterStrings
    }

    override def messages: Seq[Message] = messageOption.fold(super.messages)(_ +: super.messages)

    override protected def computeValid = valid
  }

  trait ParametersInLinks extends InputComponent {
    override def linkParameters: Seq[(String, String)] = if(!isIgnored && isChanged) strings.map(v => name -> v) else Nil
  }

  abstract class Hidden(ilk: String) extends InputComponent(ilk: String) with ParametersInLinks

  trait Field extends InputComponent with Focusable with ValidateInTree with ParametersInLinks {
    override def translator: Translator = super.translator.kind("FIELD")

    def fieldTitle = t"field-title: #$ilk"

    def placeholder = t"placeholder: #$ilk"

    def needsFocus = !isValid

    def focusJs: JsCmd = jQuery(entries.find(!_.valid).map(e => indexId(e.index)) getOrElse id).call("focus")

    def submitOnChange = false
  }

  trait SubmitOnChange extends Field {
    override def submitOnChange = true

    def isSubmittedOnChange = form.request.parameters.getString("form-change", "") == name
  }

  abstract class SingleLineField(ilk: String) extends InputComponent(ilk) with Field {
    override def translator: Translator = super.translator.kind("SINGLE-LINE")
  }

  abstract class MultiLineField(ilk: String) extends InputComponent(ilk) with Field {
    override def translator: Translator = super.translator.kind("MULTI-LINE")
  }

  abstract class FieldWithOptions(ilk: String) extends InputComponent(ilk) with Field with Options

  abstract class SelectField(ilk: String) extends FieldWithOptions(ilk) {
    override def translator: Translator = super.translator.kind("SELECT")
  }

  abstract class SingleSelectField(ilk: String) extends SelectField(ilk) {
    override def translator: Translator = super.translator.kind("SINGLE-SELECT")
  }

  abstract class MultiSelectField(ilk: String) extends SelectField(ilk) {
    override def translator: Translator = super.translator.kind("MULTI-SELECT")
  }

  abstract class CheckboxField(ilk: String) extends FieldWithOptions(ilk) {
    override def translator: Translator = super.translator.kind("CHECKBOX")

    override def required: Boolean = false

    override def minimumNumberOfEntries: Int = 0

    override def maximumNumberOfEntries: Int = optionEntries.size
  }

  abstract class BooleanCheckboxField(ilk: String) extends CheckboxField(ilk) with BooleanInput {
    override def translator: Translator = super.translator.kind("BOOLEAN-CHECKBOX")

    override def options: Seq[ValueType] = true :: Nil

    def isChecked = valueOption.exists(v => v)
  }

  abstract class RadioField(ilk: String) extends FieldWithOptions(ilk) with Options {
    override def translator: Translator = super.translator.kind("RADIO")

    override def minimumNumberOfEntries: Int = 1

    override def maximumNumberOfEntries: Int = optionEntries.size
  }

  abstract class Button(ilk: String) extends InputComponent(ilk) with DisplayType {
    override def translator: Translator = super.translator.kind("BUTTON")

    def buttonTitle = t"button-title: #$ilk"

    def buttonIconName = t"button-icon:"
  }

  trait DefaultButton extends Button {
    def defaultButtonHtml: NodeSeq
  }

  class StaticContainer(ilk: String) extends ChildComponent(ilk) with Container {
    val prefixForChildNames: String = parent.prefixForChildNames
  }

  abstract class DynamicContainer(ilk: String) extends ChildComponent(ilk) with Container {
    type T <: Dynamic

    val prefixForChildNames: String = parent.prefixForChildNames

    override def reset(): Unit = {
      super.reset()
      _children --= dynamics
    }

    override protected def computeValid = super.computeValid && numberOfDynamicsValid

    override def parse(parameterStrings: Seq[String]): Unit = {
      super.parse(parameterStrings)
      parameterStrings.filterNot(_.isEmpty).foreach(recreate)
    }

    def dynamics: Seq[T] = children collect { case child: T => child}

    def recreate(dynamicId: String): T = dynamics.collectFirst { case child if child.dynamicId == dynamicId => child} getOrElse create(dynamicId)

    def create(dynamicId: String = IdGenerator.next()): T

    def numberOfDynamicsValid = !parsed || numberOfDynamicsInRange

    def numberOfDynamicsInRange = Range(minimumNumberOfDynamics, maximumNumberOfDynamics).contains(children.size)

    def minimumNumberOfDynamics = 0

    def maximumNumberOfDynamics = Int.MaxValue

    override def messages: Seq[Message] =
      if (numberOfDynamicsValid) super.messages
      else if (dynamics.size < minimumNumberOfDynamics) warn"minimum-number-of-children-message: Please provide at least {$minimumNumberOfDynamics, plural, =1{one child}other{# children}}" +: super.messages
      else warn"maximum-number-of-chilren-message: Please provide no more than {$maximumNumberOfDynamics, plural, =1{one child}other{# children}}" +: super.messages

    class Dynamic(ilk: String, val dynamicId: String) extends ChildComponent(ilk) with Container {
      override val prefixForChildNames: String = dynamicId
    }

  }

  class DisplayHtml(visible: => Boolean, renderHtml: => NodeSeq) extends ChildComponent("display") {
    def this(html: => NodeSeq) = this(true, html)

    override protected def computeIgnored: Boolean = !visible

    override def html = if (isHidden) NodeSeq.Empty else renderHtml
  }

  class DisplayText(visible: => Boolean, text: => String) extends DisplayHtml(visible, Unparsed(text)) {
    def this(text: => String) = this(true, text)
  }

}

trait ValidateInTree extends Component

trait Floating extends Component

trait Focusable extends Component {
  def needsFocus: Boolean

  def focusJs: JsCmd
}

trait CancelStateInheritance extends Component {
  override protected def computeDisabled: Boolean = selfIsDisabled

  override protected def computeHidden: Boolean = selfIsHidden

  override protected def computeIgnored: Boolean = selfIsIgnored
}

trait UseLastParameterOnly extends Component {
  override def parse(parameters: Seq[String]) = super.parse(parameters.lastOption.map(_ :: Nil) getOrElse Nil)
}

trait Detachable extends Container

object FormConstants {
  val PN_FORM_ID_SUFFIX = "-form-id"

  val PN_FORM_MODAL_SUFFIX = "-form-modal"
}

object FormUtils {
  val escaper = UrlEscapers.urlFormParameterEscaper()

  def toQueryString(parameters: Seq[(String, String)]) = parameters.map(e => escaper.escape(e._1) + "=" + escaper.escape(e._2)).mkString("&")
}

class Form(val ilk: String) extends Container with CancelStateInheritance {
  override final val prefixForChildNames: String = ""

  private[form] final val pnId = ilk + FormConstants.PN_FORM_ID_SUFFIX

  private[form] final val pnModal = ilk + FormConstants.PN_FORM_MODAL_SUFFIX

  override final val form: Form = this

  override final val name: String = ilk

  override final val parent: Container = this

  final val request: Request = Request

  override final val id: IdString = request.parameters.getString(pnId, IdGenerator.next())

  final val modal = request.parameters.getBoolean(pnModal, default = false)

  final val formId = id ~ "form"

  final val modalId = id ~ "modal"

  override def translator = Request.current.translator.usage("FORM").usage(ilk)

  def inlineHtml: NodeSeq = html

  def modalHtml: NodeSeq = html

  def process(parameters: Parameters): Response = try {
    reset()
    parse(parameters)
    val result = callExecute()

    result.collectFirst { case UseResponse(response) => response} match {
      case Some(response) => response
      case None =>

        val beforeDisplayJs = result.collect { case BeforeFormDisplay(js) => js}

        val insteadOfFormDisplayJs = result.collect { case InsteadOfFormDisplay(js) => js} match {
          case Nil => refreshJs :: Nil
          case l => l
        }

        val afterDisplayJs = result.collect { case AfterFormDisplay(js) => js}

        val javascript: JsCmd = beforeDisplayJs ++ insteadOfFormDisplayJs ++ afterDisplayJs

        new StringResponse with VolatileResponse with TextMimeType {
          val asString = javascript.toString
        }
    }
  } catch {
    case e: Exception =>
      logger.error("Parsing form request failed", e)
      new StringResponse with VolatileResponse with TextMimeType {
        val asString = (jQuery(formId).call("reenableForm") ~ danger"interal-server-error: Internal Server Error".showNotification).toString
      }
  }

  def refreshJs = replaceContentJs ~ javascript ~ focusJs

  def openModalJs = jQuery("body").call("append", form.modalHtml) ~ jQuery(form.modalId).call("twibsModal") ~ javascript

  def hideModalJs = jQuery(modalId).call("modal", "hide")

  override def replaceContentJs = beforeReplaceContentJs ~ jQuery(formId).call("html", html)

  def beforeReplaceContentJs = components.collect { case c if c.isEnabled && c != this => c.replaceContentJs}

  override def javascript: JsCmd = if (isDisabled) JsEmpty else components.collect { case c if c.isEnabled && c != this => c.javascript}

  def focusJs = components.collectFirst({ case f: Focusable if f.needsFocus => f.focusJs}) getOrElse JsEmpty

  def actionLink = "/forms" + ClassUtils.toPath(getClassForActionLink(getClass))

  private def getClassForActionLink(classToCheck: Class[_]): Class[_] =
    if (classToCheck.isLocalClass) getClassForActionLink(classToCheck.getSuperclass) else classToCheck

  def actionLinkWithContextPathAppIdAndParameters(parameters: Seq[(String, String)]): String = actionLinkWithContextPath + queryString(addAppIdAndModal(addComponentParameters(parameters)))

  def actionLinkWithContextPathAndParameters(parameters: Seq[(String, String)]): String = actionLinkWithContextPath + queryString(addComponentParameters(parameters))

  def actionLinkWithContextPath: String = Request.contextPath + actionLink

  def queryString(parameters: Seq[(String, String)]) = "?" + FormUtils.toQueryString(parameters)

  def addAppIdAndModal(parameters: Seq[(String, String)]) = {
    val keyValues = (pnId -> id.string) +: (pnModal -> modal.toString) +: addComponentParameters(parameters)
    if (ApplicationSettings.name != ApplicationSettings.DEFAULT_NAME) (ApplicationSettings.PN_NAME -> ApplicationSettings.name) +: keyValues else keyValues
  }

  def addComponentParameters(parameters: Seq[(String, String)]) =  componentParameters ++ parameters

  def componentParameters: Seq[(String, String)] = components.toSeq.map(_.linkParameters).flatten

  lazy val defaultButtonOption: Option[DefaultButton] = components.collectFirst { case b: DefaultButton if b.isEnabled => b}

  validateSettings()
}

class FormException(message: String) extends RuntimeException(message)
