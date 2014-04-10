/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import scala.xml.{Elem, Unparsed, NodeSeq}
import twibs.form.base._
import twibs.util.JavaScript._
import twibs.util._
import twibs.web.{PostMethod, GetMethod, Request}

abstract class Form(val name: String) extends BaseForm {

  abstract class OpenModalLink(implicit val parent: BaseParentItem) extends BaseChildItemWithName with ButtonRenderer {
    def ilk = "open-modal-link"

    def html = <a href="#" class={buttonCssClasses} data-call={actionLinkWithContextPathAndParameters}>{buttonTitleWithIconHtml}</a>
  }

  protected def enctype = "multipart/form-data"

  def formCssClasses = "form-horizontal" :: "twibs-form" :: Nil

  override def form: Form = this

  /* Rendering */
  def inlineHtml =
    <div class="form-container">
      {formHeader}
      {formHtml(modal = false)}
      {javascript.toString match {case "" => NodeSeq.Empty case js => <script>{Unparsed("$(function () {" + js + "});")}</script>}}
    </div>

  def openModalJs = jQuery("body").call("append", modalHtml) ~ jQuery(modalId).call("twibsModal") ~ javascript

  def modalHtml =
    <div id={modalId} class="modal fade" role="dialog" name={name + "-modal"} tabindex="-1">
     <div class="modal-dialog">
       <div class="modal-content">
         <div class="modal-header">
           <button type="button" class="close" data-dismiss="modal">×</button>
           {formTitle}
         </div>
         <div class="modal-body">
           {formHtml(modal = true)}
         </div>
       </div>
     </div>
    </div>

  def formHtml(modal: Boolean) = if (!accessAllowed) noAccessHtml
  else
    <form id={id} name={name} class={formCssClasses} action={actionLinkWithContextPath} method="post" enctype={enctype}>
      {HiddenInputRenderer(BaseForm.PN_ID, id) ++ HiddenInputRenderer(BaseForm.PN_MODAL, "" + modal) ++ HiddenInputRenderer(ApplicationSettings.PN_NAME, requestSettings.applicationSettings.name)}
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
        {enrichedHtml}
      </div>
    </form>

  override def html = defaultButtonHtml ++ super.html

  private def defaultButtonHtml = items.collectFirst({case e: DefaultButton => e.renderAsDefault}) getOrElse NodeSeq.Empty

  def formHeader = if (formHeaderContent.isEmpty) formHeaderContent else <header class="form-header">{formHeaderContent}</header>

  def formHeaderContent: NodeSeq = formTitle

  def formTitle = formTitleString match {case "" => NodeSeq.Empty case s => <h3>{s}</h3> }

  def formTitleString = t"form-title: #$name"

  def refreshJs = replaceContentJs ~ javascript ~ focusJs

  def displayJs = Request.method match {
    case GetMethod => openModalJs
    case PostMethod => refreshJs
    case _ => JsEmpty
  }

  def javascript: JsCmd = if (!accessAllowed) JsEmpty else items.collect({case item: JavascriptItem => item.javascript})

  def focusJs = items.collectFirst({case item: Field if item.needsFocus => item.focusJs}) getOrElse JsEmpty

  def replaceContentJs = jQuery(contentId).call("html", enrichedHtml)

  def hideModalJs = jQuery(modalId).call("modal", "hide")

  def noAccessHtml: NodeSeq = <div class="alert alert-warning">{t"no-access-body: You have no permission to use this method."}</div>

  override val renderer: Renderer = new BootstrapRenderer
}

class BootstrapRenderer extends Renderer {
  def renderMessage(message: Message): NodeSeq =
    if (message.dismissable)
        <div class={"alert" :: ("alert-" + message.displayTypeString) :: "alert-dismissable" :: Nil}><button type="button" class="close" data-dismiss="alert">×</button>{message.text}</div>
    else
        <div class={"alert" :: ("alert-" + message.displayTypeString) :: Nil}>{message.text}</div>
}

trait JavascriptItem extends BaseItem {
  def javascript: JsCmd
}

trait FormGroupItem extends RenderedItem {
  override def html: NodeSeq =
    <div class={formGroupCssClasses}>
      <label class={formGroupTitleCssClasses} for={id}>{formGroupTitle}</label>
      <div class={controlContainerCssClasses}>{controlContainerHtml}</div>
    </div>

  def formGroupCssClasses = "form-group" :: Nil

  def formGroupTitleCssClasses = s"col-$gridSize-3" :: "control-label" :: Nil

  def controlContainerCssClasses = s"col-$gridSize-9" :: "controls" :: Nil

  def gridSize = "sm"

  def controlContainerHtml: NodeSeq

  def formGroupTitle: NodeSeq

  def id: IdString
}

trait LargeGridSize extends FormGroupItem {
  override def gridSize = "lg"
}

abstract class DisplayField private(val ilk: String, val parent: BaseParentItem, unit: Unit = Unit) extends BaseChildItemWithName with FormGroupItem {
  def this(ilk: String)(implicit parent: BaseParentItem) = this(ilk, parent)

