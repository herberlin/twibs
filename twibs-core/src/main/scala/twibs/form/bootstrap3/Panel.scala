/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import scala.xml.NodeSeq
import twibs.form.base._
import twibs.util.Translator

trait Panel extends StaticContainer {
  override def prefixForChildNames = name + "-"

  override def translator: Translator = super.translator.kind("PANEL")

  def panelCssClasses: List[String] = "panel" :: "panel-default" :: Nil

  override def containerAsDecoratedHtml: NodeSeq =
    <div class={panelCssClasses}>
      {panelHeading}
      <div class="panel-body">
        {super.containerAsDecoratedHtml}
      </div>
    </div>

  def panelHeading = t"panel-title: #$name" match {
    case "" => NodeSeq.Empty
    case title =>
      <div class="panel-heading">
         <h4>{title}</h4>
      </div>
  }
}

class ButtonFormGroup(implicit parent: Container) extends StaticContainer("button-group")(parent) {
  override def containerAsDecoratedHtml =
    <div class="form-group">
      <div class="col-sm-offset-3 col-sm-9">
        {super.containerAsDecoratedHtml}
      </div>
    </div>
}

trait BoostrapMinMaxContainer extends MinMaxContainer {
  override def messageHtml: NodeSeq = messageOption match {
    case Some(message) => Bootstrap.withUntitledFormGroup(form.renderer.renderMessage(message))
    case _ => NodeSeq.Empty
  }
}

trait Detachable extends Container {
  override def containerAsDecoratedHtml =
    <div class={ilk :: "detachable" :: Nil}>
      {closeButton}
      <div class="detachable-content">
        {super.containerAsDecoratedHtml}
      </div>
    </div>

  def closeButton =
    if (state.isEnabled) <button type="button" class="close" data-toggle="popover" data-html="true" data-placement="auto left" data-title={t"delete-component.popover-title: Delete component?"} data-content={dismissButton}>&times;</button>
    else NodeSeq.Empty

  def dismissButton = <button type="button" class="btn btn-danger" data-dismiss="detachable">{t"delete-component.button-title: Delete"}</button>
}

trait UploadWithComment extends Dynamic with Detachable {
  val upload = new Field("file") with UploadValues with ReadOnlyField {
    override def inputAsElem(input: Input) = <span></span>
  }

  val comment = new Field("file-comment") with StringValues with MultiLineField with Required

  override def translator = super.translator.kind("UPLOAD-WITH-COMMENT")

  override def state: ComponentState = super.state.ignoreIf(upload.isModified && upload.values.isEmpty)
}
