/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form

import com.google.common.net.UrlEscapers
import net.twibs.util.JavaScript._
import net.twibs.util.XmlUtils._
import net.twibs.util._
import net.twibs.web._

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.languageFeature.dynamics
import scala.xml.{Elem, NodeSeq, Text, Unparsed}

trait Component extends TranslationSupport {
  def parent: Container

  def ilk: String

  def form: Form = parent.form

  def name: String

  def id: IdString = parent.id ~ name

  def shellId = id ~ "shell"

  def translator = parent.translator.usage(ilk)

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
    case Some(parameterStrings) => parse(parameterStrings.filter(_ != triggerValue))
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

  def triggerValue = "_TRIGGER_"

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
    require(!(ilk startsWith "t-"), "Ilk must not start with the reserved 't-'")
  }

  implicit class Bs3RichElem(elem: Elem) {
    def addPopover(messageOption: Option[Message], placement: String = "bottom"): Elem = messageOption.fold(elem)(addPopover(_, placement))

    def addPopover(message: Message, placement: String): Elem =
      elem
        .set("data-toggle", "popover")
        .set("data-content", message.toBsMessageAttribute)
        .set("data-placement", placement)
        .set("data-trigger", "hover focus")
        .set("data-html", "true")
        .set("data-container", form.formId.toCssId)
        .addClass(message.messageCssClass)

    def addTooltip(messageOption: Option[Message], placement: String = "bottom"): Elem = messageOption.fold(elem)(addTooltip(_, placement))

    def addTooltip(message: Message, placement: String): Elem =
      elem
        .set("data-toggle", "tooltip")
        .set("data-title", message.text.toString())
        .set("data-placement", placement)
        .set("data-trigger", "hover focus")
        .set("data-html", "true")
        .set("data-container", form.formId.toCssId)
        .addClass(message.messageCssClass)
  }

  implicit class Bs3RichMessage(message: Message) {
    def toBsMessage = <div class={s"alert alert-${message.displayTypeString}"}>{message.text}</div>

    def toBsMessageAttribute: String = toBsMessage.toString()
  }

}

trait ExecuteValidated extends Component {
  override def execute(): Seq[Result] = if (callValidation()) executeValidated() else super.execute()

  def callValidation() = form.validate()

  def executeValidated(): Seq[Result] = Ignored
}

trait Control extends Component with Input {
  override def parse(parameterStrings: Seq[String]): Unit = {
    super.parse(parameterStrings)
    strings = parameterStrings
  }

  override protected def computeValid = valid

  override def hiddenHtml = entries.flatMap(renderHidden)

  def renderHidden(entry: Entry): NodeSeq = form.hidden(name, entry.string)

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

  def infoMessageHtml = infoMessageOption.fold(NodeSeq.Empty)(_.text)

  def infoMessageTitle = t"info-message.title: $controlTitle"

  def infoMessageOption = Some(t"info-message.content:").filter(!_.isEmpty)

  private def max(messages: Seq[Option[Message]]) = messages.flatten match {
    case x if x.isEmpty => ""
    case x => x.maxBy(_.importance).messageCssClass
  }

  def triggerHtml = form.hidden(name, triggerValue)
}

trait ParametersInLinks extends Control {
  override def linkParameters: Seq[(String, String)] = if (!isIgnored && isChanged) strings.map(v => name -> v) else Nil
}

trait OneControlForAllEntries extends Control with Options {
  override def controlHtml = <div class="entries" id={shellId}>{optionEntries.flatMap(optionHtmlFor)}</div>

  override def invalidControlIdOption: Option[IdString] = if (isValid) None else Some(id)

  def optionHtmlFor(option: Entry): NodeSeq

  override def minimumNumberOfEntries: Int = if (required) 1 else 0

  override def maximumNumberOfEntries: Int = optionEntries.size
}

trait OneControlPerEntry extends Control {
  control =>

  override def controlHtml =
    <div class="entries" id={shellId}>{entriesHtml}{controlActions}</div>
        .addClass(isSortable, "sortable")

