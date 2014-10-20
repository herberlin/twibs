/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.form

import scala.xml.{NodeSeq, Text, Unparsed}

import net.twibs.util.XmlUtils._
import net.twibs.util.{ApplicationSettings, Message, RequestSettings}

sealed trait BsContainer extends Container {
  override def html = {
    if (isIgnored) NodeSeq.Empty
    else if (isHidden) super.html
    else {
      <div id={shellId} class={("form-container-shell" :: Nil).addClass(isDetachable, "detachable")}>
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

  abstract class Button(ilk: String) extends super.Button(ilk) {
    def renderButtonTitle = if (buttonUseIconOnly) buttonIconOrButtonTitleIfEmptyHtml else buttonTitleWithIconHtml

    def buttonUseIconOnly = false

    def buttonIconBefore = true

    def buttonTitleWithIconHtml: NodeSeq = buttonIconHtml match {case NodeSeq.Empty => buttonTitleHtml case ns => if (buttonIconBefore) ns ++ Text(" ") ++ buttonTitleHtml else buttonTitleHtml ++ Text(" ") ++ ns }

    def buttonIconOrButtonTitleIfEmptyHtml: NodeSeq = buttonIconHtml match {case NodeSeq.Empty => buttonTitleHtml case s => s }

    def buttonTitleHtml: NodeSeq = Unparsed(buttonTitle)

    def buttonIconHtml: NodeSeq = buttonIconName match {
      case "" => NodeSeq.Empty
      case s if s.startsWith("fa-") => <span class={s"fa $s"}></span>
      case s if s.startsWith("glyphicon-") => <span class={s"glyphicon $s"}></span>
      case s => <span class={s"glyphicon glyphicon-$s"}></span>
    }

    def btnCssClasses = "btn" :: ("btn-" + displayTypeString) :: Nil

    override def html =
      this match {
        case b: Options => b.optionEntries.map(o => render(o.string, o.index)).flatten
        case _ => render(string, 0)
      }

    def render(string: String, index: Int): NodeSeq = {
      if (isHidden) NodeSeq.Empty
      else if (isDisabled) <span class={"disabled" :: btnCssClasses}>{renderButtonTitle}</span>
      else <button type="submit" name={name} id={indexId(index)} class={"can-be-disabled" :: btnCssClasses} value={string}>{renderButtonTitle}</button>
    }
  }

  abstract class Hidden(ilk: String) extends super.InputComponent(ilk) {
    override def html: NodeSeq =
      if (isIgnored) NodeSeq.Empty
      else entries.map(entry => hidden(name, entry.string)).flatten
  }

  abstract class Field(ilk: String) extends super.Field(ilk) {
    override def html = entries.map(renderEntry).flatten

    def fieldCssClasses: Seq[String] = Nil

    def renderEntry(entry: Entry): NodeSeq =
      if (isIgnored) NodeSeq.Empty
      else if (isHidden) hidden(name, entry.string)
      else inner(entry)

    private def inner(entry: Entry) =
        <input type="text" name={name} id={indexId(entry.index)} placeholder={placeholder} value={entry.string} class={fieldCssClasses}/>
        .setIfMissing(isDisabled, "disabled", "disabled")
        .addClass(isDisabled, "disabled")
        .addClass(!isDisabled, "can-be-disabled")
  }

  def indexId(index: Int) = id ~ (if (index > 0) index.toString else "")

  class StaticContainer(ilk: String) extends super.StaticContainer(ilk) with BsContainer

  abstract class DynamicContainer(ilk: String) extends super.DynamicContainer(ilk) with BsContainer {

    class Dynamic(ilk: String, dynamicId: String) extends super.Dynamic(ilk, dynamicId) with BsContainer {
      override def html =
        if (isIgnored) NodeSeq.Empty
        else super.html ++ hidden(parent.name, dynamicId)
    }

  }

  def hidden(name: String, value: String): NodeSeq = <input type="hidden" autocomplete="off" name={name} value={value} />

  class HorizontalLayout extends StaticContainer("hl") {
    override def html: NodeSeq = <div class="form-horizontal">{super.html}</div>

    abstract class Field(ilk: String) extends super.Field(ilk) {
      override def fieldCssClasses: Seq[String] = "form-control" +: super.fieldCssClasses

      override def html: NodeSeq =
        if (isHidden || isFloating) super.html
        else
          <div class="form-group">
            <div class={labelCssMessageClass :: "col-sm-3" :: Nil}><label class="control-label">{fieldTitle}</label></div>
            <div class="col-sm-9">{renderMessages ++ super.html}</div>
          </div>

      def renderMessages = if (validated) messages.map(m => <div class={cssMessageClass(m)}><div class="help-block">{m.text}</div></div>) else NodeSeq.Empty

      def renderEntryMessage(entry: Entry) =
        if (validated)
          entry.messageOption.fold(NodeSeq.Empty)(message => <div class="help-block">{message.text}</div>)
        else NodeSeq.Empty

      override def renderEntry(entry: Entry): NodeSeq =
        <div class={cssMessageClass(entry.messageOption)}>
          {super.renderEntry(entry)}
          {renderEntryMessage(entry)}
        </div>

      def cssMessageClass(messageOption: Option[Message]): String = messageOption.fold("")(cssMessageClass)

      def cssMessageClass(message: Message): String = if (validated) "has-" + message.displayTypeString else ""

      def labelCssMessageClass = if (validated) max(messages ++ entries.map(_.messageOption).flatten) else ""

      def max(messages: Seq[Message]) = messages match {
        case x if x.isEmpty => ""
        case x => cssMessageClass(x.maxBy(_.importance))
      }
    }

    abstract class Button(ilk: String) extends super.Button(ilk) {
      override def html: NodeSeq =
        if (isHidden || isFloating) super.html
        else
          <div class="form-group">
            <div class="col-sm-offset-3 col-sm-9">{super.html}</div>
          </div>
    }

  }

}

trait Bootstrap3Form extends Form with BsContainer {
  def submitFallback = <input type="submit" class="concealed" tabindex="-1" name="fallback-default" value="" />

  def formHeader = if (formHeaderContent.isEmpty) formHeaderContent else <header class="form-header">{formHeaderContent}</header>

  def formHeaderContent: NodeSeq = formTitle ++ formDescription

  def formTitle = formTitleString match {case "" => NodeSeq.Empty case s => <h3>{s}</h3> }

  def formTitleString = t"form-title: #$name"

  def formDescription = formDescriptionString match {case "" => NodeSeq.Empty case s => Unparsed(s) }

  def formDescriptionString = t"form-description:"

  def modalHtml: NodeSeq =
    if (isIgnored) NodeSeq.Empty
    else
    <div id={modalId} class="modal fade" role="dialog" name={name + "-modal"} tabindex="-1">
      <div class="modal-dialog">
        {html}
      </div>
    </div>

  def inlineHtml: NodeSeq =
    if (isIgnored) NodeSeq.Empty
    else html

  override def html: NodeSeq =
    <form id={formId} name={name} class="twibs-form" action={actionLinkWithContextPath} method="post" enctype="multipart/form-data">
      {hidden(pnId, id) ++ hidden(pnModal, modal.toString) ++ hidden(ApplicationSettings.PN_NAME, RequestSettings.applicationSettings.name)}
      {if (modal) modalContent else inlineContent}
    </form>


  def modalContent: NodeSeq =
    <div class="modal-content">
      <div class="modal-header">
       <button type="button" class="close" data-dismiss="modal">×</button>
        {formHeaderContent}
      </div>
      <div class="modal-body">
        {super.html}
        {submitFallback}
      </div>
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
    </div> ++
      formHeader ++
      super.html ++
      submitFallback

}
