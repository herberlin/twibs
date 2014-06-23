/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

import twibs.util.{ApplicationSettings, Parameters}

import com.ibm.icu.util.ULocale

class StaticRequest(val path: String,
                    val domain: String = "localhost",
                    val parameters: Parameters = Parameters(Map()),
                    val useCache: Boolean = true) extends Request with StaticAttributeContainer {
  val timestamp = Request.now()

  def method: RequestMethod = GetMethod

  def accept: List[String] = Nil

  def remoteAddress: String = "::1"

  def remoteHost: String = "localhost"

  def userAgent: String = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/34.0.1847.116 Chrome/34.0.1847.116 Safari/537.36"

  def referrerOption: Option[String] = None

  def userAgentOption: Option[String] = None

  def remoteUserOption: Option[String] = None

  def doesClientSupportGzipEncoding: Boolean = false

  def uri: String = path

  def uploads = Map[String, Seq[Upload]]()

  val session = new StaticSession

  val desiredLocale: ULocale = ApplicationSettings.locales.head

  def getCookieValue(cookieName: String) = None
}
