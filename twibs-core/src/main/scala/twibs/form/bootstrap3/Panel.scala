package twibs.form.bootstrap3

import scala.xml.NodeSeq
import twibs.form.base._
import twibs.util.Translator

trait Panel extends ItemContainer {
  override def prefixForChildNames = name + "-"

  override def translator: Translator = super.translator.kind("PANEL")

  def panelCssClasses: List[String] = "panel" :: "panel-default" :: Nil

  override def html: NodeSeq =
    <div class={panelCssClasses}>
      {panelHeading}
      <div class="panel-body">
        {super.html}
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

class ButtonFormGroup(implicit _parent: BaseParentItem) extends ItemContainer("button-group")(_parent) {
  override def html =
    <div class="form-group">
      <div class="col-sm-offset-3 col-sm-9">
        {super.html}
      </div>
    </div>
}

trait BoostratpMinMaxChildren extends MinMaxChildren {
  override def messageHtml: NodeSeq = messageOption match {
    case Some(message) => Bootstrap.withUntitledFormGroup(form.renderer.renderMessage(message))
    case _ => NodeSeq.Empty
  }
}

trait Detachable extends BaseItemContainer {
  override def html: NodeSeq =
    <div class={ilk :: "detachable" :: Nil}>
      {closeButton}
      <div class="detachable-content">
        {super.html}
      </div>
    </div>

  def closeButton =
    if (isDisabled) NodeSeq.Empty
    else <button type="button" class="close" data-toggle="popover" data-html="true" data-placement="auto left" data-title={t"delete-item.popover-title: Delete item?"} data-content={dismissButton}>&times;</button>

  def dismissButton = <button type="button" class="btn btn-danger" data-dismiss="detachable">{t"delete-item.button-title: Delete"}</button>
}

trait UploadWithComment extends Dynamic with Detachable {
  val upload = new Field("file") with UploadValues with ReadOnlyField

  val comment = new Field("file-comment") with StringValues with MultiLineField with Required

  override def translator = super.translator.kind("UPLOAD-WITH-COMMENT")
}
