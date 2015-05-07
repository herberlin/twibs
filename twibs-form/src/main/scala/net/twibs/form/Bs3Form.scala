/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form

import net.twibs.util.XmlUtils._
import net.twibs.util.{ApplicationSettings, Message, Request}

import scala.xml.{Elem, NodeSeq, Unparsed}

sealed trait Bs3Component extends Component {

  implicit class Bs3RichElem(elem: Elem) {
    def addPopover(messageOption: Option[Message], placement: String = "bottom"): Elem = messageOption.fold(elem)(addPopover(_, placement))

    def addPopover(message: Message, placement: String): Elem =
      elem
        .set("data-toggle", "popover")
        .set("data-content", message.toBsMessageAttribute)
        .set("data-placement", placement)
        .set("data-trigger", "hover focus")
        .set("data-html", "true")
        .addClass(message.messageCssClass)

    def addTooltip(messageOption: Option[Message], placement: String = "bottom"): Elem = messageOption.fold(elem)(addTooltip(_, placement))

    def addTooltip(message: Message, placement: String): Elem =
      elem
        .set("data-toggle", "tooltip")
        .set("data-title", message.text.toString())
        .set("data-placement", placement)
        .set("data-trigger", "hover focus")
        .set("data-html", "true")
        .addClass(message.messageCssClass)
  }

  implicit class Bs3RichMessage(message: Message) {
    def toBsMessage = <div class={s"alert alert-${message.displayTypeString}"}>{message.text}</div>

    def toBsMessageAttribute: String = toBsMessage.toString()
  }

}

sealed trait Bs3Container extends Container {
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

  override def containerCssClasses = "form-container" +: super.containerCssClasses

  //  override def renderMessage(message: Message): NodeSeq =
  //    if (message.dismissable)
  //        <div class={"alert" :: ("alert-" + message.displayTypeString) :: "alert-dismissable" :: Nil}><button type="button" class="close" data-dismiss="alert">×</button>{message.text}</div>
  //    else
  //        <div class={"alert" :: ("alert-" + message.displayTypeString) :: Nil}>{message.text}</div>

  trait Bs3Field extends Field {
    override def controlCssClasses: Seq[String] = "form-control" +: super.controlCssClasses
  }

  /* Buttons */

  trait ButtonTrait extends super.ButtonTrait with Bs3Component {
    override def optionHtmlFor(option: Entry): NodeSeq = new Bs3OptionRenderer(option).html

    class Bs3OptionRenderer(option: Entry) extends OptionRenderer(option) {
      override def controlCssClasses = "btn" +: ("btn-" + displayTypeString) +: super.controlCssClasses

      override def displayTypeString = translator.translate("display-type", super.displayTypeString)

      def buttonIconName = translator.translate("icon", ButtonTrait.this.buttonIconName)

      override def buttonIconHtml: NodeSeq = buttonIconName match {
        case "" => NodeSeq.Empty
        case s if s.startsWith("fa-") => <span class={s"fa $s"}></span>
        case s if s.startsWith("glyphicon-") => <span class={s"glyphicon $s"}></span>
        case s => <span class={s"glyphicon glyphicon-$s"}></span>
      }

      def translator = ButtonTrait.this.translator.usage("values").usage(option.string)
    }

  }

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

  trait SingleLineFieldTrait extends super.SingleLineFieldTrait with Bs3Field

  trait MultiLineFieldTrait extends super.MultiLineFieldTrait with Bs3Field

  trait HtmlFieldTrait extends super.HtmlFieldTrait with Bs3Field

  trait CheckboxFieldTrait extends super.CheckboxFieldTrait

  trait RadioFieldTrait extends super.RadioFieldTrait

  trait SingleSelectFieldTrait extends super.SingleSelectFieldTrait with Bs3Field

  trait MultiSelectFieldTrait extends super.MultiSelectFieldTrait with Bs3Field

  trait ChildContainerTrait extends super.ChildContainerTrait with Bs3Container

  abstract class HorizontalLayout extends Child("hl") with Bs3HorizontalLayout

  /* Child constructors (plain copy from Form) */

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

trait Bs3HorizontalLayout extends Bs3Container {
  override def containerCssClasses = "form-horizontal" +: super.containerCssClasses

  def labelColumns = 3

  def contentColumns = 9

  def ->>(nodeSeq: => NodeSeq) = new DisplayHtml(<div class="form-group"><div class={s"col-sm-offset-$labelColumns col-sm-$contentColumns"}>{nodeSeq}</div></div>)

  /* Fields */

  trait Bs3HlControl extends Control with Bs3Component {
    override def treeHtml: NodeSeq = <div class="form-group">{formGroupContent}</div>.addClass(required, "required")

    def formGroupContent = controlTitle match {
      case "" =>
        <div class={s"col-sm-offset-$labelColumns col-sm-$contentColumns"}>{super.treeHtml}</div>
      case ct =>
        <div class={s"col-sm-$labelColumns" :: Nil}><label class={labelMessageCssClass :: "control-label" :: Nil}>{ct}{infoIcon}</label></div> ++
        <div class={s"col-sm-$contentColumns"}>{super.treeHtml}</div>
    }

    def infoIcon = infoMessageOption match {
      case None => NodeSeq.Empty
      case Some(string) =>
        val title = infoMessageOption.fold("")(_ => infoMessageTitle)
        <span class="info-icon fa fa-info-circle"></span>
          .set(!title.isEmpty, "data-title", title)
          .set("data-toggle", "popover")
          .set("data-content", string)
          .set("data-placement", "bottom")
          .set("data-trigger", "hover focus")
          .set("data-container", form.formId.toCssId)
          .set("data-html", "true")
    }

