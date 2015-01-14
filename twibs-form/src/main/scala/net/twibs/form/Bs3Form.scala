/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form

import net.twibs.util.JavaScript._
import net.twibs.util.XmlUtils._
import net.twibs.util.{ApplicationSettings, DisplayType, Message, Request}

import scala.xml.{NodeSeq, Text, Unparsed}

sealed trait Bs3Container extends Container {
  override def html = {
    if (isIgnored) NodeSeq.Empty
    else if (isHidden) super.html
    else {
      <div id={shellId} name={name} class={("form-container-shell" :: Nil).addClass(isDetachable, "detachable")}>
        {if (isDetachable) closeButton else NodeSeq.Empty}
        {containerCore}
      </div>
    }
  }

  def closeButton = {
    def dismissButton: NodeSeq = <button type="button" class="btn btn-danger" data-dismiss="detachable">{t"delete-component.button-title: Delete"}</button>

    if (isEnabled) <button type="button" class="close" data-toggle="popover" data-html="true" data-placement="auto left" data-title={t"delete-component.popover-title: Delete component?"} data-content={dismissButton}>&times;</button>
    else NodeSeq.Empty
  }

  def containerCore: NodeSeq =
    <div id={id} class="form-container">
      {messagesHtml}
      {super.html}
    </div>

  def messagesHtml = renderMessages(messages)

  def renderMessages(messages: Seq[Message]) = messages.map(renderMessage)

  def renderMessage(message: Message): NodeSeq =
    if (message.dismissable)
        <div class={"alert" :: ("alert-" + message.displayTypeString) :: "alert-dismissable" :: Nil}><button type="button" class="close" data-dismiss="alert">×</button>{message.text}</div>
    else
        <div class={"alert" :: ("alert-" + message.displayTypeString) :: Nil}>{message.text}</div>

  def hidden(name: String, value: String): NodeSeq = <input type="hidden" autocomplete="off" name={name} value={value} />

  trait ButtonRenderer extends DisplayType {
    def buttonCssClasses = "btn" :: ("btn-" + displayTypeString) :: Nil

    def renderButtonTitle = if (buttonUseIconOnly) buttonIconOrButtonTitleIfEmptyHtml else buttonTitleWithIconHtml

    def buttonUseIconOnly = false

    def buttonIconOrButtonTitleIfEmptyHtml: NodeSeq = buttonIconHtml match {case NodeSeq.Empty => buttonTitleHtml case s => s }

    def buttonTitleWithIconHtml: NodeSeq = buttonIconHtml match {case NodeSeq.Empty => buttonTitleHtml case ns => if (buttonIconBefore) ns ++ Text(" ") ++ buttonTitleHtml else buttonTitleHtml ++ Text(" ") ++ ns }

    def buttonIconBefore = true

    def buttonIconHtml: NodeSeq = buttonIconName match {
      case "" => NodeSeq.Empty
      case s if s.startsWith("fa-") => <span class={s"fa $s"}></span>
      case s if s.startsWith("glyphicon-") => <span class={s"glyphicon $s"}></span>
      case s => <span class={s"glyphicon glyphicon-$s"}></span>
    }

    def buttonTitleHtml: NodeSeq = Unparsed(buttonTitle)

    def buttonTitle: String

    def buttonIconName: String
  }

  abstract class Button(ilk: String) extends super.Button(ilk) with ButtonRenderer {
    override def html =
      this match {
        case b: Options => b.optionEntries.map(o => render(o.string, o.index)).flatten
        case _ => render(string, 0)
      }

    def render(string: String, index: Int): NodeSeq = {
      if (isHidden) NodeSeq.Empty
      else if (isDisabled) <span class={"disabled" :: buttonCssClasses}>{renderButtonTitle}</span>
      else <button type="submit" name={name} id={indexId(index)} class={"can-be-disabled" :: buttonCssClasses} value={string}>{renderButtonTitle}</button>
    }
  }

  trait EnabledForm extends Button {
    override def buttonCssClasses: List[String] = "enabled-form" +: super.buttonCssClasses
  }

  trait NoRefocus extends Button {
    override def buttonCssClasses: List[String] = "no-refocus" +: super.buttonCssClasses
  }

  trait DefaultButton extends super.DefaultButton {
    override def defaultButtonHtml: NodeSeq = <input type="submit" class="concealed" tabindex="-1" name={name} value={defaultButtonValue} />

    private def defaultButtonValue = this match {
      case b: Options if b.optionEntries.nonEmpty => b.optionEntries.head.string
      case _ => string
    }
  }

