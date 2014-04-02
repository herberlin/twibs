/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

import twibs.util.Loggable

class StaticErrorResponder(contentResponder: Responder) extends Responder with Loggable {
  def respond(request: Request): Option[Response] =
    try {
      contentResponder.respond(request)
    }
    catch {
      case e: Exception =>
        logger.error("Uncatched exception", e)
        Some(new StringResponse with CacheableResponse with ErrorResponse with HtmlMimeType {
          def asString: String = "Fatal error"

          def lastModified: Long = System.currentTimeMillis()

          def isModified = false
        })
    }
}
