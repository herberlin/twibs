/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

class HideResponder(contentResponder: Responder) extends Responder {
  def respond(request: Request): Option[Response] =
    if (request.path.matches(".*/_.*")) None
    else contentResponder.respond(request)
}
