/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util.Formatters._
import net.twibs.util.{Loggable, Request}
import org.threeten.bp.{Duration, ZonedDateTime}

class LoggingResponder(delegate: Responder) extends Responder with Loggable {
  def respond(request: Request) = {
    val responseOption = delegate.respond(request)
    responseOption.foreach(response => log(request, response))
    responseOption
  }

  def log(request: Request, response: Response): Unit = {
    def elapsed = Duration.between(timestamp, ZonedDateTime.now()).toMillis

    def timestamp = request.timestamp

    //    def referrer = request.referrerOption getOrElse "-"
    //
    //    def userAgent = request.userAgentOption getOrElse "-"
    //
    //    def remoteUserString = request.remoteUserOption getOrElse "-"
    //
    def timestampString = timestamp.formatAsIso

    def status = response match {
      case r: RedirectResponse => "Redirect"
      case r: NotFoundResponse => "NotFound"
      case r: ErrorResponse => "Error"
      case _ => "Ok"
    }

    def uri = request.domain + request.contextPath + request.path.string

    //    def remoteAddress = request.remoteAddress
    //
    def contentLengthString = response.length.toString

    if (logger.isInfoEnabled) {
      logger.info( s"""$timestampString "$uri" $status $contentLengthString $elapsed ms""")
      //      logger.info( s"""$remoteAddress $remoteUserString $timestampString "$uri" $status "$referrer" "$userAgent" $elapsed ms""")
    }
  }
}
