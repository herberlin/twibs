/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util.RequestSettings

class IndexRedirectResponder extends Responder {
  def respond(request: Request): Option[Response] =
    if (needsRedirectToIndex(request)) Some(new RedirectResponse(RequestSettings.contextPath + "/index.html"))
    else None

  private def needsRedirectToIndex(request: Request): Boolean =
    request.path == "/" || request.path == "/_RedirectToIndex"
}
