/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import net.twibs.util.Request

class HtmlMinimizerResponder(contentResponder: Responder) extends Responder {
  def respond(request: Request): Option[Response] =
    (if (request.path.toLowerCase.endsWith(".html")) contentResponder.respond(request) else None) match {
      case Some(response) if !response.isContentFinal => Some(minimize(request, response))
      case any => any
    }

  def minimize(request: Request, response: Response): Response = {
    val compressor = new HtmlCompressor()
    compressor.setRemoveSurroundingSpaces("br,p,head,body,html,article,section,nav,dt,dd,h1,h2,h3,h4,h5,h6,script,li,ul,ol,meta,link")
    new StringResponse with SingleResponseWrapper with HtmlMimeType {
      override protected def delegatee = response

      val asString: String = compressor.compress(response.asString)

      override def isContentFinal = true
    }
  }
}