  abstract class Popover(ilk: String) extends StaticContainer(ilk) with ButtonRenderer {
    def popoverContainer = form.shellId.toCssId

    def popoverPlacement = "bottom"

    def popoverTitle = t"popover-title:"

    def popoverContentText = t"popover-text:"

    def buttonTitle = t"button-title: #$ilk"

    def buttonIconName = t"button-icon:"

    override def html: NodeSeq =
      if (isIgnored) NodeSeq.Empty
      else if (isHidden) super.html
      else if (isDisabled) <span class={"disabled" :: buttonCssClasses}>{renderButtonTitle}</span>
      else <button type="button" class={"can-be-disabled" :: buttonCssClasses}  data-container={popoverContainer} data-toggle="popover" data-html="true" data-placement={popoverPlacement} data-title={popoverTitle} data-content={super.html}>{renderButtonTitle}</button>
  }

  trait LinkButton extends Button {
    override def render(string: String, index: Int): NodeSeq = {
      if (isHidden) NodeSeq.Empty
      else if (isDisabled) <span class={"disabled" :: buttonCssClasses}>{renderButtonTitle}</span>
      else <a href="#" class={"can-be-disabled" :: buttonCssClasses} data-call={link(name, string)}>{renderButtonTitle}</a>
    }

    def link(parameters: Seq[(String, String)]) = form.actionLinkWithContextPathAppIdAndParameters(parameters)
  }

  abstract class OpenModalLink extends Button("open-modal") with LinkButton with Floating {
    override def execute(): Seq[Result] = InsteadOfFormDisplay(form.openModalJs)

    override def link(parameters: Seq[(String, String)]) = form.actionLinkWithContextPathAndParameters(parameters)
  }

  abstract class Hidden(ilk: String) extends super.Hidden(ilk) {
    override def html: NodeSeq =
      if (isIgnored) NodeSeq.Empty
      else entries.map(entry => hidden(name, entry.string)).flatten
  }

  trait Field extends super.Field {
    override def html = entries.map(renderEntry).flatten

    def fieldCssClasses: Seq[String] = Nil

    def renderEntry(entry: Entry): NodeSeq =
      if (isIgnored) NodeSeq.Empty
      else if (isHidden) renderHidden(entry)
      else inner(entry)

    protected def inner(entry: Entry): NodeSeq

    protected def renderHidden(entry: Entry) = hidden(name, entry.string)
  }

  abstract class SingleLineField(ilk: String) extends super.SingleLineField(ilk) with Field {
    protected def inner(entry: Entry) =
        <input type="text" name={name} id={indexId(entry.index)} placeholder={placeholder} value={entry.string} class={fieldCssClasses}/>
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
        .addClass(submitOnChange && isEnabled, "submit-on-change")
        .set(maximumLength < Int.MaxValue, "maxlength", maximumLength.toString)
  }

  abstract class MultiLineField(ilk: String) extends super.MultiLineField(ilk) with Field {
    protected def inner(entry: Entry) =
      <textarea rows={rows.toString} name={name} id={indexId(entry.index)} placeholder={placeholder} class={fieldCssClasses}>{entry.string}</textarea>
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
        .addClass(submitOnChange && isEnabled, "submit-on-change")
        .set(maximumLength < Int.MaxValue, "maxlength", maximumLength.toString)

    def rows = 6

    override protected def renderHidden(entry: Entry): NodeSeq = <textarea class="concealed" name={name}>{entry.string}</textarea>
  }

  abstract class HtmlField(ilk: String) extends MultiLineField(ilk) {
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

    override def focusJs: JsCmd = jQuery(entries.find(!_.valid).map(e => indexId(e.index)) getOrElse id).call("ckeditorGet").call("focus")
  }

  trait FieldWithOptions extends super.FieldWithOptions {
    def triggerValue = "TRIGGER"

    override def parse(parameterStrings: Seq[String]): Unit = super.parse(parameterStrings.filter(_ != triggerValue))

    def triggerHtml = hidden(name, triggerValue)

    override def html =
      if (isIgnored) NodeSeq.Empty
      else if (isHidden) entries.map(entry => hidden(name, entry.string)).flatten ++ triggerHtml
      else renderOptions

    def renderOptions: NodeSeq = optionEntries.map(renderOption).flatten ++ triggerHtml

    def renderOption(option: Entry): NodeSeq
  }

  trait CheckboxOrRadioField extends FieldWithOptions {
    def renderOption(option: Entry): NodeSeq =
        <input type={inputType} name={name} id={indexId(option.index)} value={option.string}/>
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
        .addClass(submitOnChange && isEnabled, "submit-on-change")
        .set(values.contains(option.valueOption.get), "checked")

