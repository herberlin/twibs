package twibs.web

import twibs.util.{RunMode, Loggable, JsMinimizerException, JsMinimizer}

class JsMinimizerResponder(contentResponder: Responder) extends JsMinimizer with Responder with Loggable {
  def respond(request: Request): Option[Response] =
    (if (request.path.toLowerCase.endsWith(".js")) contentResponder.respond(request) else None) match {
      case Some(response) if !response.isContentFinal => Some(minimize(request, response))
      case any => any
    }

  def minimize(request: Request, response: Response): Response = {
    val minimized = try {
      minimize(request.path, response.asString)
    } catch {
      case e: JsMinimizerException =>
        logger.warn(e.getMessage)
        if (RunMode.isDevelopment || RunMode.isTest) "// " + e.getMessage.replace("\n", "\n// ") else "// Internal server error"
    }

    new StringResponse with SingleResponseWrapper with JavaScriptMimeType {
      val delegatee = response

      val asString: String = minimized

      override def isContentFinal = true
    }
  }
}
