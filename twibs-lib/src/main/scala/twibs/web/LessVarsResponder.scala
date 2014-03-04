package twibs.web

import twibs.util.{ClassUtils, WebContext}

class LessVarsResponder extends Responder {
  def respond(request: Request): Option[Response] =
    if (request.path == "/inc/_foreign/twibs-vars.less") Some(
      new StringResponse with CacheableResponse with CssMimeType with CalculatedLastModifiedResponse {
        def asString: String = s"""@context-path: "${WebContext.path}";"""

        def calculateModified: Long = ClassUtils.getCompilationTime(classOf[LessVarsResponder])
      })
    else None
}
