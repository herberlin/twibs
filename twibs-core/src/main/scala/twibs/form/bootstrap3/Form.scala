/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import scala.xml.{Elem, NodeSeq, Text, Unparsed}

import twibs.form.base._
import twibs.util.{ApplicationSettings, IdString, Message}

abstract class Form(override val ilk: String) extends BaseForm {
  self =>

  override protected def computeName: String = ilk

  abstract class OpenModalLink extends BootstrapButton with StringValues with Floating with LinkButton {
    override def parent: Container = self

    override def ilk = "open-modal-link"

    override def state: ComponentState = super.state.ignoreIf(!super.state.isEnabled)
  }

  protected def enctype = "multipart/form-data"

  def formCssClasses = "form-horizontal" :: "twibs-form" :: Nil

  /* Rendering */
  def inlineHtml =
    <div class="form-container">
      {formHeader}
      {formHtml(modal = false)}
      {javascript.toString match {case "" => NodeSeq.Empty case js => <script>{Unparsed("$(function () {" + js + "});")}</script>}}
    </div>

  def modalHtml =
    <div id={modalId} class="modal fade" role="dialog" name={name + "-modal"} tabindex="-1">
     <div class="modal-dialog">
       <div class="modal-content">
         <div class="modal-header">
           <button type="button" class="close" data-dismiss="modal">×</button>
           {formHeaderContent}
         </div>
         <div class="modal-body">
           {formHtml(modal = true)}
         </div>
       </div>
     </div>
    </div>

  def formHtml(modal: Boolean) =
    if (state.isHidden) noAccessHtml
    else if (state.isIgnored) NodeSeq.Empty
    else
      <form id={id} name={name} class={formCssClasses} action={actionLinkWithContextPath} method="post" enctype={enctype}>
      {renderer.hiddenInput(pnId, id) ++ renderer.hiddenInput(pnModal, "" + modal) ++ renderer.hiddenInput(ApplicationSettings.PN_NAME, requestSettings.applicationSettings.name)}
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
      </div>

      <div id={contentId}>
        {asHtml}
      </div>
    </form>

  def formHeader = if (formHeaderContent.isEmpty) formHeaderContent else <header class="form-header">{formHeaderContent}</header>

  def formHeaderContent: NodeSeq = formTitle ++ formDescription

  def formTitle = formTitleString match {case "" => NodeSeq.Empty case s => <h3>{s}</h3> }

  def formTitleString = t"form-title: #$name"

  def formDescription = formDescriptionString match {case "" => NodeSeq.Empty case s => Unparsed(s) }

  def formDescriptionString = t"form-description:"

  def noAccessHtml: NodeSeq = <div class="alert alert-warning">{t"no-access-body: You have no permission to use this method."}</div>

  override val renderer: Renderer = new BootstrapRenderer
}

class BootstrapRenderer extends Renderer {
  def renderMessage(message: Message): NodeSeq =
    if (message.dismissable)
        <div class={"alert" :: ("alert-" + message.displayTypeString) :: "alert-dismissable" :: Nil}><button type="button" class="close" data-dismiss="alert">×</button>{message.text}</div>
    else
        <div class={"alert" :: ("alert-" + message.displayTypeString) :: Nil}>{message.text}</div>

  override def hiddenInput(name: String, value: String): NodeSeq = <input type="hidden" autocomplete="off" name={name} value={value} />

  override def renderAsDefaultExecutable(executable: Executable): NodeSeq = <input type="submit" class="concealed" tabindex="-1" name={executable.name} value="" />
}

trait LargeGridSize extends Field {
  override def gridSize = "lg"
}

abstract class Field private(override val ilk: String, val parent: Container, unit: Unit = Unit) extends BaseField {
  def this(ilk: String)(implicit parent: Container) = this(ilk, parent)

  override def asHtml: NodeSeq =
    if (state.isIgnored) NodeSeq.Empty
    else if (state.isHidden) inputsAsHiddenHtml
    else if (state.isDisabled) inputsAsHiddenHtml ++ fieldAsDecoratedHtml
    else fieldAsDecoratedHtml

  def inputsAsHiddenHtml = inputs.map(input => form.renderer.hiddenInput(name, input.string)).flatten

  def fieldAsDecoratedHtml: NodeSeq =
    <div class={formGroupCssClasses} id={formGroupId}>
      <label class={formGroupTitleCssClasses} for={id}>{fieldTitleHtml}</label>
      <div class={fieldContainerCssClasses}>{fieldAsHtml}</div>
    </div>

  def formGroupId = id ~ "form-group"

  def formGroupCssClasses = (messageDisplayTypeOption.fold("")("has-" + _) :: "form-group" :: Nil).addClass(required, "required")

  def formGroupTitleCssClasses = s"col-$gridSize-3" :: "control-label" :: Nil

  def fieldContainerCssClasses = s"col-$gridSize-9" :: "controls" :: Nil