  def entriesHtml: NodeSeq = entries match {
    case Seq() => triggerHtml
    case _ => entries.flatMap(entryHtmlFor)
  }

  def entryHtmlFor(entry: Entry) =
    <div class="entry">{controlHtmlFor(entry) ++ entryActions(entry) ++ disabledFallback(entry)}</div>
        .addTooltip(entry.validationMessageOption.filter(_ => validated))
      .addClass(hasActions, "has-actions")
      .addClass(hasSorter, "has-sorter")

  def disabledFallback(entry: Entry) = if (isDisabled) renderHidden(entry) else NodeSeq.Empty

  def hasActions = !entryAddButton.isIgnored || !entryRemoveButton.isIgnored

  def entryActions(entry: Entry) = <div class="actions">{entryAddActionHtml(entry)}{entryRemoveActionHtml(entry)}</div>.unwrapIfEmpty

  def entryAddActionHtml(entry: Entry) = entryAddButton.withOption(entry.index)(_.html)

  def entryRemoveActionHtml(entry: Entry) = entryRemoveButton.withOption(entry.index)(_.html)

  def controlHtmlFor(entry: Entry): NodeSeq

  def controlActions = <div class="actions">{entryAddActionHtml}</div>

  def entryAddActionHtml = entryAddButton.withOption(-1)(_.html)

  def hasSorter = isSortable

  def isSortable = sortableCache()

  def computeSortable = true

  private[this] val sortableCache = Memo {isEnabled && entries.size > 1 && computeSortable}

  val entryAddButton = new Child("add-entry-button", control.parent) with ButtonTrait with DynamicOptions with IntInput with DefaultDisplayType with Floating {
    override def controlCssClasses: Seq[String] = "btn-xs" +: super.controlCssClasses

    override def buttonUseIconOnly: Boolean = true

    override def execute(): Seq[Result] = addEntryBefore(value)

    override protected def selfIsDisabled: Boolean =
      control.entries.size >= control.maximumNumberOfEntries

    override protected def selfIsIgnored: Boolean =
      control.isDisabled || control.minimumNumberOfEntries == control.maximumNumberOfEntries && selfIsDisabled
  }

  val entryRemoveButton = new Child("remove-entry-button", control.parent) with ButtonTrait with DynamicOptions with DefaultDisplayType with Floating with IntInput {
    override def controlCssClasses: Seq[String] = "btn-xs" +: super.controlCssClasses

    override def buttonUseIconOnly: Boolean = true

    override def execute(): Seq[Result] = removeEntryAt(value)

    override protected def selfIsDisabled: Boolean =
      control.entries.size <= control.minimumNumberOfEntries

    override protected def selfIsIgnored: Boolean =
      control.isDisabled || control.minimumNumberOfEntries == control.maximumNumberOfEntries && selfIsDisabled
  }

  def addEntryBefore(pos: Int): Unit = strings = if (pos == -1) strings :+ "" else strings.patch(pos, Seq(""), 0)

  def removeEntryAt(pos: Int): Unit = strings = strings.patch(pos, Seq(), 1)

  override protected def setEntries(es: Seq[Entry]): Unit = {
    super.setEntries(es)
    entryAddButton.reset()
    entryRemoveButton.reset()
  }
}


trait Field extends Control with Focusable with ParametersInLinks {
  override def translator: Translator = super.translator.kind("FIELD")

  def placeholder = t"placeholder: #$ilk"

  def needsFocus = !isValid

  def submitOnChange = false

  override def focusJs: JsCmd = jQuery(focusId).call("focus")

  def focusId = invalidControlIdOption getOrElse id
}

trait FormControlField extends Field {
  override def controlCssClasses: Seq[String] = "form-control" +: super.controlCssClasses
}

trait SubmitOnChange extends Field {
  override def submitOnChange = true

  def isSubmittedOnChange = form.actionName == name && form.isSubmittedOnChange
}

