package twibs.web

import com.googlecode.htmlcompressor.compressor.HtmlCompressor

class HtmlMinimizerResponder(contentResponder: Responder) extends Responder {
  def respond(request: Request): Option[Response] =
    (if (request.path.toLowerCase.endsWith(".html")) contentResponder.respond(request) else None) match {
      case Some(response) if response.isWrappable => Some(minimize(request, response))
      case any => any
    }

  def minimize(request: Request, response: Response): Response = {
    val compressor = new HtmlCompressor()
    compressor.setRemoveSurroundingSpaces("br,p,head,body,html,article,section,nav,dt,dd,h1,h2,h3,h4,h5,h6,script,li,ul,ol,meta,link")
    new StringResponse with CacheableResponse with HtmlMimeType {
      val asString: String = compressor.compress(response.asString)

      val lastModified: Long = response.lastModified

      def isModified = response.isModified
    }
  }
}
