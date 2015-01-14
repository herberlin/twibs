/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util.{Request, Loggable}

class StaticErrorResponder(contentResponder: Responder) extends Responder with Loggable {
  def respond(request: Request): Option[Response] =
    try {
      contentResponder.respond(request)
    }
    catch {
      case e: Exception =>
        logger.error("Uncatched exception", e)
        Some(new StringResponse with CacheableResponse with ErrorResponse with HtmlMimeType {
          def asString: String = "Internal Server Error"

          def lastModified: Long = System.currentTimeMillis()

          def isModified = false
        })
    }
}