trait OneControlPerEntryWithOptions extends OneControlPerEntry with Options {
  def controlHtmlFor(entry: Entry) = optionEntries.flatMap(option => optionHtmlFor(entry, option))

  def optionHtmlFor(entry: Entry, option: Entry): NodeSeq
}

trait SingleLineFieldTrait extends FormControlField with OneControlPerEntry {
  override def translator: Translator = super.translator.kind("SINGLE-LINE")

  override def controlHtmlFor(entry: Entry): NodeSeq =
      <input type="text" name={name} id={entryId(entry)} placeholder={placeholder} value={entry.string} class={controlCssClasses}/>
        .setIfMissing(isDisabled, "disabled", "disabled")
      .addClass(isDisabled, "disabled")
      .addClass(!isDisabled, "can-be-disabled")
      .addClass(submitOnChange && isEnabled, FormConstants.ACTION_SUBMIT_ON_CHANGE)
      .set(maximumLength < Int.MaxValue, "maxlength", maximumLength.toString)
}

trait MultiLineFieldTrait extends FormControlField with OneControlPerEntry {
  override def translator: Translator = super.translator.kind("MULTI-LINE")

  override def controlHtmlFor(entry: Entry) =
    <textarea rows={rows.toString} name={name} id={entryId(entry)} placeholder={placeholder} class={controlCssClasses}>{entry.string}</textarea>
        .setIfMissing(isDisabled, "disabled", "disabled")
      .addClass(isDisabled, "disabled")
      .addClass(!isDisabled, "can-be-disabled")
      .addClass(submitOnChange && isEnabled, FormConstants.ACTION_SUBMIT_ON_CHANGE)
      .set(maximumLength < Int.MaxValue, "maxlength", maximumLength.toString)

  def rows = 6

  override def renderHidden(entry: Entry): NodeSeq = <textarea class="concealed" name={name}>{entry.string}</textarea>
}

trait HtmlFieldTrait extends MultiLineFieldTrait with HtmlInput {
  // Remove CKEDITOR instance from previous textarea otherwise a javascript error appears
  // TODO: This has moved to javascript - delete this line if successful
  //  override def replaceContentJs: JsCmd = jQuery(shellId + " .html-field[contenteditable='true']").call("destroyCkeditor")

  override def javascript: JsCmd = jQuery(shellId + " .html-field[contenteditable='true']").call("ckeditor", ckeditorInit, ckeditorConfig)

  def ckeditorInit = JsEmpty

  def ckeditorConfig: Map[String, Any] =
    Map(
      //        "toolbar" -> Array(Array("Bold", "Italic", "-", "Smiley"))
    )

  //    override def focusJs: JsCmd = jQuery(firstInvalidEntryOption.fold(id)(entryId)).call("ckeditorGet").call("focus")

  override def translator: Translator = super.translator.kind("HTML")

  override def controlHtmlFor(entry: Entry) =
    <div data-name={name} id={entryId(entry)} placeholder={placeholder} class={controlCssClasses}>{Unparsed(entry.string)}</div>
        .setIfMissing(isDisabled, "disabled", "disabled")
      .addClass(isDisabled, "disabled")
      .set(isEnabled, "contenteditable", "true")
      .addClass(!isDisabled, "can-be-disabled")
      .addClass(submitOnChange && isEnabled, FormConstants.ACTION_SUBMIT_ON_CHANGE)

  override def renderHidden(entry: Entry): NodeSeq = <textarea class="concealed" name={name}>{entry.string}</textarea>

  override def controlCssClasses: Seq[String] = "html-field" +: super.controlCssClasses
}

trait SelectField extends FormControlField with Options {
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
    <select name={name} id={entryId(entry)} data-placeholder={placeholder} class={controlCssClasses}>{emptyOption(entry) ++ super.controlHtmlFor(entry)}</select>
        .setIfMissing(isDisabled, "disabled", "disabled")
      .addClass(isDisabled, "disabled")
      .addClass(!isDisabled, "can-be-disabled")
      .addClass(submitOnChange && isEnabled, FormConstants.ACTION_SUBMIT_ON_CHANGE)

