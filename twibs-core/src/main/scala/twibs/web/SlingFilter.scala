package twibs.web

import javax.servlet.http.HttpServletRequest
import org.apache.sling.api.SlingHttpServletRequest

class SlingFilter extends Filter {
  override def createRequest(httpRequest: HttpServletRequest): HttpRequest =
    httpRequest match {
      case slingRequest: SlingHttpServletRequest => new HttpRequestWithSlingUpload(slingRequest)
      case _ => super.createRequest(httpRequest)
    }
}
