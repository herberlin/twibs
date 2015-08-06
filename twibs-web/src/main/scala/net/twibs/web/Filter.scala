/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import javax.servlet._
import javax.servlet.http._

import com.google.common.base.Charsets
import com.ibm.icu.util.ULocale
import net.twibs.util._
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.{FileItem, FileItemFactory}
import org.apache.commons.io.FileUtils
import org.threeten.bp.{DateTimeException, ZoneId, ZonedDateTime}

import scala.collection.convert.wrapAsScala._

abstract class AbstractFilter extends javax.servlet.Filter {
  private var servletContextVar: ServletContext = null

  def servletContext = servletContextVar

  override def init(filterConfig: FilterConfig): Unit = {
    servletContextVar = filterConfig.getServletContext
  }

  override def destroy(): Unit = ()

  override def doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain): Unit =
    CurrentServletRequest.withValue(request) {
      CurrentServletResponse.withValue(response) {
        (request, response) match {
          case (httpRequest: HttpServletRequest, httpResponse: HttpServletResponse) =>
            doFilter(httpRequest, httpResponse, filterChain)
          case _ => filterChain.doFilter(request, response)
        }
      }
    }

  def doFilter(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, filterChain: FilterChain): Unit = {
    CurrentHttpServletRequest.withValue(httpRequest) {
      CurrentHttpServletResponse.withValue(httpResponse) {
        httpRequest.setCharacterEncoding(Charsets.UTF_8.name)
        httpResponse.setCharacterEncoding(Charsets.UTF_8.name)
        httpResponse.setHeader("X-Twibs", if (RunMode.isPublic) SystemSettings.version else SystemSettings.version + " - " + RunMode.name)
        val request = createRequest(httpRequest, httpResponse)
        request.use {doFilter(request, httpRequest, httpResponse, filterChain)}
      }
    }
  }

  def doFilter(request: Request, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, filterChain: FilterChain): Unit

  def createRequest(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): Request =
    HttpServletRequestWithCommonsFileUpload(httpServletRequest, httpServletResponse)
}

class Filter extends AbstractFilter {
  private var combiningResponderVar: CombiningResponder = null

  def combiningResponder = combiningResponderVar

  def createCombiningResponder(): CombiningResponder = new FilterResponder(this)

  override def init(filterConfig: FilterConfig): Unit = {
    super.init(filterConfig)
    combiningResponderVar = createCombiningResponder()
  }

  override def destroy(): Unit = combiningResponderVar.destroy()

  override def doFilter(request: Request, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, filterChain: FilterChain): Unit = {
    combiningResponder.respond(request) match {
      case Some(response) =>
        new HttpResponseRenderer(request, response, httpRequest, httpResponse).render()
      case None =>
        ApplicationResponder.modify(request).use {
          filterChain.doFilter(httpRequest, httpResponse)
        }
    }
  }
}

trait HttpServletUtils {
  def removeUnderscoreParameterSetByJQuery(map: Map[String, Seq[String]]): Map[String, Seq[String]] = map.filterNot(_._1 == "_")
}