  def emptyOption(entry: Entry) =
    if (required && optionEntries.exists(_.string == entry.string)) NodeSeq.Empty
    else <option value=""></option>.set(entry.string == "", "selected")

  override def optionHtmlFor(entry: Entry, option: Entry): NodeSeq =
    <option value={ option.string }>{ option.title }</option>.set(option.string == entry.string, "selected")
}

trait MultiSelectFieldTrait extends SelectField with OneControlForAllEntries {
  override def translator: Translator = super.translator.kind("SELECT").kind("MULTI-SELECT")

  override def controlHtml =
    <select name={name} id={id} data-placeholder={placeholder} class={controlCssClasses}>{super.controlHtml}</select>
        .setIfMissing(isDisabled, "disabled", "disabled")
      .addClass(isDisabled, "disabled")
      .addClass(!isDisabled, "can-be-disabled")
      .addClass(submitOnChange && isEnabled, FormConstants.ACTION_SUBMIT_ON_CHANGE)

  override def optionHtmlFor(option: Entry): NodeSeq =
    <option value={ option.string }>{ option.title }</option>.set(option.string == stringOrEmpty, "selected")
}

trait CheckboxFieldTrait extends Field with OneControlForAllEntries {
  override def translator: Translator = super.translator.kind("CHECKBOX")

  //    override def required: Boolean = false

  def optionHtmlFor(option: Entry): NodeSeq =
      <input type="checkbox" name={name} id={optionId(option)} value={option.string} class={controlCssClasses} />
        .setIfMissing(isDisabled, "disabled", "disabled")
      .addClass(isDisabled, "disabled")
      .addClass(!isDisabled, "can-be-disabled")
      .addClass(submitOnChange && isEnabled, FormConstants.ACTION_SUBMIT_ON_CHANGE)
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

  def entryName(entry: Entry) = name + "_" + entry.index

  override def controlHtmlFor(entry: Entry): NodeSeq = super.controlHtmlFor(entry) ++ entryTriggerHtml

  override def optionHtmlFor(entry: Entry, option: Entry): NodeSeq =
    <div class="radio">
        <label>
          {inputFieldFor(entry, option)}
          {option.title}
        </label>
      </div>.addClass(isDisabled, "disabled")

  def inputFieldFor(entry: Entry, option: Entry): NodeSeq =
      <input type="radio" data-name={name} name={entryName(entry)} id={optionId(entry, option)} value={option.string} class={controlCssClasses} />
        .setIfMissing(isDisabled, "disabled", "disabled")
      .addClass(isDisabled, "disabled")
      .addClass(!isDisabled, "can-be-disabled")
      .addClass(submitOnChange && isEnabled, FormConstants.ACTION_SUBMIT_ON_CHANGE)
      .set(entry.valueOption == option.valueOption, "checked")

  override def parse(parameterStrings: Seq[String]): Unit = super.parse(resolveEntryTriggers(parameterStrings))

  private def resolveEntryTriggers(parameterStrings: Seq[String]): Seq[String] = parameterStrings.headOption match {
    case None => Nil
    case Some(v) if v == entryTriggerValue => "" +: resolveEntryTriggers(parameterStrings.tail)
    case Some(v) => v +: resolveEntryTriggers(dropEntryTrigger(parameterStrings.tail))
  }

  private def dropEntryTrigger(parameterStrings: Seq[String]) = parameterStrings.headOption match {
    case Some(v) if v == entryTriggerValue => parameterStrings.tail
    case _ => parameterStrings
  }

  def entryTriggerHtml = if (isEnabled) form.hidden(name, entryTriggerValue) else NodeSeq.Empty

  def entryTriggerValue = "_ET_"
}

trait RadioInlineLayout extends RadioFieldTrait {
  override def optionHtmlFor(entry: Entry, option: Entry): NodeSeq =
    <label class="radio-inline">{inputFieldFor(entry, option)} {option.title}</label>.addClass(isDisabled, "disabled")
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

