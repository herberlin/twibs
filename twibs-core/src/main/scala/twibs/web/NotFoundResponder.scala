package twibs.web

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
