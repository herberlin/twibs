package twibs.web

import twibs.util.Loggable

class ErrorResponder(contentResponder: Responder, errorContentResponder: Responder) extends RecursiveFilenameResolverResponder(errorContentResponder, "_500.html") with Loggable {
  def respond(request: Request): Option[Response] =
    try {
      contentResponder.respond(request)
    }
    catch {
      case e: Exception =>
        respondWithFilename(request) match {
          case None => throw e
          case Some(response) =>
            logger.error("Internal Server Error", e)
            Some(new ResponseWrapper(response) with ErrorResponse)
        }
    }
}
