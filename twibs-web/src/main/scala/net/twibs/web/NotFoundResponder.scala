/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util.Request

class NotFoundResponder(contentResponder: Responder, fallbackContentResponder: Responder) extends RecursiveFilenameResolverResponder(fallbackContentResponder, "_404.html") {
  def respond(request: Request): Option[Response] =
    contentResponder.respond(request) match {
      case None => respondWithFilename(request) match {
        case None => None
        case Some(response) => Some(new DecoratableResponseWrapper(response) with NotFoundResponse)
      }
      case responseOption => responseOption
    }
}
