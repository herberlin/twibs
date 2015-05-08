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

  /* Fields */

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
        val title = infoMessageOption.fold("")(_ => infoMessageTitle)
        <span class="info-icon fa fa-info-circle"></span>
          .set(!title.isEmpty, "data-title", title)
          .set("data-toggle", "popover")
          .set("data-content", string)
          .set("data-placement", "auto")
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

//  trait Bs3HlField extends FormControlField with Bs3HlControl {
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
  //  }

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

  trait SingleLineFieldTrait extends super.SingleLineFieldTrait with HorizontalControl

  trait MultiLineFieldTrait extends super.MultiLineFieldTrait with HorizontalControl

  trait HtmlFieldTrait extends super.HtmlFieldTrait with HorizontalControl

  trait CheckboxFieldTrait extends super.CheckboxFieldTrait with HorizontalControl {
    override def optionHtmlFor(option: Entry): NodeSeq =
      <div class="checkbox">
        <label>
          {super.optionHtmlFor(option)}
          {option.title}
        </label>
      </div>.addClass(isDisabled, "disabled")
  }

  trait RadioFieldTrait extends super.RadioFieldTrait with HorizontalControl {
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

  trait SingleSelectFieldTrait extends super.SingleSelectFieldTrait with HorizontalControl

  trait MultiSelectFieldTrait extends super.MultiSelectFieldTrait with HorizontalControl

  trait ButtonTrait extends super.ButtonTrait with HorizontalControl

  trait ButtonRowTrait extends super.ButtonRowTrait {
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

trait HorizontalForm extends Form with HorizontalLayoutContainer {
  override def formCssClasses: Seq[String] = "form-horizontal" +: super.formCssClasses
}
