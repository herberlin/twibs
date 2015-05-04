/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form

import com.google.common.net.UrlEscapers
import net.twibs.util.JavaScript._
import net.twibs.util.Translator._
import net.twibs.util.XmlUtils._
import net.twibs.util._
import net.twibs.web._

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.languageFeature.dynamics
import scala.xml.{Elem, NodeSeq, Text, Unparsed}

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

  private[this] var _validated = false

  private[this] var _parsed = false

  private[this] val disabledCache = Memo {isHidden || computeDisabled}

  private[this] val hiddenCache = Memo {isIgnored || computeHidden}

  private[this] val ignoredCache = Memo {computeIgnored}

  private[this] val validCache = Memo {isDisabled || !validated || computeValid}

  def validated = isEnabled && _validated

  def parsed = _parsed

  def validateInTree(): Unit = validate()

  def validate(): Boolean = {
    _validated = true
    isValid
  }

  def reset(): Unit = {
    disabledCache.reset()
    hiddenCache.reset()
    ignoredCache.reset()
    validCache.reset()
    _validated = false
    _parsed = false
  }

  def javascript: JsCmd = JsEmpty

  def replaceContentJs: JsCmd = JsEmpty

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
    case None => ()
  }

  def parse(parameterStrings: Seq[String]): Unit = _parsed = true

  def linkParameters: Seq[(String, String)] = Seq()

  final def html: NodeSeq =
    if (isIgnored) ignoredHtml
    else if (isHidden) hiddenHtml
    else if (isFloating) componentHtml
    else treeHtml

  def ignoredHtml: NodeSeq = NodeSeq.Empty

  def hiddenHtml: NodeSeq = ignoredHtml

  def treeHtml: NodeSeq = if (isEnabled) enabledTreeHtml else disabledTreeHtml

  def disabledTreeHtml: NodeSeq = disabledComponentHtml

  def enabledTreeHtml: NodeSeq = enabledComponentHtml

  def componentHtml: NodeSeq = if (isEnabled) enabledComponentHtml else disabledComponentHtml

  def disabledComponentHtml: NodeSeq = enabledComponentHtml

  def enabledComponentHtml: NodeSeq = hiddenHtml

  // Execution with result
  implicit def toResultSeq(unit: Unit): Seq[Result] = Ignored :: Nil

  implicit def toResultSeq(result: Result): Seq[Result] = result :: Nil

  implicit def toResultSeq(resultOption: Option[Result]): Seq[Result] = resultOption.map(_ :: Nil) getOrElse Nil

  implicit def toParameterSeq(parameters: (String, String)*): Seq[(String, String)] = parameters.toSeq

  implicit def toParameterSeq(parameter: (String, String)): Seq[(String, String)] = Seq(parameter)

  def executeInTree(): Seq[Result] = if (isEnabled && parsed) execute() else Ignored

  def execute(): Seq[Result] = Ignored

  implicit class RichMessage(message: Message) {
    def showNotificationAfterReload(session: Session = Session.current) = session.addNotificationToSession(message.showNotification.toString + ";")

    def messageCssClass: String = if (validated) "has-" + message.messageTypeString else ""
  }

  // Accessors
  final def isEnabled: Boolean = !isDisabled

  final def isDisabled: Boolean = disabledCache()

  final def isHidden: Boolean = hiddenCache()

  final def isIgnored: Boolean = ignoredCache()

  final def isValid: Boolean = validCache()

  def isFloating: Boolean = false

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

trait Container extends Component {
  private[form] val _children = ListBuffer[Component]()

  def children: Seq[Component] = _children

  def validationMessageOption: Option[Message] = None

  def descendants: Stream[Component] = GraphUtils.breadthFirstSearch[Component](this) {
    case container: Container => container.children
    case component => Seq(component)
  }.map(_.head)

  override def reset(): Unit = {
    super.reset()
    children.foreach(_.reset())
  }

  override def validate(): Boolean = {
    children.foreach(_.validateInTree())
    super.validate()
  }

  override def parse(parameters: Parameters): Unit = {
    super.parse(parameters)
    children.foreach(_.parse(parameters))
  }

  override def executeInTree(): Seq[Result] = children.flatMap(_.executeInTree())

