/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util.{RequestSettings, ClassUtils}

class LessVarsResponder extends Responder {
  def respond(request: Request): Option[Response] =
    if (request.path == "/inc/_foreign/twibs-vars.less") Some(
      new StringResponse with CacheableResponse with CssMimeType with CompilationTimeResponse {
        def asString: String = s"""@context-path: "${RequestSettings.contextPath}";"""

        override def compilationTime: Long = ClassUtils.getCompilationTime(classOf[LessVarsResponder])
      })
    else None
}