  def fieldTitleHtml: NodeSeq = Text(fieldTitle)

  def fieldTitle = t"field-title: #$ilk"

  def gridSize = "sm"

  def fieldAsHtml: NodeSeq = inputsAsHtml ++ messageHtml

  def messageHtml: NodeSeq = inputsMessageOption.fold(NodeSeq.Empty)(message => <div class="help-block">{message.text}</div>)

  protected def infoButtonHtml: NodeSeq = infoMessage match {
    case "" => NodeSeq.Empty
    case m =>
      val title = infoTitle
      <span class="btn btn-default field-info" data-toggle="popover" data-placement="left" data-container={form.id.toCssId} data-content={m} data-html="true"><span class="fa fa-info-circle"></span></span>
        .setIfMissing(!title.isEmpty, "title", title)
        .setIfMissing(!title.isEmpty, "data-title", title)
  }

  def infoTitle = translator.translateOrUseDefault("info-title", fieldTitle)

  def infoMessage = translator.translate("info-message", "Info message")

  def inputsAsHtml: NodeSeq = inputs.map(inputWithMessageHtml).flatten

  def inputWithMessageHtml(input: Input): NodeSeq = inputAsSurroundedHtml(input) ++ messageHtmlFor(input)

  def inputAsSurroundedHtml(input: Input): NodeSeq =
    addonsHtml match {
      case NodeSeq.Empty => inputAsEnrichedHtml(input)
      case a => surroundWithInputGroup(input, inputAsEnrichedHtml(input) ++ a)
    }

  def addonsHtml = suffixesDecoratedHtml ++ buttonsDecoratedHtml

  def suffixesDecoratedHtml: NodeSeq = suffixes.filter(_.nonEmpty).map(ns => <span class="input-group-addon">{ ns }</span>)

  def suffixes: List[NodeSeq] = suffix :: Nil

  def suffix: NodeSeq = NodeSeq.Empty

  def buttonsDecoratedHtml = <span class="input-group-btn" />.surround(buttons.filter(_.nonEmpty).flatten)

  def buttons: List[NodeSeq] = infoButtonHtml :: Nil

  def surroundWithInputGroup(input: Input, nodeSeq: NodeSeq) = <div class="input-group">{nodeSeq}</div>

  def messageHtmlFor(input: Input): NodeSeq =
    input.messageOption.filter(x => validated).fold(NodeSeq.Empty)(x => <div class="help-block">{x.text}</div>)

  def inputAsEnrichedHtml(input: Input): NodeSeq = enrichInputElem(inputAsElem(input), input)

  def enrichInputElem(elem: Elem, input: Input): Elem = enrichInputElem(elem, idForInput(input))

  def enrichInputElem(elem: Elem, inputId: IdString): Elem =
    elem.setIfMissing("name", name)
      .setIfMissing("id", inputId.string)
      .setIfMissing(state.isDisabled, "disabled", "disabled")
      .setIfMissing(name != ilk, "data-ilk", ilk)
      .addClasses(inputCssClasses)

  def idForInput(input: Input): IdString = id ~ (if (input.index > 0) input.index.toString else "")

  // TODO: Remove after transition to improved rendering process
  def inputAsElem(input: Input): Elem = <span></span>

  def inputCssClasses: List[String] =
    ("form-control" :: Nil)
      .addClass(submitOnChange && state.isEnabled, "submit-on-change")
      .addClass(state.isDisabled, "disabled")
      .addClass(state.isEnabled, "can-be-disabled")
}

trait Inline extends Field {
  override def fieldAsDecoratedHtml =
    <div class={formGroupCssClasses}>
      <label class={formGroupTitleCssClasses} for={id}>{fieldTitleHtml}</label>
      {inputsAsHtml}
    </div>

  override def infoButtonHtml = NodeSeq.Empty

  override def formGroupTitleCssClasses = "sr-only" :: Nil
}

object Bootstrap {
  def withUntitledFormGroup(html: NodeSeq) =
    <div class="form-group">
      <div class="col-sm-offset-3 col-sm-9 controls">{html}</div>
    </div>
}

abstract class Button private(override val ilk: String, val parent: Container, unit: Unit = Unit) extends Executable with Result with BootstrapButton {
  def this(ilk: String)(implicit parent: Container) = this(ilk, parent)
}

trait LinkButton extends BootstrapButton {
  override def buttonAsElem: Elem =
    if (state.isEnabled)
      <a href="#" data-call={dataCall}>{renderButtonTitle}</a>
    else
      <span>{renderButtonTitle}</span>

  def dataCall = form.actionLinkWithContextPathAndParameters(name -> stringOrEmpty)
}

trait EnabledForm extends Button {
  override def buttonCssClasses: List[String] = "enabled-form" :: super.buttonCssClasses
}

trait FieldWithOptions extends Field with Options {
  override def reset(): Unit = {
    super.reset()
    resetOptions()
  }
}