  override def validateInTree(): Unit = ()

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

    def translator = ButtonTrait.this.translator.usage("values").usage(option.string)

    def buttonIconHtml: NodeSeq = buttonIconName match {
      case "" => NodeSeq.Empty
      case s if s.startsWith("fa-") => <span class={s"fa $s"}></span>
      case s if s.startsWith("glyphicon-") => <span class={s"glyphicon $s"}></span>
      case s => <span class={s"glyphicon glyphicon-$s"}></span>
    }

    def buttonIconName = translator.translate("icon", ButtonTrait.this.buttonIconName)

    def controlCssClasses = "btn" +: ("btn-" + displayTypeString) +: ButtonTrait.this.controlCssClasses

    def displayTypeString = translator.translate("display-type", ButtonTrait.this.displayTypeString)
  }

}

class Child(val ilk: String, val parent: Container) extends Component {
  validateSettings()

  val name: String = computeName

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

  parent._children += this
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

  def containerCssClasses: Seq[String] = "form-container" +: Nil

  def renderChild(child: Component) = child.html

  def isDetachable = isInstanceOf[Detachable]

  override def componentHtml: NodeSeq =
    <div id={shellId} name={name} class={("form-container-shell" :: Nil).addClass(isDetachable, "detachable")}>
      {if (isDetachable) closeButton else NodeSeq.Empty}
      <div id={id} class={containerCssClasses}>
        {super.componentHtml}
      </div>
    </div>

  def closeButton = {
    def dismissButton: NodeSeq = <button type="button" class="btn btn-danger" data-dismiss="detachable">{t"delete-component.button-title: Delete"}</button>

    if (isEnabled) <button type="button" class="close" data-toggle="popover" data-html="true" data-placement="auto left" data-title={t"delete-component.popover-title: Delete component?"} data-content={dismissButton}>&times;</button>
    else NodeSeq.Empty
  }

  //  override def renderMessage(message: Message): NodeSeq =
  //    if (message.dismissable)
  //        <div class={"alert" :: ("alert-" + message.displayTypeString) :: "alert-dismissable" :: Nil}><button type="button" class="close" data-dismiss="alert">×</button>{message.text}</div>
  //    else
  //        <div class={"alert" :: ("alert-" + message.displayTypeString) :: Nil}>{message.text}</div>

  /* Containers */

  //  trait Popover extends ChildContainer with ButtonRenderer {
  //    def popoverContainer = form.shellId.toCssId
  //
  //    def popoverPlacement = "bottom"
  //
  //    def popoverTitle = t"popover-title:"
  //
  //    def popoverContentText = t"popover-text:"
  //
  //    override def visibleHtml: NodeSeq =
  //      if (isDisabled) <span class={"disabled" :: buttonCssClasses}>{renderButtonTitle}</span>
  //      else <button type="button" class={"can-be-disabled" :: buttonCssClasses}  data-container={popoverContainer} data-toggle="popover" data-html="true" data-placement={popoverPlacement} data-title={popoverTitle} data-content={super.html}>{renderButtonTitle}</button>
  //  }

  /* Simple children */

  class DisplayHtml(visible: => Boolean, renderHtml: => NodeSeq) extends Child("display", this) {
    def this(html: => NodeSeq) = this(true, html)

    override protected def computeIgnored: Boolean = !visible

    override def enabledComponentHtml = renderHtml
  }

  class DisplayText(visible: => Boolean, text: => String) extends DisplayHtml(visible, Unparsed(text)) {
    def this(text: => String) = this(true, text)
  }

  abstract class Hidden(ilk: String) extends Child(ilk: String, this) with Control with ParametersInLinks {
    override protected def selfIsHidden: Boolean = true
  }

  /* Child constructors */

  abstract class SingleLineField(ilk: String) extends Child(ilk, this) with SingleLineFieldTrait

