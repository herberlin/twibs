/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util._

import scala.collection.mutable.ListBuffer

class LessCssParserResponder(contentResponder: Responder, compress: Boolean = true) extends Responder with Loggable {
  def respond(request: Request): Option[Response] =
    if (request.path.suffix == "less") contentResponder.respond(request)
    else if (request.path.suffix == "css") {
      val lessRequest = request.copy(path = request.path.copy(suffix = "less"))
      contentResponder.respond(request) orElse lessRequest.use {contentResponder.respond(lessRequest)} match {
        case Some(response) if !response.isContentFinal => Some(compile(lessRequest, response))
        case any => any
      }
    } else None

  def compile(request: Request, response: Response): Response = {
    val responsesBuffer = ListBuffer(response)

    val lessCssParser = LessCssParserFactory.createParser {
      relativePath =>
        logger.debug(s"Loading: $relativePath")

        if (relativePath == request.path.string) Some(response.asString)
        else {
          contentResponder.respond(request.relative(relativePath)).map {
            response =>
              responsesBuffer += response
              response.asString
          }
        }
    }

    try {
      val string = lessCssParser.parse(request.path.string, compress)

      new StringResponse with MultiResponseWrapper with CssMimeType {
        protected val delegatees: List[Response] = responsesBuffer.toList

        val asString: String = string

        override lazy val isContentFinal = true
      }
    } catch {
      case e: LessCssParserException =>
        logger.error(e.getMessage, e)

        val string = if (RunMode.isDevelopment || RunMode.isTest) "// " + e.getMessage.replace("\n", "\n// ") else "// Internal Server Error"

        new StringResponse with MultiResponseWrapper with CssMimeType with ErrorResponse {
          protected val delegatees: List[Response] = responsesBuffer.toList

          val asString: String = string

          override lazy val isContentFinal = true
        }
    }
  }
}
