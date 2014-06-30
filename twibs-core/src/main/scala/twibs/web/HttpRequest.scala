/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

import java.beans.Transient
import javax.servlet.http.{Cookie, HttpServletRequest, HttpServletResponse}

import scala.collection.JavaConverters._

import twibs.util._

import com.ibm.icu.util.ULocale
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.{FileItem, FileItemFactory}
import org.apache.commons.io.FileUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.request.RequestParameter

private[web] abstract class HttpRequest(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) extends Request {
  val timestamp = Request.now()

  val method: RequestMethod = httpServletRequest.getMethod match {
    case "GET" => GetMethod
    case "POST" => PostMethod
    case "PUT" => PutMethod
    case "DELETE" => DeleteMethod
    case _ => UnknownMethod
  }

  val domain = httpServletRequest.getServerName

  val path = httpServletRequest.getServletPath + (Option(httpServletRequest.getPathInfo) getOrElse "")

  val doesClientSupportGzipEncoding = Option(httpServletRequest.getHeader("Accept-Encoding")).exists(_ contains "gzip")

  val accept: List[String] = Option(httpServletRequest.getHeader("Accept")).map(_.split(",").toList) getOrElse Nil

  val referrerOption = Option(httpServletRequest.getHeader("Referer"))

  val userAgentOption = Option(httpServletRequest.getHeader("User-Agent"))

  val remoteUserOption = Option(httpServletRequest.getRemoteUser)

  val remoteAddress = httpServletRequest.getRemoteAddr

  val remoteHost = httpServletRequest.getRemoteHost

  val userAgent = httpServletRequest.getHeader("User-Agent")

  val uri = httpServletRequest.getRequestURI

  val desiredLocale = ULocale.forLocale(httpServletRequest.getLocale)

  val parameters: Parameters = Parameters(removeUnderscoreParameterSetByJQuery(urlParameters ++ multiPartParameters))

  private def removeUnderscoreParameterSetByJQuery(map: Map[String, Seq[String]]): Map[String, Seq[String]] = map.filterNot(_._1 == "_")

  def urlParameters: Map[String, Seq[String]] = httpServletRequest.getParameterMap.asScala.map(entry => (entry._1, entry._2.toSeq)).toMap

  @Transient
  val session = new HttpSession(httpServletRequest)

  def setAttribute(name: String, value: Any): Unit =
    httpServletRequest.setAttribute(name, value)

  def getAttribute(name: String): Option[Any] = Option(httpServletRequest.getAttribute(name))

  def removeAttribute(name: String): Unit =
    httpServletRequest.removeAttribute(name)

  val useCache = !((RunMode.isDevelopment || RunMode.isTest) && httpServletRequest.getHeader("Cache-Control") == "no-cache" && httpServletRequest.getHeader("If-Modified-Since") == null)

  def multiPartParameters: Map[String, Seq[String]]

  def getCookie(name: String) = httpServletRequest.getCookies.find(_.getName.equalsIgnoreCase(name)).map(_.getValue)

  def removeCookie(name: String) = {
    val cookie: Cookie = new Cookie(name, "empty")
    cookie.setMaxAge(0)
    cookie.setPath("/")
    httpServletResponse.addCookie(cookie)
  }

  def setCookie(name: String, value: String) = {
    val cookie: Cookie = new Cookie(name, value)
    cookie.setMaxAge(5 * 365 * 24 * 60 * 60)
    cookie.setPath("/")
    httpServletResponse.addCookie(cookie)
  }
}

class HttpRequestWithCommonsFileUpload(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) extends HttpRequest(httpServletRequest, httpServletResponse) {
  def multiPartParameters: Map[String, Seq[String]] =
    if (isMultiPart) CollectionUtils.zipToMap(formFieldsFromMultipartRequest.map(fileItem => (fileItem.getFieldName, fileItem.getString("UTF-8"))))
    else Map()

  def uploads: Map[String, Seq[Upload]] =
    if (isMultiPart) CollectionUtils.zipToMap(fileItemsFromMultipartRequest.map(fileItem => (fileItem.getFieldName, toUpload(fileItem))))
    else Map()

  private def toUpload(fileItemArg: FileItem) = new Upload() {
    val fileItem = fileItemArg

    def name = fileItem.getName

    def size = fileItem.getSize

    def stream = fileItem.getInputStream
  }

  private def isMultiPart = ServletFileUpload.isMultipartContent(httpServletRequest)

  private def formFieldsFromMultipartRequest: Seq[FileItem] = allFileItems.filter(_.isFormField)

  private def fileItemsFromMultipartRequest: Seq[FileItem] = allFileItems.filter(isValidFileItem)

  private def isValidFileItem(fileItem: FileItem) = !fileItem.isFormField && (fileItem.getSize > 0 || !fileItem.getName.isEmpty)

  private lazy val allFileItems: Seq[FileItem] = fileUpload.parseRequest(httpServletRequest).asScala

  private def fileUpload = new ServletFileUpload(fileItemFactory)

  private def fileItemFactory: FileItemFactory = {
    val ret = new DiskFileItemFactory()
    ret.setRepository(FileUtils.getTempDirectory)
    ret
  }
}

private[web] class HttpRequestWithSlingUpload(httpServletRequest: SlingHttpServletRequest, httpServletResponse: HttpServletResponse) extends HttpRequest(httpServletRequest, httpServletResponse) {
  def multiPartParameters: Map[String, Seq[String]] = CollectionUtils.zipToMap(formFieldsFromMultipartRequest.map(e => (e._1, e._2.getString("UTF-8"))))

  def uploads: Map[String, Seq[Upload]] = CollectionUtils.zipToMap(fileItemsFromMultipartRequest.map(e => (e._1, toUpload(e._1, e._2))))

  private def toUpload(nameArg: String, requestParameterArg: RequestParameter) = new Upload() {
    val requestParameter = requestParameterArg

    def name = new String(requestParameter.getFileName.getBytes("ISO8859-1"), "UTF-8")

    def size = requestParameter.getSize

    def stream = requestParameter.getInputStream
  }

  private def formFieldsFromMultipartRequest: Seq[(String, RequestParameter)] = allRequestParameters.filter(_._2.isFormField)

  private def fileItemsFromMultipartRequest: Seq[(String, RequestParameter)] = allRequestParameters.filter(e => isValidFileItem(e._2))

  private def isValidFileItem(requestParameter: RequestParameter) = !requestParameter.isFormField && (requestParameter.getSize > 0 || !requestParameter.getFileName.isEmpty)

  private lazy val allRequestParameters: Seq[(String, RequestParameter)] =
    httpServletRequest.getRequestParameterMap.asScala.map(e => e._2.map(e._1 ->).toList).flatten.toSeq
}