    def inputType: String
  }

  trait BsCheckboxField extends CheckboxOrRadioField {
    override def inputType: String = "checkbox"
  }

  trait BsRadioField extends CheckboxOrRadioField {
    override def inputType: String = "radio"
  }

  abstract class CheckboxField(ilk: String) extends super.CheckboxField(ilk) with BsCheckboxField

  class BooleanCheckboxField(ilk: String) extends super.BooleanCheckboxField(ilk) with BsCheckboxField

  abstract class RadioField(ilk: String) extends super.RadioField(ilk) with BsRadioField

  trait Chosen extends SelectField with Field {
    override def fieldCssClasses = (if (required) "chosen" else "chosen-optional") +: super.fieldCssClasses
  }

  abstract class SingleSelectField(ilk: String) extends super.SingleSelectField(ilk) with FieldWithOptions with Field {
    protected def inner(entry: Entry) =
      <select name={name} id={indexId(entry.index)} data-placeholder={placeholder} class={fieldCssClasses}>{renderOptions}</select>
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
        .addClass(submitOnChange && isEnabled, "submit-on-change")

    override def optionEntries: Seq[Entry] =
      if (required) super.optionEntries
      else Entry("", None, "", None) +: super.optionEntries

    override def renderOption(option: Entry): NodeSeq =
      <option value={ option.string }>{ option.title }</option>.set(option.string == string, "selected")
  }

  abstract class MultiSelectField(ilk: String) extends super.MultiSelectField(ilk) with FieldWithOptions with Field {
    protected def inner(entry: Entry) =
      <select name={name} id={indexId(entry.index)} data-placeholder={placeholder} multiple="multiple" class={fieldCssClasses}>{renderOptions}</select>
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
        .addClass(submitOnChange && isEnabled, "submit-on-change")

    override def renderOption(option: Entry): NodeSeq =
      <option value={ option.string }>{ option.title }</option>.set(strings.contains(option.string), "selected")
  }

  class StaticContainer(ilk: String) extends super.StaticContainer(ilk) with Bs3Container

  abstract class DynamicContainer(ilk: String) extends super.DynamicContainer(ilk) with Bs3Container {

    class Dynamic(ilk: String, dynamicId: String) extends super.Dynamic(ilk, dynamicId) with Bs3Container {
      override def html =
        if (isIgnored) NodeSeq.Empty
        else super.html ++ hidden(parent.name, dynamicId)
    }

  }

  class HorizontalLayout extends StaticContainer("hl") with Bs3HorizontalLayout

  class ButtonRow extends StaticContainer("br") with Bs3Container {
    override def html =
      if (isIgnored) NodeSeq.Empty
      else if (isHidden) super.html
      else <div>{containerCore}</div>
  }

}

trait Bs3HorizontalLayout extends Bs3Container {
  override def html: NodeSeq = <div class="form-horizontal">{super.html}</div>

  def ->>(nodeSeq: => NodeSeq) = new DisplayHtml(<div class="form-group"><div class="col-sm-offset-3 col-sm-9">{nodeSeq}</div></div>)

  trait Messages extends InputComponent {
    def labelCssMessageClass = if (validated) max(messages ++ entries.map(_.messageOption).flatten) else ""

    def max(messages: Seq[Message]) = messages match {
      case x if x.isEmpty => ""
      case x => cssMessageClass(x.maxBy(_.importance))
    }

    def cssMessageClass(messageOption: Option[Message]): String = messageOption.fold("")(cssMessageClass)

    def cssMessageClass(message: Message): String = if (validated) "has-" + message.displayTypeString else ""

    def renderMessages = if (validated) messages.map(m => <div class={cssMessageClass(m)}><div class="help-block">{m.text}</div></div>) else NodeSeq.Empty
  }

  trait Field extends super.Field with Messages {
    override def fieldCssClasses: Seq[String] = "form-control" +: super.fieldCssClasses

    override def html: NodeSeq =
      if (isHidden || isFloating) super.html
      else
        <div class="form-group">
            <div class={labelCssMessageClass :: "col-sm-3" :: Nil}><label class="control-label">{fieldTitle}</label></div>
            <div class="col-sm-9">{renderMessages ++ super.html}</div>
          </div>.addClass(required, "required")

    def renderEntryMessage(entry: Entry) =
      if (validated)
        entry.messageOption.fold(NodeSeq.Empty)(message => <div class="help-block">{message.text}</div>)
      else NodeSeq.Empty

