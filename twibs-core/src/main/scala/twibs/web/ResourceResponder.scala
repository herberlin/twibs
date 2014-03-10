package twibs.web

import java.net.URL
import twibs.util.Predef._
import twibs.util.{ApplicationSettings, IOUtils}

trait ResourceResponder extends Responder {
  def respond(request: Request): Option[Response] =
    if (request.method != GetMethod)
      None
    else
      getResourceOption(request).flatMap {
        resource =>
          def exists = try {
            resource.openStream() closeAfter Unit
            true
          } catch {
            case e: Throwable => false
          }

          if (exists && !IOUtils.isDirectory(resource)) {
            Some(new InputStreamResponse() with CacheableResponse {
              def asInputStream = resource.openStream()

              val length = resource.openConnection().getContentLengthLong

              val lastModified = resource.openConnection().getLastModified

              def isModified = !exists || resource.openConnection().getLastModified != lastModified

              lazy val mimeType = asInputStream useAndClose {
                is => ApplicationSettings.tika.detect(resource)
              }
            })
          }
          else None
      }

  def getResourceOption(request: Request): Option[URL]
}