  def formGroupTitle: NodeSeq = t"field-title: #$ilk"
}

abstract class Field private(val ilk: String, val parent: BaseParentItem, unit: Unit = Unit) extends BaseField with FormGroupItem {
  def this(ilk: String)(implicit parent: BaseParentItem) = this(ilk, parent)

  override def formGroupCssClasses = (messageDisplayTypeOption.map("has-" + _) getOrElse "") :: super.formGroupCssClasses.addClass(required, "required")

  def formGroupTitle: NodeSeq = fieldTitle

  def fieldTitle = t"field-title: #$ilk"

  def controlContainerHtml: NodeSeq = inputsAsHtml ++ messageHtml

  def messageHtml: NodeSeq = inputsMessageOption.map(message => <div class="help-block">{message.text}</div>) getOrElse NodeSeq.Empty

  protected def infoHtml: NodeSeq = infoMessage match {
    case "" => NodeSeq.Empty
    case m =>
      val title = infoTitle
      <span class="btn btn-default text-info" data-toggle="popover" data-placement="left" data-container={parent.form.id.toCssId} data-content={m} data-html="true"><span class="glyphicon glyphicon-info-sign"></span></span>
        .add(!title.isEmpty, "title", title)
        .add(!title.isEmpty, "data-title", title)
  }

  def infoTitle = translator.translateOrUseDefault("info-title", fieldTitle)

  def infoMessage = translator.translate("info-message", "Info message")

  def inputsAsHtml: NodeSeq = inputs.zipWithIndex.map(e => inputWithMessageHtml(e._1, e._2)).flatten

  def inputWithMessageHtml(input: Input, index: Int): NodeSeq = inputAsEnrichedHtml(input, index) ++ messageHtmlFor(input)

  def messageHtmlFor(input: Input): NodeSeq = input match {
    case i: InvalidInput if validated => <div class="help-block">{i.message}</div>
    case _ => NodeSeq.Empty
  }

  def inputAsEnrichedHtml(input: Input, index: Int): NodeSeq = inputAsEnrichedElem(input, index)

  def inputAsEnrichedElem(input: Input, index: Int): Elem = enrichInputElem(inputAsElem(input), index)

  def enrichInputElem(elem: Elem, index: Int): Elem =
    elem.add("name", name)
      .add("id", idForIndex(index))
      .addClasses(inputCssClasses)
      .addClass(isDisabled, "disabled")
      .addClass(!isDisabled, "can-be-disabled")
      .addClass(submitOnChange, "submit-on-change")
      .add(isDisabled, "disabled", "disabled")
      .add(name != ilk, "data-ilk", ilk)

  private def idForIndex(index: Int): String = id + (if (index > 0) index.toString else "")

  def inputAsElem(input: Input): Elem

  def inputCssClasses: List[String] = "form-control" :: Nil

  def needsFocus = !isDisabled && !isValid

  def focusJs = jQuery(id).call("focus")

  override def translator: Translator = super.translator.kind("FIELD")
}

trait Inline extends Field {
  override def html =
    <div class={formGroupCssClasses}>
      <label class={formGroupTitleCssClasses} for={id}>{formGroupTitle}</label>
      {inputsAsHtml}
    </div>

  override def infoHtml = NodeSeq.Empty

  override def formGroupTitleCssClasses = "sr-only" :: Nil
}

trait DefaultButton extends ButtonRenderer with Executable {
  def renderAsDefault = <input type="submit" class="concealed" tabindex="-1" name={name} value="" />
}

object Bootstrap {
  def withUntitledFormGroup(html: NodeSeq) =
    <div class="form-group">
      <div class="col-sm-offset-3 col-sm-9 controls">{html}</div>
    </div>
}

abstract class Button(val ilk: String)(implicit val parent: BaseParentItem) extends Executable with BootstrapButtonRenderer with RenderedItem {
  override def html: NodeSeq =
    <div class={formGroupCssClasses}>
      <div class={controlContainerCssClasses}>{buttonAsHtml}</div>
    </div>

  def formGroupCssClasses = "form-group" :: Nil

  def controlContainerCssClasses = "col-sm-offset-3" :: "col-sm-9" :: "controls" :: Nil

  override def enrichButtonElem(elem: Elem) =
    super.enrichButtonElem(elem)
      .add("name", name)
      .add(name != ilk, "data-ilk", ilk)
      .addClass(isDisabled, "disabled")
      .addClass(!isDisabled, "can-be-disabled")
      .add(isDisabled, "disabled", "disabled")

  override def translator: Translator = super.translator.kind("BUTTON")
}

abstract class SpecialButton(val ilk: String)(implicit val parent: BaseParentItem) extends BaseChildItemWithName with BootstrapButtonRenderer

trait FieldWithOptions extends Field with Options {
  override def reset(): Unit = {
    super.reset()
    resetOptions()
  }
}