object HttpServletRequestBase extends HttpServletUtils {
  def apply(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): Request = {
    val cookieContainer = new CookieContainer {
      def getCookie(name: String) = Option(httpServletRequest.getCookies).flatMap(_.find(_.getName.equalsIgnoreCase(name))).map(_.getValue)

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

    val zoneId = cookieContainer.getCookie("client-time-zone").fold(Request.zoneId) { z =>
      try {ZoneId.of(z)} catch {
        case e: DateTimeException => Request.zoneId
      }
    }

    Request.copy(
      session = new HttpSession(httpServletRequest),

      cookies = cookieContainer,

      attributes = new AttributeContainer {
        def setAttribute(name: String, value: Any): Unit = httpServletRequest.setAttribute(name, value)

        def getAttribute(name: String): Option[Any] = Option(httpServletRequest.getAttribute(name))

        def removeAttribute(name: String): Unit = httpServletRequest.removeAttribute(name)
      },

      // The timestamp is created in the zone of the system, not the client
      timestamp = ZonedDateTime.now(),

      method = httpServletRequest.getMethod match {
        case "GET" => GetMethod
        case "POST" => PostMethod
        case "PUT" => PutMethod
        case "DELETE" => DeleteMethod
        case _ => UnknownMethod
      },

      protocol = if (httpServletRequest.isSecure) "https" else "http",

      domain = httpServletRequest.getServerName,

      port = httpServletRequest.getServerPort,

      path = httpServletRequest.getRequestURI.stripPrefix(httpServletRequest.getContextPath),

      remoteAddress = httpServletRequest.getRemoteAddr,

      remoteHost = httpServletRequest.getRemoteHost,

      userAgent = httpServletRequest.getHeader("User-Agent"),

      desiredLocale = ULocale.forLocale(httpServletRequest.getLocale),

      doesClientSupportGzipEncoding = Option(httpServletRequest.getHeader("Accept-Encoding")).exists(_ contains "gzip"),

      accept = Option(httpServletRequest.getHeader("Accept")).map(_.split(",").toList) getOrElse Nil,

      useCache = !(RunMode.isPrivate && httpServletRequest.getHeader("Cache-Control") == "no-cache" && httpServletRequest.getHeader("If-Modified-Since") == null),

      zoneId = zoneId

      //  def uri = httpServletRequest.getRequestURI

      //  def referrerOption = Option(httpServletRequest.getHeader("Referer"))

      //  def userAgentOption = Option(httpServletRequest.getHeader("User-Agent"))

      //  def remoteUserOption = Option(httpServletRequest.getRemoteUser)
    )
  }
}

object HttpServletRequestWithCommonsFileUpload extends HttpServletUtils {
  def apply(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) = {
    lazy val allFileItems: Seq[FileItem] = fileUpload.parseRequest(httpServletRequest)

    def fileUpload = new ServletFileUpload(fileItemFactory)

    def fileItemFactory: FileItemFactory = {
      val ret = new DiskFileItemFactory()
      ret.setRepository(FileUtils.getTempDirectory)
      ret
    }

    def parameters: Parameters = removeUnderscoreParameterSetByJQuery(urlParameters ++ multiPartParameters)

    def urlParameters: Map[String, Seq[String]] = httpServletRequest.getParameterMap.map(entry => (entry._1, entry._2.toSeq)).toMap

    def multiPartParameters: Map[String, Seq[String]] =
      if (isMultiPart) CollectionUtils.seqToMap(formFieldsFromMultipartRequest.map(fileItem => (fileItem.getFieldName, fileItem.getString(Charsets.UTF_8.name))))
      else Map()

    def formFieldsFromMultipartRequest: Seq[FileItem] = allFileItems.filter(_.isFormField)

    def uploads: Map[String, Seq[Upload]] =
      if (isMultiPart) CollectionUtils.seqToMap(fileItemsFromMultipartRequest.map(fileItem => (fileItem.getFieldName, toUpload(fileItem))))
      else Map()

    def isMultiPart = ServletFileUpload.isMultipartContent(httpServletRequest)

    def fileItemsFromMultipartRequest: Seq[FileItem] = allFileItems.filter(isValidFileItem)

    def isValidFileItem(fileItem: FileItem) = !fileItem.isFormField && (fileItem.getSize > 0 || !fileItem.getName.isEmpty)

    def toUpload(fileItemArg: FileItem) = new Upload() {
      val fileItem = fileItemArg

      def name = fileItem.getName

      def size = fileItem.getSize

      def stream = fileItem.getInputStream
    }

    HttpServletRequestBase(httpServletRequest, httpServletResponse).copy(parameters = parameters, uploads = uploads)
  }
}

object CurrentServletRequest extends UnwrapableDynamicVariable[ServletRequest](null)

object CurrentServletResponse extends UnwrapableDynamicVariable[ServletResponse](null)

object CurrentHttpServletRequest extends UnwrapableDynamicVariable[HttpServletRequest](null)

object CurrentHttpServletResponse extends UnwrapableDynamicVariable[HttpServletResponse](null)