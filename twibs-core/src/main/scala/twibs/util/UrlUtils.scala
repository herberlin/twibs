/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import com.google.common.base.Charsets
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.regex.Pattern

object UrlUtils {
  final val webAddressRegex: String = "^(http|https|ftp)\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~])*[^\\.\\,\\)\\(\\s]$"

  final val webAddressRegexPattern: Pattern = Pattern.compile(webAddressRegex, Pattern.CASE_INSENSITIVE)

  def isValidWebAddress(emailAddress: String) = emailAddress != null && webAddressRegexPattern.matcher(emailAddress).matches()

  def encodeUrl(string: String) = URLEncoder.encode(string, Charsets.UTF_8.name())

  def decodeUrl(string: String) = URLDecoder.decode(string, Charsets.UTF_8.name())

  def isValidUrlPath(possiblePathString: String): Boolean = {
    val x = possiblePathString.replace("/", "")
    encodeUrl(decodeUrl(x)) == x
  }

  def createUrlWithParameters(path: String, parameters: Map[String, Seq[String]] = Map()) =
    createUrlWithParametersAndFragment(path, parameters)

  def createUrlWithParametersAndFragment(path: String, parameters: Map[String, Seq[String]] = Map(), fragment: String = "") =
    appendFragmentToUrl(appendParametersStringToUrl(createValidUrl(path), createParametersString(parameters)), fragment)

  def createValidUrl(path: String) = if (path.isEmpty) "/" else path

  def parametersAsList(parameters: Map[String, Seq[String]]) =
    parameters.toList.flatMap(entry => entry._2.map(value => (encodeUrl(entry._1), encodeUrl(value))))

  def createParametersString(parameters: Map[String, Seq[String]]) =
    parametersAsList(parameters).map(entry => entry._1 + "=" + entry._2).mkString("&")

  def appendParametersStringToUrl(url: String, parametersString: String) =
    if (parametersString.isEmpty) url
    else url + (if (url.contains('?')) '&' else '?') + parametersString

  def appendFragmentToUrl(url: String, fragment: String) =
    if (fragment.isEmpty) url else url + "#" + fragment
}
