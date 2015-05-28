/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form

import net.twibs.util.XmlUtils._

import scala.xml.{NodeSeq, Unparsed}

trait HorizontalLayoutContainer extends Container {
  override def containerCssClasses = "form-horizontal" +: super.containerCssClasses

  def labelColumns = 3

  def contentColumns = 9

  def ->>(nodeSeq: => NodeSeq) = new DisplayHtml(<div class="form-group"><div class={s"col-sm-offset-$labelColumns col-sm-$contentColumns"}>{nodeSeq}</div></div>)

  trait HorizontalControl extends Control {
    override def treeHtml: NodeSeq = <div class="form-group">{formGroupContent}</div>.addClass(required, "required")

    def formGroupContent = controlTitle match {
      case "" =>
        <div class={s"col-sm-offset-$labelColumns col-sm-$contentColumns"}>{super.treeHtml}</div>
      case ct =>
        <div class={s"col-sm-$labelColumns" :: labelMessageCssClass :: Nil}><label class={"control-label" :: Nil}>{ct}{infoIcon}</label></div> ++
        <div class={s"col-sm-$contentColumns"}>{super.treeHtml}</div>
    }

    def infoIcon = infoMessageOption match {
      case None => NodeSeq.Empty
      case Some(string) =>
        <span class="info-icon fa fa-info-circle"></span>
          .set("data-title", infoMessageTitle)
          .set("data-toggle", "popover")
          .set("data-content", string)
          .set("data-placement", "auto")
          .set("data-trigger", "hover focus")
          .set("data-container", form.formId.toCssId)
          .set("data-html", "true")
    }

    override def helpMessageHtml: NodeSeq = helpMessageOption.fold(NodeSeq.Empty)(m => <div class="help-block">{Unparsed(m)}</div>)
  }

  abstract class SingleLineField(ilk: String) extends super.SingleLineField(ilk) with HorizontalControl

  abstract class MultiLineField(ilk: String) extends super.MultiLineField(ilk) with HorizontalControl

  abstract class HtmlField(ilk: String) extends super.HtmlField(ilk) with HorizontalControl

  abstract class CheckboxField(ilk: String) extends super.CheckboxField(ilk) with HorizontalControl

  abstract class RadioField(ilk: String) extends super.RadioField(ilk) with HorizontalControl

  abstract class SingleSelectField(ilk: String) extends super.SingleSelectField(ilk) with HorizontalControl

  abstract class MultiSelectField(ilk: String) extends super.MultiSelectField(ilk) with HorizontalControl

  abstract class DateTimeField(ilk: String) extends super.DateTimeField(ilk) with HorizontalControl

  abstract class DateField(ilk: String) extends super.DateField(ilk) with HorizontalControl

  abstract class IntField(ilk: String) extends super.IntField(ilk) with HorizontalControl

  abstract class LongField(ilk: String) extends super.LongField(ilk) with HorizontalControl

  abstract class DoubleField(ilk: String) extends super.DoubleField(ilk) with HorizontalControl

  abstract class PercentField(ilk: String) extends super.PercentField(ilk) with HorizontalControl

  abstract class Button(ilk: String) extends super.Button(ilk) with HorizontalControl

  abstract class ButtonRow extends super.ButtonRow {
    override def treeHtml = <div class="form-group"><div class={s"col-sm-offset-$labelColumns col-sm-$contentColumns"}>{super.treeHtml}</div></div>
  }
}

trait HorizontalForm extends Form with HorizontalLayoutContainer {
  override def formCssClasses: Seq[String] = "form-horizontal" +: super.formCssClasses
}
