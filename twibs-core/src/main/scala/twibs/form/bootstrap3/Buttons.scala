/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import scala.xml.{NodeSeq, Elem}
import twibs.form.base._
import twibs.util.JavaScript._
import twibs.util.{Translator, PrimaryDisplayType}
import twibs.web.{Upload, Request}

trait UploadButton extends Button with PrimaryDisplayType {
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
  override def execute(strings: Seq[String]): Unit = Unit

  def uploaded(uploads: List[Upload]): Unit

  def submitOnChange: Boolean = false

  override def translator: Translator = super.translator.kind("UPLOAD-BUTTON")
}

trait BootstrapButton extends BaseButton {
  def buttonAsElem =
    if (isEnabled)
      <button type="submit" value={buttonValueAsString}>{renderButtonTitle}</button>
    else
      <span>{renderButtonTitle}</span>
}

trait BootstrapPopoverButton extends BootstrapButton with ButtonValues {
  self =>
  def usePopover = true

  override def buttonAsEnrichedElem: Elem = if (usePopover) openPopoverButton.buttonAsEnrichedElem else super.buttonAsEnrichedElem

  class OpenPopoverButton extends BootstrapButton with Executable with Result {
    override def parent = self.parent

    override def isActive = self.isActive

    override def itemIsVisible = self.itemIsVisible

    override def itemIsRevealed = self.itemIsRevealed

    override def itemIsEnabled = self.itemIsEnabled

    override def ilk = self.ilk + "-popover"

    override def translator = self.translator.usage("open-popover")

    override def displayTypeString = self.displayTypeString

    override def buttonValueAsString: String = self.buttonValueAsString

    override def buttonAsElem: Elem =
      if (isEnabled)
        if (popoverNeedsCalculation)
          <button type="submit" value={self.buttonValueAsString}>{renderButtonTitle}</button>
        else
          <button type="button" data-container={popoverContainer} data-toggle="popover" data-html="true" data-placement={popoverPlacement} data-title={popoverTitle} data-content={popoverContent}>{renderButtonTitle}</button>
      else
        <span>{renderButtonTitle}</span>

    override def execute(strings: Seq[String]): Unit = {
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

  def popoverContainer = parent.form.contentId.toCssId
}
