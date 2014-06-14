/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import scala.xml.{Unparsed, Text, NodeSeq, Elem}
import twibs.form.base._
import twibs.util.JavaScript._
import twibs.util.{DisplayType, IdString, Translator, PrimaryDisplayType}
import twibs.web.{Upload, Request}

trait UploadButton extends Button with StringValues with PrimaryDisplayType {
  override def buttonAsEnrichedElem =
    <span class={spanCssClasses}>
      {buttonTitleWithIconHtml}
      {super.buttonAsEnrichedElem.addClass(submitOnChange, "submit-on-change")}
    </span>.addClass(if (isDisabled) "disabled" else "can-be-disabled-by-class")

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

  def buttonTitleWithIconHtml: NodeSeq = buttonIconHtml match {case NodeSeq.Empty => buttonTitleHtml case ns => ns ++ Text(" ") ++ buttonTitleHtml }

  def buttonIconOrButtonTitleIfEmptyHtml: NodeSeq = buttonIconHtml match {case NodeSeq.Empty => buttonTitleHtml case s => s }

  def buttonIconHtml: NodeSeq = buttonIconName match {case "" => NodeSeq.Empty case s => <span class={s"glyphicon glyphicon-$s"}></span> }

  def buttonTitleHtml = Unparsed(buttonTitle)

  def buttonTitle = t"button-title: #$ilk"

  def buttonIconName = t"button-icon:"

  def buttonCssClasses = "btn" :: "btn-" + displayTypeString :: Nil

  def buttonAsHtml: NodeSeq = if (isVisible) buttonAsEnrichedElem else NodeSeq.Empty

  def buttonAsEnrichedElem: Elem = enrichButtonElem(buttonAsElem)

  def enrichButtonElem(elem: Elem) : Elem =
    elem
      .setIfMissing("name", name)
      .setIfMissing("id", id.string)
      .addClass(isActive, "active")
      .addClasses(buttonCssClasses)
      .addClass(isDisabled, "disabled")
      .addClass(!isDisabled, "can-be-disabled")
      .setIfMissing(isDisabled, "disabled", "disabled")
      .setIfMissing(name != ilk, "data-ilk", ilk)
      .setIfMissing(buttonUseIconOnly, "title", buttonTitle)

  def isActive = false

  def isInactive = !isActive

  def buttonAsElem =
    if (isEnabled)
      <button type="submit" value={stringOrEmpty}>{renderButtonTitle}</button>
    else
      <span>{renderButtonTitle}</span>

  override def html: NodeSeq =
    <div class={formGroupCssClasses}>
      <div class={controlContainerCssClasses}>{buttonAsHtml}</div>
    </div>

  def formGroupCssClasses = "form-group" :: Nil

  def controlContainerCssClasses = "col-sm-offset-3" :: "col-sm-9" :: "controls" :: Nil

  override def translator: Translator = super.translator.kind("BUTTON")
}

trait ButtonWithPopover extends BootstrapButton {
  self =>
  def usePopover = true

  override def buttonAsEnrichedElem: Elem = if (usePopover) openPopoverButton.buttonAsEnrichedElem else super.buttonAsEnrichedElem

  class OpenPopoverButton extends BootstrapButton with Executable with StringValues with Result {
    override def parent = self.parent

    override def isActive = self.isActive

    override def selfIsVisible = self.selfIsVisible

    override def selfIsRevealed = self.selfIsRevealed

    override def selfIsEnabled = self.selfIsEnabled

    override def ilk = self.ilk + "-popover"

    override def translator = self.translator.usage("open-popover")

    override def displayTypeString = self.displayTypeString

    override def stringOrEmpty: String = self.stringOrEmpty

    override def buttonAsElem: Elem =
      if (isEnabled)
        if (popoverNeedsCalculation)
          <button type="submit" value={self.stringOrEmpty}>{renderButtonTitle}</button>
        else
          <button type="button" data-container={popoverContainer} data-toggle="popover" data-html="true" data-placement={popoverPlacement} data-title={popoverTitle} data-content={popoverContent}>{renderButtonTitle}</button>
      else
        <span>{renderButtonTitle}</span>

    override def execute(): Unit = {
      self.strings = strings
      result = AfterFormDisplay(openPopoverJs)
    }

    def openPopoverJs = jQuery(id).call("popover", popoverOptions).call("addClass", "popover-by-script").call("popover", "show")
  }

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

  def popoverContent: NodeSeq = enrichButtonElem(buttonAsElem)

  def popoverPlacement = "bottom"

  def popoverContainer = form.contentId.toCssId
}
