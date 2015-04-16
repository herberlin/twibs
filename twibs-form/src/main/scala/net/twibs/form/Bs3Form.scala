/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form

import net.twibs.util.XmlUtils._
import net.twibs.util.{ApplicationSettings, Message, Request}

import scala.xml.{NodeSeq, Unparsed}

sealed trait Bs3Container extends Container {
  override def visibleHtml: NodeSeq =
    <div id={shellId} name={name} class={("form-container-shell" :: Nil).addClass(isDetachable, "detachable")}>
      {if (isDetachable) closeButton else NodeSeq.Empty}
      <div id={id} class={containerCssClasses}>
        {super.visibleHtml}
      </div>
    </div>

  def closeButton = {
    def dismissButton: NodeSeq = <button type="button" class="btn btn-danger" data-dismiss="detachable">{t"delete-component.button-title: Delete"}</button>

    if (isEnabled) <button type="button" class="close" data-toggle="popover" data-html="true" data-placement="auto left" data-title={t"delete-component.popover-title: Delete component?"} data-content={dismissButton}>&times;</button>
    else NodeSeq.Empty
  }

  override def containerCssClasses = "form-container" +: super.containerCssClasses

  override def renderMessage(message: Message): NodeSeq =
    if (message.dismissable)
        <div class={"alert" :: ("alert-" + message.displayTypeString) :: "alert-dismissable" :: Nil}><button type="button" class="close" data-dismiss="alert">×</button>{message.text}</div>
    else
        <div class={"alert" :: ("alert-" + message.displayTypeString) :: Nil}>{message.text}</div>

  /* Buttons */

  abstract class Button(ilk: String) extends super.Button(ilk) {
    override def controlCssClasses = "btn" +: ("btn-" + displayTypeString) +: super.controlCssClasses

    override def buttonIconHtml: NodeSeq = buttonIconName match {
      case "" => NodeSeq.Empty
      case s if s.startsWith("fa-") => <span class={s"fa $s"}></span>
      case s if s.startsWith("glyphicon-") => <span class={s"glyphicon $s"}></span>
      case s => <span class={s"glyphicon glyphicon-$s"}></span>
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

  class ChildContainer(ilk: String) extends super.ChildContainer(ilk) with Bs3Container

  class HorizontalLayout extends ChildContainer("hl") with Bs3HorizontalLayout

  class ButtonRow extends ChildContainer("br") with Bs3Container
}

trait Bs3HorizontalLayout extends Bs3Container {
  override def containerCssClasses = "form-horizontal" +: super.containerCssClasses

  def labelColumns = 3

  def contentColumns = 9

  def ->>(nodeSeq: => NodeSeq) = new DisplayHtml(<div class="form-group"><div class={s"col-sm-offset-$labelColumns col-sm-$contentColumns"}>{nodeSeq}</div></div>)

  trait Messages extends super.Control {
    def labelCssMessageClass = if (validated) max(messages ++ entries.flatMap(_.messageOption)) else ""

    def max(messages: Seq[Message]) = messages match {
      case x if x.isEmpty => ""
      case x => cssMessageClass(x.maxBy(_.importance))
    }

    def cssMessageClass(messageOption: Option[Message]): String = messageOption.fold("")(cssMessageClass)

    def cssMessageClass(message: Message): String = if (validated) "has-" + message.displayTypeString else ""

    def renderMessages = if (validated) messages.map(m => <div class={cssMessageClass(m)}><div class="help-block">{m.text}</div></div>) else NodeSeq.Empty
  }

  /* Fields */

  trait Field extends super.Field with Messages {
    override def controlCssClasses: Seq[String] = "form-control" +: super.controlCssClasses

    override def controlHtml: NodeSeq =
      if (isFloating) super.visibleHtml
      else
        <div class="form-group">
          <div class={labelCssMessageClass :: s"col-sm-$labelColumns" :: Nil}><label class="control-label">{fieldTitle}</label></div>
          <div class={s"col-sm-$contentColumns"}>{renderMessages ++ super.visibleHtml}</div>
        </div>.addClass(required, "required")

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

  abstract class SingleLineField(ilk: String) extends super.SingleLineField(ilk) with Field

  abstract class MultiLineField(ilk: String) extends super.MultiLineField(ilk) with Field

  abstract class HtmlField(ilk: String) extends super.HtmlField(ilk) with Field

  abstract class CheckboxField(ilk: String) extends super.CheckboxField(ilk) with Field

  abstract class RadioField(ilk: String) extends super.RadioField(ilk) with Field

  abstract class SingleSelectField(ilk: String) extends super.SingleSelectField(ilk) with SelectField

  abstract class MultiSelectField(ilk: String) extends super.MultiSelectField(ilk) with SelectField

  /* Buttons */

  abstract class Button(ilk: String) extends super.Button(ilk) {
    override def controlHtml: NodeSeq =
      if (isFloating) super.visibleHtml
      else
        <div class="form-group">
          <div class={s"col-sm-offset-$labelColumns col-sm-$contentColumns"}>{super.visibleHtml}</div>
        </div>
  }

  /* Containers */

  abstract class ChildContainer(ilk: String) extends super.ChildContainer(ilk) with Bs3HorizontalLayout

  //  trait Popover extends super.Popover with Bs3HorizontalLayout with ButtonRenderer

  class ButtonRow extends super.ButtonRow with Bs3HorizontalLayout {
    override def visibleHtml = <div class="form-group"><div class={s"col-sm-offset-$labelColumns col-sm-$contentColumns"}>{super.visibleHtml}</div></div>
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

  override def visibleHtml: NodeSeq = surround({if (modal) modalContent else inlineContent})

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

  def formBody = defaultButtonHtml ++ super.visibleHtml

  def javascriptHtml = javascript.toString match {case "" => NodeSeq.Empty case js => <script>{Unparsed("$(function () {" + js + "});")}</script> }

  def defaultButtonHtml: NodeSeq = defaultButtonOption match {
    case Some(b) => b.defaultButtonHtml
    case None => <input type="submit" class="concealed" tabindex="-1" name="fallback-default" value="" />
  }
}

trait Bs3HorizontalForm extends Bs3Form with Bs3HorizontalLayout