    override def renderEntry(entry: Entry): NodeSeq =
      if (isHidden)
        super.renderEntry(entry)
      else
        <div class={cssMessageClass(entry.messageOption)}>
          {super.renderEntry(entry)}
          {renderEntryMessage(entry)}
        </div>
  }

  trait FieldWithOptions extends super.FieldWithOptions with Field

  trait SelectField extends super.SelectField with FieldWithOptions

  trait CheckboxOrRadioField extends super.CheckboxOrRadioField with Messages {
    override def renderOptions: NodeSeq =
      <div class="form-group">
          <div class={labelCssMessageClass :: "col-sm-3" :: Nil}><label class="control-label">{fieldTitle}</label></div>
          <div class="col-sm-9">{renderMessages ++ super.renderOptions}</div>
        </div>.addClass(required, "required")

    override def renderOption(option: Entry): NodeSeq =
        <div class="checkbox">
          <label>
            {super.renderOption(option)} {option.title}
          </label>
        </div>
  }

  abstract class SingleLineField(ilk: String) extends super.SingleLineField(ilk) with Field

  abstract class MultiLineField(ilk: String) extends super.MultiLineField(ilk) with Field

  abstract class HtmlField(ilk: String) extends super.HtmlField(ilk) with Field

  abstract class CheckboxField(ilk: String) extends super.CheckboxField(ilk) with CheckboxOrRadioField

  abstract class RadioField(ilk: String) extends super.RadioField(ilk) with CheckboxOrRadioField

  class BooleanCheckboxField(ilk: String) extends super.BooleanCheckboxField(ilk) with CheckboxOrRadioField

  abstract class SingleSelectField(ilk: String) extends super.SingleSelectField(ilk) with SelectField

  abstract class MultiSelectField(ilk: String) extends super.MultiSelectField(ilk) with SelectField

  abstract class Button(ilk: String) extends super.Button(ilk) {
    override def html: NodeSeq =
      if (isHidden || isFloating) super.html
      else
          <div class="form-group">
            <div class="col-sm-offset-3 col-sm-9">{super.html}</div>
          </div>
  }

  class ButtonRow extends StaticContainer("br") with Bs3Container {
    override def html =
      if (isIgnored) NodeSeq.Empty
      else if (isHidden) super.html
      else <div class="form-group"><div class="col-sm-offset-3 col-sm-9">{containerCore}</div></div>
  }

}

trait Bs3Form extends Form with Bs3Container {
  def formHeader = if (formHeaderContent.isEmpty) formHeaderContent else <header class="form-header">{formHeaderContent}</header>

  def formHeaderContent: NodeSeq = formTitle ++ formDescription

  def formTitle = formTitleString match {case "" => NodeSeq.Empty case s => <h3>{s}</h3> }

  def formTitleString = t"form-title: #$name"

  def formDescription = formDescriptionString match {case "" => NodeSeq.Empty case s => Unparsed(s) }

  def formDescriptionString = t"form-description:"

  override def modalHtml: NodeSeq =
    if (isIgnored) NodeSeq.Empty
    else
    <div id={modalId} class="modal fade" role="dialog" name={name + "-modal"} tabindex="-1">
      <div class="modal-dialog">
        {surround(modalContent, "true")}
      </div>
    </div>

  override def inlineHtml: NodeSeq =
    if (isIgnored) NodeSeq.Empty
    else surround(inlineContent, "false") ++ javascriptHtml

  override def html: NodeSeq = surround({if (modal) modalContent else inlineContent})

  def surround(content: NodeSeq, modalValue: String = modal.toString) =
    <form id={formId} name={name} class={formCssClasses} action={actionLinkWithContextPath} method="post" enctype="multipart/form-data">
      {hidden(pnId, id) ++ hidden(pnModal, modalValue) ++ hidden(ApplicationSettings.PN_NAME, Request.applicationSettings.name)}
      {content}
    </form>

  def formCssClasses = "twibs-form" :: Nil

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

  def formBody = defaultButtonHtml ++ super.html

  def javascriptHtml = javascript.toString match {case "" => NodeSeq.Empty case js => <script>{Unparsed("$(function () {" + js + "});")}</script> }

  def defaultButtonHtml: NodeSeq = defaultButtonOption match {
    case Some(b) => b.defaultButtonHtml
    case None => <input type="submit" class="concealed" tabindex="-1" name="fallback-default" value="" />
  }
}

trait Bs3HorizontalForm extends Form with Bs3HorizontalLayout with Bs3Form
