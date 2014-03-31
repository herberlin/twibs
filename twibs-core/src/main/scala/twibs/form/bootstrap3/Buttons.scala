package twibs.form.bootstrap3

import scala.xml.{NodeSeq, Elem}
import twibs.form.base.ButtonRenderer
import twibs.util.{Translator, PrimaryDisplayType}
import twibs.web.{Upload, Request}

//trait IconButton extends Button {
//  override def inputElem(input: InputValue) = <button type="submit" value={input.string} title={buttonTitle}>{buttonIconOrButtonTitleIfEmptyHtml}</button>
//}
//

//trait ButtonWithInputOptions extends Button with OneInputElementPerOption {
//  override def inputElem(input: InputValue): Elem = <button class={buttonCssClasses} type="submit" value={input.string}>{prefixWithButtonIconHtml(input.title)}</button>
//
//  override def titleForValue(value: ValueType): String = buttonTitle
//}

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
  def buttonAsHtml(buttonValue: String): NodeSeq = {
    _buttonValue = buttonValue
    buttonAsHtml
  }

  def buttonAsHtml: NodeSeq = buttonAsEnrichedElem

  def buttonAsEnrichedElem: Elem = enrichButtonElem(buttonAsElem)

  def enrichButtonElem(elem: Elem) =
    elem
      .add("name", name)
      .addClasses(buttonCssClasses)

  def buttonAsElem = <button type="submit" value={buttonValue}>{buttonTitleWithIconHtml}</button>

  def buttonValue = _buttonValue

  private var _buttonValue: String = ""
}

trait PopoverButtonRenderer extends BootstrapButtonRenderer {
  override def buttonAsEnrichedElem: Elem = <button type="button" class={openPopoverButtonCssClasses} data-container={popoverContainer} data-toggle="popover" data-html="true" data-placement={popoverPlacement} data-title={openPopoverTitle} data-content={popoverContent}>{openPopoverButtonTitleWithIconHtml}</button>

  def openPopoverButtonTitleWithIconHtml = buttonTitleWithIconHtml

  def openPopoverButtonCssClasses = buttonCssClasses

  def openPopoverTitle = translator.translateOrUseDefault("popover-title", buttonTitle)

  def popoverContent = enrichButtonElem(buttonAsElem)

  def popoverPlacement = "bottom"

  def popoverContainer = "body"
}
