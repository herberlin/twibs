/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

import org.threeten.bp.Duration
import org.threeten.bp.format.DateTimeFormatter
import twibs.util.{WebContext, Loggable}

class LoggingResponder(delegate: Responder) extends Responder with Loggable {
  def respond(request: Request) = {
    val responseOption = delegate.respond(request)
    responseOption.map(response => log(request, response))
    responseOption
  }

  def log(request: Request, response: Response): Unit = {
    def elapsed = Duration.between(timestamp, Request.now()).toMillis

    def timestamp = request.timestamp

    //    def referrer = request.referrerOption getOrElse "-"
    //
    //    def userAgent = request.userAgentOption getOrElse "-"
    //
    //    def remoteUserString = request.remoteUserOption getOrElse "-"
    //
    def timestampString = DateTimeFormatter.ISO_DATE_TIME.format(timestamp)

    def status = response match {
      case r: RedirectResponse => "Redirect"
      case r: NotFoundResponse => "NotFound"
      case r: ErrorResponse => "Error"
      case _ => "Ok"
    }

    def uri = request.domain + WebContext.path + request.path

    //    def remoteAddress = request.remoteAddress
    //
    def contentLengthString = response.length.toString

    if (logger.isInfoEnabled) {
      logger.info( s"""$timestampString "$uri" $status $contentLengthString $elapsed ms""")
      //      logger.info( s"""$remoteAddress $remoteUserString $timestampString "$uri" $status "$referrer" "$userAgent" $elapsed ms""")
    }
  }
}
