/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util.Request

class HideResponder(contentResponder: Responder) extends Responder {
  def respond(request: Request): Option[Response] =
    if (request.path.matches(".*/_.*")) None
    else contentResponder.respond(request)
}
