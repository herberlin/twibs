/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util._

class JsMinimizerResponder(contentResponder: Responder) extends JsMinimizer with Responder with Loggable {
  def respond(request: Request): Option[Response] =
    (if (request.path.suffix == "js") contentResponder.respond(request) else None) match {
      case Some(response) if !response.isContentFinal => Some(minimize(request, response))
      case any => any
    }

  def minimize(request: Request, response: Response): Response = {
    val minimized = try {
      minimize(request.path.string, response.asString)
    } catch {
      case e: JsMinimizerException =>
        logger.warn(e.getMessage)
        if (RunMode.isPrivate) "// " + e.getMessage.replace("\n", "\n// ") else "// Internal Server Error"
    }

    new StringResponse with SingleResponseWrapper with JavaScriptMimeType {
      val delegatee = response

      val asString: String = minimized

      override def isContentFinal = true
    }
  }
}
