package twibs.web

class StaticNotFoundResponder(contentResponder: Responder) extends Responder {
  def respond(request: Request): Option[Response] =
    Some(contentResponder.respond(request) getOrElse {
      new StringResponse with CacheableResponse with NotFoundResponse with HtmlMimeType {
        def asString: String = "Not found"

        def lastModified: Long = System.currentTimeMillis()

        def isModified = false
      }
    })
}