  abstract class MultiLineField(ilk: String) extends Child(ilk, this) with MultiLineFieldTrait

  abstract class HtmlField(ilk: String) extends Child(ilk, this) with HtmlFieldTrait

  abstract class CheckboxField(ilk: String) extends Child(ilk, this) with CheckboxFieldTrait

  abstract class RadioField(ilk: String) extends Child(ilk, this) with RadioFieldTrait

  abstract class SingleSelectField(ilk: String) extends Child(ilk, this) with SingleSelectFieldTrait

  abstract class MultiSelectField(ilk: String) extends Child(ilk, this) with MultiSelectFieldTrait

  abstract class Button(ilk: String) extends Child(ilk, this) with ButtonTrait

  abstract class ChildContainer(ilk: String) extends Child(ilk, this) with Container

  abstract class ButtonRow extends Child("br", this) with Container

  abstract class HorizontalLayout extends Child("hl", this) with HorizontalLayoutContainer

}

trait DynamicChildren extends Container {
  type T <: DynamicContainer

  override def reset(): Unit = {
    super.reset()
    _children --= dynamics
  }

  override def renderChild(child: Component) = super.renderChild(child) ++ {
    child match {case dc: DynamicContainer => form.hidden(name, dc.dynamicId) case _ => NodeSeq.Empty}
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
  val PN_ID = "t-id"
  val PN_ILK = "t-ilk"
  val PN_MODAL = "t-modal"
  val PN_ACTION_REASON = "t-action-reason"
  val PN_ACTION_ID = "t-action-id"
  val PN_ACTION_NAME = "t-action-name"

  val ACTION_SUBMIT_ON_CHANGE = "submit-on-change"
}

object FormUtils {
  val escaper = UrlEscapers.urlFormParameterEscaper()

  def toQueryString(parameters: Seq[(String, String)]) = parameters.map(e => escaper.escape(e._1) + "=" + escaper.escape(e._2)).mkString("&")
}

class Form(val ilk: String) extends Container with CancelStateInheritance with Loggable {

  import FormConstants._

  override final val prefixForChildNames: String = ""

  override final def form: Form = this

  override final lazy val name: String = ilk

  override final def parent: Container = this

  final val request: Request = Request

  override final val id: IdString = request.parameters.getString(FormConstants.PN_ID, IdGenerator.next())

  final val formId = id ~ "form"

  final val modalId = id ~ "modal"

  final val modal = request.parameters.getBoolean(FormConstants.PN_MODAL, default = false)

  lazy val actionName = request.parameters.getString(PN_ACTION_NAME, "")

  lazy val actionReason = request.parameters.getString(PN_ACTION_REASON, "")

  override def translator = Request.current.translator.usage("FORM").usage(ilk)

  def hidden(name: String, value: String): NodeSeq = <input type="hidden" autocomplete="off" name={name} value={value} />

  def isSubmittedOnChange: Boolean = actionReason == ACTION_SUBMIT_ON_CHANGE

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

  override def replaceContentJs = beforeReplaceContentJs ~ jQuery(formId).call("updateFormElements", html)

  def beforeReplaceContentJs = descendants.collect { case c if c.isEnabled && c != this => c.replaceContentJs }

  override def javascript: JsCmd = if (isDisabled) JsEmpty else descendants.collect { case c if c.isEnabled && c != this => c.javascript }

  def focusJs = descendants.collectFirst({ case f: Focusable if f.needsFocus => f.focusJs }) getOrElse refocusJs

  def refocusJs = focusedId.fold(JsEmpty)(id => jQuery(id).call("focus"))

  def focusedId = request.parameters.getStringOption("t-focused-id").map(IdString.apply)

  def actionLinkWithContextPathAppIdAndParameters(parameters: Seq[(String, String)]): String = actionLink + queryString(addAppIdAndModal(addComponentParameters(parameters)))

  def actionLinkWithContextPathAndParameters(parameters: Seq[(String, String)]): String = actionLink + queryString(addComponentParameters(parameters))

