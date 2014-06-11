/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import scala.xml.{NodeSeq, Elem}
import twibs.form.base._
import twibs.util.JavaScript._
import twibs.util.{Translator, PrimaryDisplayType}
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

trait BootstrapButton extends InteractiveComponent with ButtonRenderer with Values {
  def buttonAsElem =
    if (isEnabled)
      <button type="submit" value={stringOrEmpty}>{renderButtonTitle}</button>
    else
      <span>{renderButtonTitle}</span>
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