  override protected def computeValid = children.forall(_.isValid)

  def prefixForChildNames: String = parent.prefixForChildNames

  def >>(nodeSeq: => NodeSeq) = new DisplayHtml(nodeSeq)

  def >>(visible: => Boolean, nodeSeq: => NodeSeq) = new DisplayHtml(visible, nodeSeq)

  override def hiddenHtml = childrenHtml

  override def enabledComponentHtml = validationMessageHtml ++ childrenHtml

  def validationMessageHtml = validationMessageOption.fold(NodeSeq.Empty)(_.text)

  def childrenHtml: NodeSeq = children.flatMap(child => if (child.isFloating) NodeSeq.Empty else renderChild(child))

  def containerCssClasses: Seq[String] = Nil

  def renderChild(child: Component) = child.html

  def isDetachable = isInstanceOf[Detachable]

  class Child(val ilk: String) extends Component {
    final val parent = Container.this

    final val form = parent.form

    final val name = computeName

    final val id = parent.id ~ name

    def translator = parent.translator.usage(ilk)

    private[form] def computeName = {
      val names = form.descendants.map(_.name).toSet
      @tailrec
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

  /* Simple children */

  class DisplayHtml(visible: => Boolean, renderHtml: => NodeSeq) extends Child("display") {
    def this(html: => NodeSeq) = this(true, html)

    override protected def computeIgnored: Boolean = !visible

    override def enabledComponentHtml = renderHtml
  }

  class DisplayText(visible: => Boolean, text: => String) extends DisplayHtml(visible, Unparsed(text)) {
    def this(text: => String) = this(true, text)
  }

  trait Control extends Child with Input {
    override def parse(parameterStrings: Seq[String]): Unit = {
      super.parse(parameterStrings)
      strings = parameterStrings
    }

    override protected def computeValid = valid

    override def hiddenHtml = entries.flatMap(renderHidden)

    def renderHidden(entry: Entry): NodeSeq = hidden(name, entry.string)

    def invalidControlIdOption: Option[IdString] = firstInvalidEntryOption.map(e => entryId(e))

    def entryId(entry: Entry) = id ~ (if (entry.index > 0) entry.index.toString else "")

    def optionId(option: Entry) = id ~~ (if (option.index > 0) option.index.toString else "")

    def optionId(entry: Entry, option: Entry) = entryId(entry) ~~ (if (option.index > 0) option.index.toString else "")

    def controlCssClasses: Seq[String] = Nil

    override def enabledComponentHtml: NodeSeq = controlHtml ++ helpMessageHtml

    def controlHtml: Elem = <span></span>

    def controlTitle = t"control-title: #$ilk"

    def labelMessageCssClass = if (validated) max(validationMessageOption +: entries.map(_.validationMessageOption)) else ""

    def helpMessageHtml: NodeSeq = helpMessageOption.fold(NodeSeq.Empty)(Unparsed(_))

    def helpMessageOption = Option(t"help-message:").filter(!_.isEmpty)

    private def max(messages: Seq[Option[Message]]) = messages.flatten match {
      case x if x.isEmpty => ""
      case x => x.maxBy(_.importance).messageCssClass
    }
  }

  abstract class Hidden(ilk: String) extends Child(ilk: String) with Control with ParametersInLinks {
    override protected def selfIsHidden: Boolean = true
  }

  trait ParametersInLinks extends Control {
    override def linkParameters: Seq[(String, String)] = if (!isIgnored && isChanged) strings.map(v => name -> v) else Nil
  }

  trait Triggered extends Control {
//    override def hiddenHtml: NodeSeq = super.hiddenHtml ++ triggerHtml

//    override def additionalControlHtml: NodeSeq = triggerHtml

    override def parse(parameterStrings: Seq[String]): Unit = super.parse(parameterStrings.filter(_ != triggerValue))

    def triggerHtml = hidden(name, triggerValue)

    def triggerValue = "_TRIGGER_"
  }

  trait OneControlForAllEntries extends Control with Options {
    override def controlHtml = <div class="entries">{optionEntries.flatMap(optionHtmlFor)}</div>

    override def invalidControlIdOption: Option[IdString] = if (isValid) None else Some(id)

    def optionHtmlFor(option: Entry): NodeSeq

    override def minimumNumberOfEntries: Int = if (required) 1 else 0

    override def maximumNumberOfEntries: Int = optionEntries.size
  }

  trait OneControlPerEntry extends Control {
    override def controlHtml = <div class="entries">{entries.flatMap(controlHtmlFor)}</div>

    def controlHtmlFor(entry: Entry): NodeSeq
  }

  def hidden(name: String, value: String): NodeSeq = <input type="hidden" autocomplete="off" name={name} value={value} />

  /* Fields */

  trait Field extends Control with Focusable with ParametersInLinks {
    override def translator: Translator = super.translator.kind("FIELD")

    def placeholder = t"placeholder: #$ilk"

    def needsFocus = !isValid

    def submitOnChange = false

    def focusJs: JsCmd = jQuery(invalidControlIdOption getOrElse id).call("focus")
  }

  trait SubmitOnChange extends Field {
    override def submitOnChange = true

    def isSubmittedOnChange = form.request.parameters.getString("form-change", "") == name
  }

  trait OneControlPerEntryWithOptions extends OneControlPerEntry with Options {
    def controlHtmlFor(entry: Entry) = optionEntries.flatMap(option => optionHtmlFor(entry, option))

    def optionHtmlFor(entry: Entry, option: Entry): NodeSeq
  }

  trait SingleLineFieldTrait extends Field with OneControlPerEntry {
    override def translator: Translator = super.translator.kind("SINGLE-LINE")

    override def controlHtmlFor(entry: Entry): NodeSeq =
        <input type="text" name={name} id={entryId(entry)} placeholder={placeholder} value={entry.string} class={controlCssClasses}/>
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
        .addClass(submitOnChange && isEnabled, "submit-on-change")
        .set(maximumLength < Int.MaxValue, "maxlength", maximumLength.toString)
  }

  trait MultiLineFieldTrait extends Field with OneControlPerEntry {
    override def translator: Translator = super.translator.kind("MULTI-LINE")

    override def controlHtmlFor(entry: Entry): NodeSeq =
      <textarea rows={rows.toString} name={name} id={entryId(entry)} placeholder={placeholder} class={controlCssClasses}>{entry.string}</textarea>
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
        .addClass(submitOnChange && isEnabled, "submit-on-change")
        .set(maximumLength < Int.MaxValue, "maxlength", maximumLength.toString)

    def rows = 6

    override def renderHidden(entry: Entry): NodeSeq = <textarea class="concealed" name={name}>{entry.string}</textarea>
  }

  trait HtmlFieldTrait extends MultiLineFieldTrait {
    // Remove CKEDITOR instance from previous textarea otherwise a javascript error appears
    override def replaceContentJs: JsCmd = jQuery(id).call("ckeditorGet").call("destroy")

    override def javascript: JsCmd =
      jQuery(id).call("ckeditor", ckeditorInit, ckeditorConfig)

    def ckeditorInit = jQuery(id.toCssId + " +div.cke").call("addClass", "form-control")

    def ckeditorConfig: Map[String, Any] =
      Map(
        "skin" -> "bootstrapck",
        "resize_enabled" -> false,
        "removePlugins" -> "elementspath",
        "toolbar" -> Array(Array("Bold", "Italic", "-", "Smiley")))

    override def focusJs: JsCmd = jQuery(firstInvalidEntryOption.fold(id)(entryId)).call("ckeditorGet").call("focus")
  }

  trait SelectField extends Field with Options {
    override def optionEntries: Seq[Entry] =
      if (required) super.optionEntries
      else Entry("", None, "", None) +: super.optionEntries
  }

  trait Chosen extends SelectField {
    override def controlCssClasses = (if (required) "chosen" else "chosen-optional") +: super.controlCssClasses
  }

  trait SingleSelectFieldTrait extends SelectField with OneControlPerEntryWithOptions {
    override def translator: Translator = super.translator.kind("SELECT").kind("SINGLE-SELECT")

    override def controlHtmlFor(entry: Entry) =
      <select name={name} id={entryId(entry)} data-placeholder={placeholder} class={controlCssClasses}>{super.controlHtmlFor(entry)}</select>
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
        .addClass(submitOnChange && isEnabled, "submit-on-change")

    override def optionHtmlFor(entry: Entry, option: Entry): NodeSeq =
      <option value={ option.string }>{ option.title }</option>.set(option.string == string, "selected")
  }

  trait MultiSelectFieldTrait extends SelectField with OneControlForAllEntries {
    override def translator: Translator = super.translator.kind("SELECT").kind("MULTI-SELECT")

    override def controlHtml =
      <select name={name} id={id} data-placeholder={placeholder} class={controlCssClasses}>{super.controlHtml}</select>
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
        .addClass(submitOnChange && isEnabled, "submit-on-change")

    override def optionHtmlFor(option: Entry): NodeSeq =
      <option value={ option.string }>{ option.title }</option>.set(option.string == stringOrEmpty, "selected")
  }

  trait CheckboxFieldTrait extends Field with OneControlForAllEntries with Triggered {
    override def translator: Translator = super.translator.kind("CHECKBOX")

    //    override def required: Boolean = false

    def optionHtmlFor(option: Entry): NodeSeq =
        <input type="checkbox" name={name} id={optionId(option)} value={option.string} class={controlCssClasses} />
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
        .addClass(submitOnChange && isEnabled, "submit-on-change")
        .set(values.contains(option.valueOption.get), "checked")
  }

  trait BooleanCheckboxField extends CheckboxFieldTrait with BooleanInput {
    override def translator: Translator = super.translator.kind("BOOLEAN-CHECKBOX")

    override def options: Seq[ValueType] = true :: Nil

    override protected def titleFor(string: String): String = translator.translate("field-title", super.titleFor(string))

    def isChecked = valueOption.isDefined
  }

  trait RadioFieldTrait extends Field with OneControlPerEntryWithOptions {
    override def translator: Translator = super.translator.kind("RADIO")

    override def minimumNumberOfEntries: Int = 1

    //    override def maximumNumberOfEntries: Int = optionEntries.size

    def entryName(entry: Entry) = name + "_" + entry.index

    override def controlHtmlFor(entry: Entry): NodeSeq = super.controlHtmlFor(entry) ++ triggerHtml

    override def optionHtmlFor(entry: Entry, option: Entry): NodeSeq =
        <input type="radio" data-fieldName={name} name={entryName(entry)} id={optionId(entry, option)} value={option.string} class={controlCssClasses} />
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
        .addClass(submitOnChange && isEnabled, "submit-on-change")
        .set(entry.valueOption == option.valueOption, "checked")

    override def parse(parameterStrings: Seq[String]): Unit = super.parse(resolveTriggers(parameterStrings))

    private def resolveTriggers(parameterStrings: Seq[String]) : Seq[String] = parameterStrings.headOption match {
      case None => Nil
      case Some(v) if v == triggerValue => "" +: resolveTriggers(parameterStrings.tail)
      case Some(v) => v +: resolveTriggers(parameterStrings.tail.tail)
    }

    def triggerHtml = hidden(name, triggerValue)

    def triggerValue = "_TRIGGER_"
  }

  /* Buttons */

  trait ButtonTrait extends OneControlForAllEntries with DisplayType {
    override def translator: Translator = super.translator.kind("BUTTON")

    def buttonTitle = t"button-title: #$ilk" match {case "" => controlTitle case s => s }

    def buttonIconName = t"button-icon:"

    def buttonUseIconOnly = false

    override def required = false

    override def optionHtmlFor(option: Entry): NodeSeq = new OptionRenderer(option).html

    override def titleFor(string: String) = translator.usage("values").usage(string).translate("title", buttonTitle)

    class OptionRenderer(option: Entry) {
      def html = if (isEnabled) enabledHtml else disabledHtml

      def enabledHtml = <button type="submit" name={name} id={optionId(option)} class={"can-be-disabled" +: controlCssClasses} value={option.string}>{renderButtonTitle}</button>

      def disabledHtml = <span class={"disabled" +: controlCssClasses}>{renderButtonTitle}</span>

      def optionTitleHtml = Unparsed(option.title)

      def renderButtonTitle = if (buttonUseIconOnly) buttonIconOrButtonTitleIfEmptyHtml else buttonTitleWithIconHtml

      def buttonUseIconOnly = ButtonTrait.this.buttonUseIconOnly

      def buttonIconOrButtonTitleIfEmptyHtml: NodeSeq = buttonIconHtml match {case NodeSeq.Empty => optionTitleHtml case s => s }

      def buttonTitleWithIconHtml: NodeSeq = buttonIconHtml match {case NodeSeq.Empty => optionTitleHtml case ns => if (buttonIconBefore) ns ++ Text(" ") ++ optionTitleHtml else optionTitleHtml ++ Text(" ") ++ ns }

      def buttonIconBefore = true

      def buttonIconHtml: NodeSeq = NodeSeq.Empty

      def controlCssClasses: Seq[String] = ButtonTrait.this.controlCssClasses

      def displayTypeString = ButtonTrait.this.displayTypeString
    }

    override def validateInTree(): Unit = ()
  }

  trait DynamicOptions extends ButtonTrait {
    private[this] var options_ : Seq[ValueType] = Nil

    final def withOption[R](optionArg: ValueType)(f: this.type => R): R = withOptions(optionArg :: Nil)(f)

    final def withOptions[R](optionsArg: Seq[ValueType])(f: this.type => R): R = {
      val was = options_
      options = optionsArg
      try {
        f(this)
      } finally {
        options_ = was
      }
    }

    def options_=(optionsArg: Seq[ValueType]) = options_ = optionsArg

    override def options: Seq[ValueType] = options_
  }

  trait SimpleButton extends ButtonTrait with StringInput {
    override def options: Seq[ValueType] = "" :: Nil
  }

  trait EnabledForm extends ButtonTrait {
    override def controlCssClasses = "enabled-form" +: super.controlCssClasses
  }

  trait NoRefocus extends ButtonTrait {
    override def controlCssClasses = "no-refocus" +: super.controlCssClasses
  }

  trait DefaultButton extends ButtonTrait {
    def defaultButtonHtml: NodeSeq = <input type="submit" class="concealed" tabindex="-1" name={name} value={defaultButtonValue} />

    private def defaultButtonValue = optionEntries.headOption.fold(string)(_.string)
  }

  trait LinkButton extends ButtonTrait {
    // TODO: Reactivate
    //    override def render(string: String, index: Int): NodeSeq = {
    //      if (isHidden) NodeSeq.Empty
    //      else if (isDisabled) <span class={"disabled" +: buttonCssClasses}>{renderButtonTitle}</span>
    //      else <a href="#" class={"can-be-disabled" +: buttonCssClasses} data-call={link(name, string)}>{renderButtonTitle}</a>
    //    }

    def link(parameters: Seq[(String, String)]) = form.actionLinkWithContextPathAppIdAndParameters(parameters)
  }

  trait OpenModalLinkButton extends SimpleButton with LinkButton with Floating {
    override def execute(): Seq[Result] = InsteadOfFormDisplay(form.openModalJs)

    override def link(parameters: Seq[(String, String)]) = form.actionLinkWithContextPathAndParameters(parameters)
  }

  /* Containers */

  trait ChildContainerTrait extends Child with Container

  trait ButtonRowTrait extends Child with Container

  /* Child constructors */

  abstract class SingleLineField(ilk: String) extends Child(ilk) with SingleLineFieldTrait

  abstract class MultiLineField(ilk: String) extends Child(ilk) with MultiLineFieldTrait

  abstract class HtmlField(ilk: String) extends Child(ilk) with HtmlFieldTrait

  abstract class CheckboxField(ilk: String) extends Child(ilk) with CheckboxFieldTrait

  abstract class RadioField(ilk: String) extends Child(ilk) with RadioFieldTrait

  abstract class SingleSelectField(ilk: String) extends Child(ilk) with SingleSelectFieldTrait

  abstract class MultiSelectField(ilk: String) extends Child(ilk) with MultiSelectFieldTrait

  abstract class Button(ilk: String) extends Child(ilk) with ButtonTrait

  abstract class ChildContainer(ilk: String) extends Child(ilk) with ChildContainerTrait

  abstract class ButtonRow extends Child("br") with ButtonRowTrait

}

trait DynamicChildren extends Container {
  type T <: DynamicContainer