  def actionLink: String = Request.contextPath + request.path

  def queryString(parameters: Seq[(String, String)]) = "?" + FormUtils.toQueryString(parameters)

  def addAppIdAndModal(parameters: Seq[(String, String)]) = {
    val keyValues = (FormConstants.PN_ILK -> ilk) +: (FormConstants.PN_ID -> id.string) +: (FormConstants.PN_MODAL -> modal.toString) +: addComponentParameters(parameters)
    if (ApplicationSettings.name != ApplicationSettings.DEFAULT_NAME) (ApplicationSettings.PN_NAME -> ApplicationSettings.name) +: keyValues else keyValues
  }

  def addComponentParameters(parameters: Seq[(String, String)]) = componentParameters ++ parameters

  def componentParameters: Seq[(String, String)] = descendants.toSeq.flatMap(_.linkParameters)

  // collectFirst does not work (for what ever reason)
  lazy val defaultButtonOption: Option[DefaultButton] = descendants.find(_.isInstanceOf[DefaultButton]).asInstanceOf[Option[DefaultButton]]

  def modalHtml: NodeSeq =
    if (isIgnored) NodeSeq.Empty
    else
    <div id={modalId} class="modal fade" role="dialog" name={name + "-modal"} tabindex="-1">
      <div class="modal-dialog">
        {surround(modalContent, "true")}
      </div>
    </div>

  def inlineHtml: NodeSeq =
    if (isIgnored) NodeSeq.Empty
    else surround(inlineContent, "false") ++ javascriptHtml

  override def enabledComponentHtml: NodeSeq = surround({if (modal) modalContent else inlineContent})

  def surround(content: NodeSeq, modalValue: String = modal.toString) =
    <form id={formId} name={name} class={formCssClasses} action={actionLink} method="post" enctype="multipart/form-data">
      {hidden(FormConstants.PN_ILK, ilk) ++ hidden(FormConstants.PN_ID, id) ++ hidden(FormConstants.PN_MODAL, modalValue) ++ hidden(ApplicationSettings.PN_NAME, Request.applicationSettings.name)}
      {content}
    </form>

  def formCssClasses: Seq[String] = "t-form" :: Nil

  def modalContent: NodeSeq =
    <div class="modal-content">
      <div class="modal-header">
       <button type="button" class="close" data-dismiss="modal">×</button>
        {formHeaderContent}
      </div>
      <div class="modal-body">{formBody}</div>
    </div>

  def inlineContent: NodeSeq =
    <div class="modal transfer-modal">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h4 class="modal-title">{t"transfer-modal.header: Transfering data ..."}</h4>
          </div>
          <div class="modal-body">
            <div class="progress progress-striped active">
              <div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
                <span class="sr-only"><span class="transfer-percent">0</span>{t"transfer-modal.sr-body: % Complete"}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div> ++ formHeader ++ formBody

  def formHeader = if (formHeaderContent.isEmpty) formHeaderContent else <header class="form-header">{formHeaderContent}</header>

  def formHeaderContent: NodeSeq = formTitleChecked ++ formDescription

  private def formTitleChecked = formTitleString match {case "" => NodeSeq.Empty case s => formTitleHtml }

  def formTitleHtml = <h3>{formTitleString}</h3>

  def formTitleString = t"form-title: #$name"

  def formDescription = formDescriptionString match {case "" => NodeSeq.Empty case s => Unparsed(s) }

  def formDescriptionString = t"form-description:"

  def formBody = defaultButtonHtml ++ super.enabledComponentHtml

  def javascriptHtml = javascript.toString match {case "" => NodeSeq.Empty case js => <script>{Unparsed("document.addEventListener('twibs-loaded', function() {" + js + "}, false)")}</script> }

  def defaultButtonHtml: NodeSeq = defaultButtonOption match {
    case Some(b) => b.defaultButtonHtml
    case None => <input type="submit" class="concealed" tabindex="-1" name="fallback-default" value="" />
  }

  validateSettings()
}