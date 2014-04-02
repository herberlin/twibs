/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

import twibs.util.WebContext

class IndexRedirectResponder extends Responder {
  def respond(request: Request): Option[Response] =
    if (needsRedirectToIndex(request)) Some(new RedirectResponse(WebContext.path + "/index.html"))
    else None

  private def needsRedirectToIndex(request: Request): Boolean =
    request.path == "/" || request.path == "/_RedirectToIndex"
}
