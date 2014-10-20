/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import org.owasp.html.HtmlPolicyBuilder

object HtmlUtils {
  private val NOTHING_GOES = new HtmlPolicyBuilder().allowElements("p", "br").toFactory
  private val ANYTHING_GOES =
    new HtmlPolicyBuilder()
      .allowAttributes("class").globally()
      .allowCommonInlineFormattingElements()
      .allowCommonBlockElements()
      .allowWithoutAttributes("span")
      .toFactory
  //  private val ANYTHING_GOES = new HtmlPolicyBuilder().allowCommonInlineFormattingElements().toFactory

  def removeAllTagsExceptPandBR(html: String): String = NOTHING_GOES.sanitize(html)

  def convertHtmlToPlain(html: String): String =
    removeAllTagsExceptPandBR(html)
      .replaceAll("\\s*<br ?/?>\\s*", "\n")
      .replaceAll("</p>\\s*<p>", "\n\n")
      .replaceAll("</?p>", "").trim

  def cleanup(html: String) = ANYTHING_GOES.sanitize(html)
}
