/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import scala.xml.{Elem, Unparsed, Text, NodeSeq}
import twibs.util.{IdString, TranslationSupport, DisplayType}
import twibs.util.XmlUtils._
import twibs.web.Request

trait ButtonRenderer extends DisplayType with TranslationSupport {
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

  def buttonValueAsString: String = ""

  def enrichButtonElem(elem: Elem) : Elem =
    elem
      .setIfMissing("name", name)
      .setIfMissing("id", id.string)
      .addClass(isActive, "active")
      .addClasses(buttonCssClasses)
      .addClass(isDisabled, "disabled")
      .addClass(!isDisabled, "can-be-disabled")
      .setIfMissing(buttonUseIconOnly, "title", buttonTitle)

  def buttonAsElem: Elem

  def isActive = false

  def isInactive = !isActive

  def isVisible: Boolean

  def isDisabled: Boolean

  def ilk: String

  def name: String

  def id: IdString
}

trait BaseButton extends Component with ButtonRenderer

trait ButtonValues extends BaseButton with Values {
  def buttonAsHtmlWithValue(buttonValue: ValueType): NodeSeq = {
    val was = values
    values = buttonValue :: Nil
    val ret = buttonAsHtml
    values = was
    ret
  }

  override def buttonValueAsString = strings.headOption getOrElse ""

  def value = values.head

  override def parse(request: Request): Unit = request.parameters.getStringsOption(name).foreach(parse)

  def parse(parameters: Seq[String]): Unit = strings = parameters
}