  override def reset(): Unit = {
    super.reset()
    _children --= dynamics
  }

  override def renderChild(child: Component) = super.renderChild(child) ++ {
    child match {case dc: DynamicContainer => hidden(name, dc.dynamicId) case _ => NodeSeq.Empty}
  }

  override protected def computeValid = super.computeValid && numberOfDynamicsValid

  override def parse(parameterStrings: Seq[String]): Unit = {
    super.parse(parameterStrings)
    parameterStrings.filterNot(_.isEmpty).foreach(recreateChild)
  }

  def dynamics: Seq[T] = children collect { case child: T => child }

  def recreateChild(dynamicId: String): T = dynamics.collectFirst { case child if child.dynamicId == dynamicId => child } getOrElse DynamicID.use(dynamicId) {createChild()}

  def createChild(): T

  def numberOfDynamicsValid = !parsed || numberOfDynamicsInRange

  def numberOfDynamicsInRange = Range(minimumNumberOfDynamics, maximumNumberOfDynamics).contains(children.size)

  def minimumNumberOfDynamics = 0

  def maximumNumberOfDynamics = Int.MaxValue

  override def validationMessageOption: Option[Message] =
    if (numberOfDynamicsValid) super.validationMessageOption
    else if (dynamics.size < minimumNumberOfDynamics) Some(danger"minimum-number-of-children-message: Please provide at least {$minimumNumberOfDynamics, plural, =1{one child}other{# children}}")
    else Some(danger"maximum-number-of-chilren-message: Please provide no more than {$maximumNumberOfDynamics, plural, =1{one child}other{# children}}")
}

protected[form] object DynamicID extends DynamicVariableWithDefault[String] {
  override def default: String = IdGenerator.next()
}

trait DynamicContainer extends Container {
  val dynamicId: String = DynamicID.current

