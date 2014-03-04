package twibs.form.base

import scala.xml.{Unparsed, Text, NodeSeq}
import twibs.util.{TranslationSupport, DisplayType}

trait ButtonRenderer extends DisplayType with TranslationSupport {
  def ilk: String

  def name: String

  def buttonTitleWithIconHtml: NodeSeq = prefixWithButtonIconHtml(buttonTitleHtml)

  def buttonIconOrButtonTitleIfEmptyHtml: NodeSeq = buttonIconHtml match {case NodeSeq.Empty => buttonTitleHtml case s => s }

  def prefixWithButtonIconHtml(t: NodeSeq): NodeSeq = buttonIconHtml match {case NodeSeq.Empty => t case ns => ns ++ Text(" ") ++ t }

  def buttonIconHtml: NodeSeq = buttonIconName match {case "" => NodeSeq.Empty case s => <span class={s"glyphicon glyphicon-$s"}></span> }

  def buttonTitleHtml = Unparsed(buttonTitle)

  def buttonTitle = t"button-title: #$ilk"

  def buttonIconName = t"button-icon:"

  def buttonCssClasses = "btn" :: "btn-" + displayTypeString :: Nil
}

object HiddenInputRenderer {
  def apply(name: String, value: String) = <input type="hidden" autocomplete="off" name={name} value={value} />
}
