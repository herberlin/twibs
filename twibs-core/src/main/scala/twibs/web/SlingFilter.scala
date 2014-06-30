/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.apache.sling.api.SlingHttpServletRequest

class SlingFilter extends Filter {
  override def createRequest(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse): HttpRequest =
    httpRequest match {
      case slingRequest: SlingHttpServletRequest => new HttpRequestWithSlingUpload(slingRequest, httpResponse)
      case _ => super.createRequest(httpRequest, httpResponse)
    }
}
