/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.form.bootstrap3

import scala.xml.{Elem, NodeSeq, Text, Unparsed}

import net.twibs.form.base._
import net.twibs.util.JavaScript._
import net.twibs.util.{DisplayType, PrimaryDisplayType, Translator}
import net.twibs.web.{Request, Upload}

trait UploadButton extends Button with StringValues with PrimaryDisplayType {
  override def buttonAsEnrichedElem =
    <span class={spanCssClasses}>
      {buttonTitleWithIconHtml}
      {super.buttonAsEnrichedElem.addClass(submitOnChange, "submit-on-change")}
    </span>.addClass(if (state.isDisabled) "disabled" else "can-be-disabled-by-class")

  def spanCssClasses = super.buttonCssClasses ::: "file-upload-button" :: "inherit-focus-from-child" :: Nil

  override def buttonCssClasses = "show-focus-on-parent" :: super.buttonCssClasses

  // Use auto complete because firefox fills in previously uploaded files.
  override def buttonAsElem: Elem = <input type="file" multiple="multiple" autocomplete="off" />

  override def parse(request: Request) = Request.uploads.get(name).map(uploads => uploaded(uploads.toList))

  // TODO: Convert UploadButton to input without appropriate Renderer
  override def execute(): Unit = Unit

  def uploaded(uploads: List[Upload]): Unit

  def submitOnChange: Boolean = false

  override def translator: Translator = super.translator.kind("UPLOAD-BUTTON")
}

trait BootstrapButton extends InteractiveComponent with Values with DisplayType {
  def renderButtonTitle = if (buttonUseIconOnly) buttonIconOrButtonTitleIfEmptyHtml else buttonTitleWithIconHtml

  def buttonUseIconOnly = false

  def buttonIconBefore = true

  def buttonTitleWithIconHtml: NodeSeq = buttonIconHtml match {case NodeSeq.Empty => buttonTitleHtml case ns => if (buttonIconBefore) ns ++ Text(" ") ++ buttonTitleHtml else buttonTitleHtml ++ Text(" ") ++ ns }

  def buttonIconOrButtonTitleIfEmptyHtml: NodeSeq = buttonIconHtml match {case NodeSeq.Empty => buttonTitleHtml case s => s }

  def buttonIconHtml: NodeSeq = buttonIconName match {
    case "" => NodeSeq.Empty
    case s if s.startsWith("fa-") => <span class={s"fa $s"}></span>
    case s if s.startsWith("glyphicon-") => <span class={s"glyphicon $s"}></span>
    case s => <span class={s"glyphicon glyphicon-$s"}></span>
  }

  def buttonTitleHtml: NodeSeq = Unparsed(buttonTitle)

  def buttonTitle = t"button-title: #$ilk"

  def buttonIconName = t"button-icon:"

  def buttonCssClasses = "btn" :: "btn-" + displayTypeString :: Nil

  def buttonAsHtml: NodeSeq = if (state.isHidden) NodeSeq.Empty else buttonAsEnrichedElem

  def buttonAsEnrichedElem: Elem = enrichButtonElem(buttonAsElem)

  def enrichButtonElem(elem: Elem): Elem =
    elem
      .setIfMissing("name", name)
      .setIfMissing("id", id.string)
      .addClass(isActive, "active")
      .addClasses(buttonCssClasses)
      .addClass(state.isDisabled, "disabled")
      .addClass(state.isEnabled, "can-be-disabled")
      .setIfMissing(state.isDisabled, "disabled", "disabled")
      .setIfMissing(name != ilk, "data-ilk", ilk)
      .setIfMissing(buttonUseIconOnly, "title", buttonTitle)

  def isActive = false

  def isInactive = !isActive

  def buttonAsElem =
    if (state.isEnabled)
      <button type="submit" value={stringOrEmpty}>{renderButtonTitle}</button>
    else
      <span>{renderButtonTitle}</span>

  override def asHtml: NodeSeq =
    if (state.isIgnored) NodeSeq.Empty
    else if (state.isHidden) inputs.map(input => form.renderer.hiddenInput(name, input.string)).flatten
    else buttonAsDecoratedHtml

  def buttonAsDecoratedHtml: NodeSeq =
    <div class={formGroupCssClasses}>
      <div class={controlContainerCssClasses}>{buttonAsHtml}</div>
    </div>

  def formGroupCssClasses = "form-group" :: Nil

  def controlContainerCssClasses = "col-sm-offset-3" :: "col-sm-9" :: "controls" :: Nil

  override def translator: Translator = super.translator.kind("BUTTON")
}

trait Spinner extends BootstrapButton {
  override def buttonCssClasses: List[String] = "has-spinner" :: super.buttonCssClasses

  override def buttonTitleHtml = { <span class="fa fa-refresh spinner"></span> ++ super.buttonTitleHtml }
}

trait ClickOnAppear extends BootstrapButton {
  override def buttonCssClasses: List[String] = "click-on-appear" :: super.buttonCssClasses
}

trait ButtonWithPopover extends BootstrapButton {
  self =>
  def usePopover = true

  override def buttonAsEnrichedElem: Elem = if (usePopover) openPopoverButton.buttonAsEnrichedElem else super.buttonAsEnrichedElem

  class OpenPopoverButton extends BootstrapButton with Executable with StringValues with Result with Floating {
    override def parent = self.parent

    override def isActive = self.isActive

    override def state = self.state

    override def ilk = self.ilk + "-popover"

    override def translator = self.translator.usage("open-popover")

    override def displayTypeString = self.displayTypeString

    override def buttonUseIconOnly = buttonUseIconOnly2

    override def buttonAsElem: Elem =
      if (state.isEnabled)
        if (popoverNeedsCalculation)
          <button type="submit" value={self.stringOrEmpty}>{renderButtonTitle}</button>
        else
          <button type="button" data-container={popoverContainer} data-toggle="popover" data-html="true" data-placement={popoverPlacement} data-title={popoverTitle} data-content={popoverContent}>{renderButtonTitle}</button>
      else
        <span>{renderButtonTitle}</span>

    override def parse(parameters: Seq[String]): Unit = {
      super.parse(parameters)
      self.strings = strings
    }

    override def execute(): Unit = result = AfterFormDisplay(openPopoverJs)

    def openPopoverJs = popoverElementSelector.call("popover", popoverOptions).call("addClass", "popover-by-script").call("popover", "show")
  }

  def popoverElementSelector = jQuery(id)

  override final def buttonUseIconOnly = if (usePopover) false else buttonUseIconOnly2

  def buttonUseIconOnly2: Boolean = super.buttonUseIconOnly

  final val openPopoverButton = computeOpenPopoverButton

  def popoverNeedsCalculation = false

  def popoverOptions = Map(
    "html" -> true,
    "title" -> popoverTitle,
    "content" -> popoverContent,
    "placement" -> popoverPlacement,
    "container" -> popoverContainer
  )

  def computeOpenPopoverButton = new OpenPopoverButton

  def popoverTitle = translator.translateOrUseDefault("popover-title", buttonTitle)

  def popoverContentText = t"popover-text:"

  def popoverContent: NodeSeq = Unparsed(popoverContentText) ++ enrichButtonElem(buttonAsElem)

  def popoverPlacement = "bottom"

  def popoverContainer = form.contentId.toCssId
}
