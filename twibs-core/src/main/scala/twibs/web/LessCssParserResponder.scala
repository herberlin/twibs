package twibs.web

import collection.mutable.ListBuffer
import twibs.util._

class LessCssParserResponder(contentResponder: Responder, compress: Boolean = true) extends Responder with Loggable {
  def respond(request: Request): Option[Response] =
    if (request.path.toLowerCase.endsWith(".less")) contentResponder.respond(request)
    else if (request.path.toLowerCase.endsWith(".css")) {
      val lessRequest = new RequestWrapper(request) {
        override def path = request.path.dropRight(3) + "less"
      }
      contentResponder.respond(request) orElse lessRequest.use {contentResponder.respond(lessRequest)} match {
        case Some(response) if response.isWrappable => Some(compile(lessRequest, response))
        case any => any
      }
    } else None

  def compile(request: Request, response: Response): Response = {
    val responsesBuffer = ListBuffer(response)

    val lessCssParser = LessCssParserFactory.createParser {
      relativePath =>
        logger.debug(s"Loading: $relativePath")

        if (relativePath == request.path) Some(response.asString)
        else {
          contentResponder.respond(request.relative(relativePath)).map {
            response =>
              responsesBuffer += response
              response.asString
          }
        }
    }

    try {
      val string = lessCssParser.parse(request.path, compress)

      new StringResponse with CacheableResponse with CssMimeType {
        private val responses: List[Response] = responsesBuffer.toList

        val asString: String = string

        val lastModified: Long = responses.map(_.lastModified).max

        def isModified = responses.exists(_.isModified)
      }
    } catch {
      case e: LessCssParserException =>
        logger.error(e.getMessage, e)

        val string = if (RunMode.isDevelopment || RunMode.isTest) "// " + e.getMessage.replace("\n", "\n// ") else "// Internal server error"

        new StringResponse with CacheableResponse with CssMimeType with ErrorResponse {
          private val responses: List[Response] = responsesBuffer.toList

          val asString: String = string

          val lastModified: Long = responses.map(_.lastModified).max

          def isModified = responses.exists(_.isModified)
        }
    }
  }
}
