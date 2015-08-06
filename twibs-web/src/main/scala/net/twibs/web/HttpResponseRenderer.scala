/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.google.common.base.Charsets
import com.google.common.io.ByteStreams
import net.twibs.util.Predef._
import net.twibs.util.Request
import org.threeten.bp.ZonedDateTime

class HttpResponseRenderer(request: Request, response: Response, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse) {
  private val currentDateTime = ZonedDateTime.now()

  private def expires = currentDateTime.plus(expiresAfter)

  private def expiresAfter = response.expiresOnClientAfter

  def render(): Unit =
    response match {
      case r: RedirectResponse => redirectTo(r.asString)
      case r: NotFoundResponse => transfer()
      case r: ErrorResponse => transfer()
      case _ => respond()
    }

  private def redirectTo(target: String): Unit =
    httpResponse.sendRedirect(target)

  private def respond(): Unit =
    if (response.lastModified == ifModifiedSince) {
      httpResponse.setDateHeader("Expires", expires.toInstant.toEpochMilli)
      httpResponse.sendError(HttpServletResponse.SC_NOT_MODIFIED)
    } else
      transfer()

  private def transfer(): Unit = {
    httpResponse.setStatus(status(response))
    //httpResponse.setHeader("Cache-Control", "private, max-age=0")
    httpResponse.setHeader("Vary", "Accept-Encoding")
    httpResponse.setDateHeader("Date", currentDateTime.toInstant.toEpochMilli)
    httpResponse.setDateHeader("Expires", expires.toInstant.toEpochMilli)

    response match {
      case r: AsAttachment => httpResponse.setHeader("Content-Disposition", """attachment; filename="""" + r.attachmentFileName + '"')
      case _ =>
    }

    if (response.mimeType.startsWith("text/"))
      httpResponse.setCharacterEncoding(Charsets.UTF_8.name)

    httpResponse.setContentType(response.mimeType)
    httpResponse.setDateHeader("Last-Modified", response.lastModified)
    transferContent()
  }

  private def status(response: Response) =
    response match {
      case r: RedirectResponse => HttpServletResponse.SC_MOVED_PERMANENTLY
      case r: NotFoundResponse => HttpServletResponse.SC_NOT_FOUND
      case r: ErrorResponse => HttpServletResponse.SC_INTERNAL_SERVER_ERROR
      case _ => HttpServletResponse.SC_OK
    }

  private def ifModifiedSince = httpRequest.getDateHeader("If-Modified-Since")

  private def transferContent(): Unit = {
    response.gzippedOption.filter(x => request.doesClientSupportGzipEncoding) match {
      case Some(bytes) =>
        httpResponse.setHeader("Content-Encoding", "gzip")
        httpResponse.setContentLength(bytes.length)
        httpResponse.getOutputStream.write(bytes)
      case None =>
        httpResponse.setContentLength(response.length.toInt)
        response.asInputStream useAndClose {is => ByteStreams.copy(is, httpResponse.getOutputStream)}
    }
    httpResponse.getOutputStream.close()
  }
}
