/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import com.google.common.net.MediaType
import net.twibs.util.ApplicationSettings
import net.twibs.util.Predef._

trait DetectedMimeType {
  self: Response =>

  lazy val mimeType = asInputStream useAndClose {
    is => ApplicationSettings.tika.detect(is, "")
  }
}

trait JsonMimeType {
  def mimeType: String = MimeTypes.JSON
}

trait PdfMimeType {
  def mimeType: String = MimeTypes.PDF
}

trait CssMimeType {
  def mimeType: String = MimeTypes.CSS
}

trait HtmlMimeType {
  def mimeType: String = MimeTypes.HTML
}

trait JavaScriptMimeType {
  def mimeType: String = MimeTypes.JAVASCRIPT
}

trait ExcelMimeType {
  def mimeType: String = MimeTypes.MICROSOFT_EXCEL
}

trait TextMimeType {
  def mimeType: String = MimeTypes.TEXT
}

trait XmlMimeType {
  def mimeType: String = MimeTypes.XML
}

trait OctetStreamMimeType {
  def mimeType: String = MimeTypes.OCTECT_STREAM
}

object MimeTypes {
  val JSON: String = MediaType.JSON_UTF_8.withoutParameters.toString
  val PDF: String = MediaType.PDF.toString
  val CSS: String = MediaType.CSS_UTF_8.withoutParameters.toString
  val HTML: String = MediaType.HTML_UTF_8.withoutParameters.toString
  val TEXT: String = MediaType.PLAIN_TEXT_UTF_8.withoutParameters.toString
  val XML: String = MediaType.XML_UTF_8.withoutParameters.toString
  val OCTECT_STREAM: String = MediaType.OCTET_STREAM.toString
  val JAVASCRIPT: String = MediaType.JAVASCRIPT_UTF_8.withoutParameters.toString
  val MICROSOFT_EXCEL = MediaType.MICROSOFT_EXCEL.withoutParameters.toString
}
