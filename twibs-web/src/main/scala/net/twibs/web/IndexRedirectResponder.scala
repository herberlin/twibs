/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util.Request

class IndexRedirectResponder extends Responder {
  def respond(request: Request): Option[Response] =
    if (needsRedirectToIndex(request)) Some(new RedirectResponse(request.contextPath + "/index.html"))
    else None

  private def needsRedirectToIndex(request: Request): Boolean =
    request.path.string == "/" || request.path.string == "/_RedirectToIndex"
}
