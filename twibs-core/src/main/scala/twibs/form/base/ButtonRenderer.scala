/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import scala.xml.{Elem, Unparsed, Text, NodeSeq}
import twibs.util.{TranslationSupport, DisplayType}
import twibs.util.XmlUtils._

trait ButtonRenderer extends DisplayType with TranslationSupport {
  def ilk: String

  def name: String

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
      .add("name", name)
      .addClass(isActive, "active")
      .addClasses(buttonCssClasses)
      .addClass(isDisabled, "disabled")
      .set(buttonUseIconOnly, "title", buttonTitle)

  def buttonAsElem: Elem

  def isVisible: Boolean

  def isActive = false

  def isInactive = !isActive

  def isEnabled: Boolean

  def isDisabled: Boolean
}

object HiddenInputRenderer {
  def apply(name: String, value: String) = <input type="hidden" autocomplete="off" name={name} value={value} />
}
