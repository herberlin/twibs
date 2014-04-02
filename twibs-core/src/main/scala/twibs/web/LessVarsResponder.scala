/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

import twibs.util.{ClassUtils, WebContext}

class LessVarsResponder extends Responder {
  def respond(request: Request): Option[Response] =
    if (request.path == "/inc/_foreign/twibs-vars.less") Some(
      new StringResponse with CacheableResponse with CssMimeType with CompilationTimeResponse {
        def asString: String = s"""@context-path: "${WebContext.path}";"""

        override def compilationTime: Long = ClassUtils.getCompilationTime(classOf[LessVarsResponder])
      })
    else None
}
