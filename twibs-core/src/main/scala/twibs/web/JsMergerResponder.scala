package twibs.web

import collection.mutable.ListBuffer
import twibs.util.{RunMode, Loggable}
import util.matching.Regex
import util.matching.Regex.Match

class JsMergerResponder(contentResponder: Responder) extends Responder with Loggable {
  val maxLevelOfRecursion = 40

  def respond(request: Request): Option[Response] = respond(0, request)

  def respond(recursionLevel: Int, request: Request): Option[Response] =
    (if (recursionLevel < maxLevelOfRecursion && request.path.toLowerCase.endsWith(".js")) contentResponder.respond(request) else None) match {
      case Some(response) if response.isWrappable => Some(merge(request, response, recursionLevel + 1))
      case any => any
    }

  def merge(request: Request, response: Response, recursionLevel: Int): Response = {
    val responsesBuffer = ListBuffer(response)

    def mergeURL(relativePath: String): String = {
      respond(recursionLevel, request.relative(relativePath)) match {
        case Some(r) =>
          responsesBuffer += r
          r.asString + ";"
        case None =>
          val message = s"""Included file '$relativePath' does not exist"""
          logger.warn(message)
          "// " + message
      }
    }

    val merged = """(?<!// {0,99})includeFile\("(.*)"\)\s*;?""".r replaceAllIn(response.asString, (m: Match) => Regex.quoteReplacement(mergeURL(m.group(1))))

    new StringResponse with MultiResponseWrapper with JavaScriptMimeType {
      protected val delegatees: List[Response] = responsesBuffer.toList

      val asString = merged
    }
  }
}
