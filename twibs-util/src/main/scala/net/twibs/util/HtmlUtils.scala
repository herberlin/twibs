/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.nio.charset.StandardCharsets

import net.htmlparser.jericho._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document.OutputSettings
import org.jsoup.nodes.Document.OutputSettings.Syntax
import org.jsoup.nodes.Entities.EscapeMode
import org.jsoup.safety.Whitelist

object HtmlUtils {
  def convertEmailHtmlToPlain(html: String): String =
    new Source(html).getRenderer.setNewLine("\n").toString

  def convertHtmlToPlain(html: String): String =
    new Source(html).getRenderer.setNewLine("\n").setIncludeHyperlinkURLs(false).toString

  private val wl = Whitelist.basicWithImages().addAttributes(":all", "class")
  private val os = new OutputSettings().charset(StandardCharsets.UTF_8).escapeMode(EscapeMode.xhtml).prettyPrint(false).syntax(Syntax.xml)

  def cleanup(html: String) = Jsoup.clean(html, "", wl, os)

  // Disable logging for jericho
  Config.LoggerProvider = LoggerProvider.DISABLED
}