  override val prefixForChildNames: String = parent.prefixForChildNames + dynamicId
}

trait Floating extends Component {
  override def isFloating: Boolean = true
}

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

//  override def renderMessage(message: Message): NodeSeq = <div>{message.text}</div>

  def process(parameters: Parameters): Response = try {
    reset()
    parse(parameters)
    val result = executeInTree()

    result.collectFirst { case UseResponse(response) => response } match {
      case Some(response) => response
      case None =>

        val beforeDisplayJs = result.collect { case BeforeFormDisplay(js) => js }

        val insteadOfFormDisplayJs = result.collect { case InsteadOfFormDisplay(js) => js } match {
          case Nil => refreshJs :: Nil
          case l => l
        }

        val afterDisplayJs = result.collect { case AfterFormDisplay(js) => js }

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

  def beforeReplaceContentJs = descendants.collect { case c if c.isEnabled && c != this => c.replaceContentJs }

  override def javascript: JsCmd = if (isDisabled) JsEmpty else descendants.collect { case c if c.isEnabled && c != this => c.javascript }

  def focusJs = descendants.collectFirst({ case f: Focusable if f.needsFocus => f.focusJs }) getOrElse JsEmpty

  def actionLinkWithContextPathAppIdAndParameters(parameters: Seq[(String, String)]): String = actionLink + queryString(addAppIdAndModal(addComponentParameters(parameters)))

  def actionLinkWithContextPathAndParameters(parameters: Seq[(String, String)]): String = actionLink + queryString(addComponentParameters(parameters))

  def actionLink: String = Request.contextPath + request.path

  def queryString(parameters: Seq[(String, String)]) = "?" + FormUtils.toQueryString(parameters)

  def addAppIdAndModal(parameters: Seq[(String, String)]) = {
    val keyValues = (pnId -> id.string) +: (pnModal -> modal.toString) +: addComponentParameters(parameters)
    if (ApplicationSettings.name != ApplicationSettings.DEFAULT_NAME) (ApplicationSettings.PN_NAME -> ApplicationSettings.name) +: keyValues else keyValues
  }

  def addComponentParameters(parameters: Seq[(String, String)]) = componentParameters ++ parameters

  def componentParameters: Seq[(String, String)] = descendants.toSeq.flatMap(_.linkParameters)

  // collectFirst does not work (for what ever reason)
  lazy val defaultButtonOption: Option[DefaultButton] = descendants.find(_.isInstanceOf[DefaultButton]).asInstanceOf[Option[DefaultButton]]

  validateSettings()
}