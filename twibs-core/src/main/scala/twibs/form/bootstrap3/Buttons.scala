/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import scala.xml.{NodeSeq, Elem}
import twibs.form.base.{BaseChildItemWithName, Result, Executor, ButtonRenderer}
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

trait BootstrapButtonRenderer extends ButtonRenderer {
  def buttonAsElem =
    if (isEnabled)
      <button type="submit" value={buttonValueAsString}>{renderButtonTitle}</button>
    else
      <span>{renderButtonTitle}</span>

  def buttonValueAsString = ""
}

trait ButtonValue[T] extends BootstrapButtonRenderer {
  def buttonAsHtmlWithValue(buttonValue: T): NodeSeq = {
    val was = _buttonValue
    _buttonValue = Some(buttonValue)
    val ret = buttonAsHtml
    _buttonValue = was
    ret
  }

  def buttonValue = _buttonValue

  override def buttonValueAsString = buttonValue.fold("")(_.toString)

  private var _buttonValue: Option[T] = None
}

trait PopoverButtonRenderer extends BootstrapButtonRenderer with BaseChildItemWithName {
  self =>
  def usePopover = true

  override def buttonAsEnrichedElem: Elem = if (usePopover) openPopoverButton.buttonAsEnrichedElem else super.buttonAsEnrichedElem

  class OpenPopoverButton extends BootstrapButtonRenderer {
    override def isActive = self.isActive

    override def isVisible = self.isVisible

    override def isEnabled = self.isEnabled

    override def isDisabled = self.isDisabled

    override def name = openPopoverButtonName

    override def ilk = self.ilk

    override def translator = self.translator.usage("open-popover")

    override def displayTypeString = self.displayTypeString

    override def buttonAsElem: Elem =
      if (isEnabled)
        if (popoverNeedsCalculation)
          <button type="submit" value={self.buttonValueAsString}>{renderButtonTitle}</button>
        else
          <button type="button" data-container={popoverContainer} data-toggle="popover" data-html="true" data-placement={popoverPlacement} data-title={popoverTitle} data-content={popoverContent}>{renderButtonTitle}</button>
      else
        <span>{renderButtonTitle}</span>

    def openPopoverJs = jQuery(id).call("popover", popoverOptions).call("addClass", "popover-by-script").call("popover", "show")

    def popoverOptions = Map(
      "html" -> true,
      "title" -> popoverTitle,
      "content" -> popoverContent,
      "placement" -> popoverPlacement,
      "container" -> popoverContainer
    )
  }

  new Executor(openPopoverButtonName)(parent) with Result {
    override def execute(strings: Seq[String]): Unit = {



      result = AfterFormDisplay(openPopoverButton.openPopoverJs)
    }
  }

  def popoverNeedsCalculation = false

  def openPopoverButtonName = self.name + "-popover"

  def openPopoverButton = new OpenPopoverButton

  def popoverTitle = translator.translateOrUseDefault("popover-title", buttonTitle)

  def popoverContent: NodeSeq = enrichButtonElem(buttonAsElem)

  def popoverPlacement = "bottom"

  def popoverContainer = "body"
}