    override def controlHtml = super.controlHtml.addTooltip(validationMessageOption.filter(_ => validated), "top")

    //    override def infoMessageHtml: NodeSeq =
    //      infoMessageOption.fold(NodeSeq.Empty) { m =>
    //        val title = infoMessageTitle
    //        <span class="info-message" data-toggle="popover" data-trigger="hover click focus" data-placement="right" data-content={m} data-html="true"><span class="fa fa-info-circle"></span></span>
    //          .setIfMissing(!title.isEmpty, "title", title)
    //          .setIfMissing(!title.isEmpty, "data-title", title)
    //      }

    override def helpMessageHtml: NodeSeq = helpMessageOption.fold(NodeSeq.Empty)(m => <div class="help-block">{Unparsed(m)}</div>)
  }

  trait Bs3HlField extends Bs3Field with Bs3HlControl {

    //    def renderEntryMessage(entry: Entry) =
    //      if (validated)
    //        entry.messageOption.fold(NodeSeq.Empty)(message => <div class="help-block">{message.text}</div>)
    //      else NodeSeq.Empty
    //
    //    override def renderVisible2(entry: Entry): NodeSeq =
    //        <div class={cssMessageClass(entry.messageOption)}>
    //          {renderInputFor(entry)}
    //          {renderEntryMessage(entry)}
    //        </div>
    //
    //    def renderInputFor(entry: Entry): NodeSeq = NodeSeq.Empty
  }

  //  trait CheckboxOrRadioField extends super.CheckboxOrRadioField with FieldWithOptions {
  //    override def renderOptions: NodeSeq =
  //      <div class="form-group">
  //        <div class={labelCssMessageClass :: s"col-sm-$labelColumns" :: Nil}><label class="control-label">{fieldTitle}</label></div>
  //        <div class={s"col-sm-$contentColumns"}>{renderMessages ++ super.renderOptions}</div>
  //      </div>.addClass(required, "required")
  //
  //    override def renderOption(option: Entry): NodeSeq =
  //      <div class="checkbox">
  //        <label>
  //          {super.renderOption(option)} {option.title}
  //        </label>
  //      </div>
  //  }

  trait SingleLineFieldTrait extends super.SingleLineFieldTrait with Bs3HlField

  trait MultiLineFieldTrait extends super.MultiLineFieldTrait with Bs3HlField

  trait HtmlFieldTrait extends super.HtmlFieldTrait with Bs3HlField

  trait CheckboxFieldTrait extends super.CheckboxFieldTrait with Bs3HlControl {
    override def optionHtmlFor(option: Entry): NodeSeq =
      <div class="checkbox">
        <label>
          {super.optionHtmlFor(option)}
          {option.title}
        </label>
      </div>.addClass(isDisabled, "disabled")
  }

  trait RadioFieldTrait extends super.RadioFieldTrait with Bs3HlControl {
    override def controlHtmlFor(entry: Entry): NodeSeq =
      <div class="entry">{super.controlHtmlFor(entry)}</div>.addTooltip(entry.validationMessageOption.filter(_ => validated))

    override def optionHtmlFor(entry: Entry, option: Entry): NodeSeq =
      <div class="radio">
        <label>
          {super.optionHtmlFor(entry, option)}
          {option.title}
        </label>
      </div>.addClass(isDisabled, "disabled")
  }

  trait SingleSelectFieldTrait extends super.SingleSelectFieldTrait with Bs3HlField

  trait MultiSelectFieldTrait extends super.MultiSelectFieldTrait with Bs3HlField

  trait ButtonTrait extends super.ButtonTrait with Bs3HlControl

  trait ChildContainerTrait extends super.ChildContainerTrait with Bs3Container

  trait ButtonRowTrait extends super.ButtonRowTrait with Bs3Container {
    override def enabledTreeHtml = <div class="form-group"><div class={s"col-sm-offset-$labelColumns col-sm-$contentColumns"}>{super.enabledTreeHtml}</div></div>
  }

  //  trait Popover extends super.Popover with Bs3HorizontalLayout with ButtonRenderer

  /* Child constructors (plain copy from Form) */

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

trait Bs3Form extends Form with Bs3Container {
  def formHeader = if (formHeaderContent.isEmpty) formHeaderContent else <header class="form-header">{formHeaderContent}</header>

  def formHeaderContent: NodeSeq = formTitleChecked ++ formDescription

  private def formTitleChecked = formTitleString match {case "" => NodeSeq.Empty case s => formTitleHtml }

  def formTitleHtml = <h3>{formTitleString}</h3>

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

  override def enabledComponentHtml: NodeSeq = surround({if (modal) modalContent else inlineContent})

  def surround(content: NodeSeq, modalValue: String = modal.toString) =
    <form id={formId} name={name} class={formCssClasses} action={actionLink} method="post" enctype="multipart/form-data">
      {hidden(pnId, id) ++ hidden(pnModal, modalValue) ++ hidden(ApplicationSettings.PN_NAME, Request.applicationSettings.name)}
      {content}
    </form>

  def formCssClasses: Seq[String] = "twibs-form" :: Nil

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

  def formBody = defaultButtonHtml ++ super.enabledComponentHtml

  def javascriptHtml = javascript.toString match {case "" => NodeSeq.Empty case js => <script>{Unparsed("$(function () {" + js + "});")}</script> }

  def defaultButtonHtml: NodeSeq = defaultButtonOption match {
    case Some(b) => b.defaultButtonHtml
    case None => <input type="submit" class="concealed" tabindex="-1" name="fallback-default" value="" />
  }
}

trait Bs3HorizontalForm extends Bs3Form with Bs3HorizontalLayout {
  override def formCssClasses: Seq[String] = "form-horizontal" +: super.formCssClasses
}
