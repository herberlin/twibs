/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import org.owasp.html.HtmlPolicyBuilder
import net.htmlparser.jericho._

object HtmlUtils {
  private val NOTHING_GOES =
    new HtmlPolicyBuilder()
      .allowElements("p", "br", "a")
      .toFactory

  private val EMAIL_POLICY_FACTORY =
    new HtmlPolicyBuilder()
      .allowElements("p", "br", "a")
      .allowAttributes("href").onElements("a")
      .allowStandardUrlProtocols()
      .toFactory

  private val ANYTHING_GOES =
    new HtmlPolicyBuilder()
      .allowAttributes("class").globally()
      .allowCommonInlineFormattingElements()
      .allowCommonBlockElements()
      .allowWithoutAttributes("span")
      .toFactory
  //  private val ANYTHING_GOES = new HtmlPolicyBuilder().allowCommonInlineFormattingElements().toFactory

  def convertEmailHtmlToPlain(html: String): String =
    new Source(html).getRenderer.setNewLine("\n").toString

  def convertHtmlToPlain(html: String): String =
    new Source(html).getRenderer.setNewLine("\n").setIncludeHyperlinkURLs(false).toString

  def cleanup(html: String) = ANYTHING_GOES.sanitize(html)

  Config.LoggerProvider = LoggerProvider.DISABLED
}
