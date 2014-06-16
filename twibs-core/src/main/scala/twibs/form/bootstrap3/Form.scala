/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import scala.xml.{Elem, NodeSeq, Unparsed}

import twibs.form.base._
import twibs.util.{ApplicationSettings, IdString, Message, Translator}

abstract class Form(override val ilk: String) extends BaseForm {
  self =>

  override protected def computeName: String = ilk

  abstract class OpenModalLink extends BootstrapButton with StringValues with Floating with LinkButton {
    override def parent: Container = self

    override def ilk = "open-modal-link"
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
           {formTitle}
         </div>
         <div class="modal-body">
           {formHtml(modal = true)}
         </div>
       </div>
     </div>
    </div>

  def formHtml(modal: Boolean) = if (!isEnabled) noAccessHtml
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
        {enrichedHtml}
      </div>
    </form>

  def formHeader = if (formHeaderContent.isEmpty) formHeaderContent else <header class="form-header">{formHeaderContent}</header>

  def formHeaderContent: NodeSeq = formTitle

  def formTitle = formTitleString match {case "" => NodeSeq.Empty case s => <h3>{s}</h3> }

  def formTitleString = t"form-title: #$name"

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
}

trait FormGroupComponent extends Component {
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

trait LargeGridSize extends FormGroupComponent {
  override def gridSize = "lg"
}

abstract class DisplayField private(override val ilk: String, val parent: Container, unit: Unit = Unit) extends Component with FormGroupComponent {
  def this(ilk: String)(implicit parent: Container) = this(ilk, parent)

  def formGroupTitle: NodeSeq = t"field-title: #$ilk"
}

abstract class Field private(override val ilk: String, val parent: Container, unit: Unit = Unit) extends BaseField with FormGroupComponent {
  def this(ilk: String)(implicit parent: Container) = this(ilk, parent)

  override def formGroupCssClasses = messageDisplayTypeOption.fold("")("has-" + _) :: super.formGroupCssClasses.addClass(required, "required")

  def formGroupTitle: NodeSeq = fieldTitle

  def fieldTitle = t"field-title: #$ilk"

  def controlContainerHtml: NodeSeq = inputsAsHtml ++ messageHtml

  def messageHtml: NodeSeq = inputsMessageOption.fold(NodeSeq.Empty)(message => <div class="help-block">
    {message.text}
  </div>)

  protected def infoHtml: NodeSeq = infoMessage match {
    case "" => NodeSeq.Empty
    case m =>
      val title = infoTitle
      <span class="btn btn-default text-info" data-toggle="popover" data-placement="left" data-container={form.id.toCssId} data-content={m} data-html="true"><span class="glyphicon glyphicon-info-sign"></span></span>
        .setIfMissing(!title.isEmpty, "title", title)
        .setIfMissing(!title.isEmpty, "data-title", title)
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
    elem.setIfMissing("name", name)
      .setIfMissing("id", idForIndex(index))
      .addClasses(inputCssClasses)
      .addClass(isDisabled, "disabled")
      .addClass(!isDisabled, "can-be-disabled")
      .addClass(submitOnChange, "submit-on-change")
      .setIfMissing(isDisabled, "disabled", "disabled")
      .setIfMissing(name != ilk, "data-ilk", ilk)

  private def idForIndex(index: Int): String = id + (if (index > 0) index.toString else "")

  def inputAsElem(input: Input): Elem

  def inputCssClasses: List[String] = "form-control" :: Nil

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

object Bootstrap {
  def withUntitledFormGroup(html: NodeSeq) =
    <div class="form-group">
      <div class="col-sm-offset-3 col-sm-9 controls">{html}</div>
    </div>
}

abstract class Button(override val ilk: String)(implicit val parent: Container) extends Executable with Result with BootstrapButton

trait LinkButton extends BootstrapButton {
  override def buttonAsElem: Elem = <a href="#" data-call={dataCall}>{renderButtonTitle}</a>

  def dataCall = {
    val ret = form.actionLinkWithContextPathAndParameters
    valueOption match {
      case Some(v) => (if (ret.contains("?")) ret + "&" else ret + "?") + name + "=" + v
      case None => ret
    }
  }
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
