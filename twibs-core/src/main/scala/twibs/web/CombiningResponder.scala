package twibs.web

import java.io.File
import org.apache.commons.io.FileUtils
import scala.concurrent.duration._
import twibs.util.WebContext

trait CombiningResponder extends Responder {
  def destroy(): Unit =
    uniqueCacheResponderVal.store()

  def respond(request: Request): Option[Response] = responder.respond(request)

  lazy val responder: Responder = loggingResponder()

  def loggingResponder(): Responder = new LoggingResponder(errorResponder())

  def errorResponder(): Responder = new StaticErrorResponder(new ErrorResponder(notFoundResponder(), cachingResponderVal))

  def notFoundResponder(): Responder = new StaticNotFoundResponder(new NotFoundResponder(hideResponder(), cachingResponderVal))

  def hideResponder(): Responder = new HideResponder(cachingResponderVal)

  final lazy val cachingResponderVal: Responder = cachingResponder()

  def cachingResponder(): Responder = new ExpiringCacheResponder(new MemoryCachingResponder(uniqueCacheResponderVal), 1 second)

  final lazy val uniqueCacheResponderVal: UniqueCacheResponder = uniqueCacheResponder()

  def uniqueCacheResponder() = {
    val storageFile = new File(FileUtils.getTempDirectory, "defaultresponder" + WebContext.path.replaceAll("/", "_"))
    val ret = new UniqueCacheResponder(processingResponder, Some(storageFile))
    ret.load()
    ret
  }

  final lazy val processingResponder: Responder = processingResponders()

  def processingResponders(): List[Responder] =
    new IndexRedirectResponder() :: contentModifingResponders()

  def contentModifingResponders() =
    new JsMinimizerResponder(new JsMergerResponder(contentResponder)) ::
      new LessCssParserResponder(contentResponder) ::
      new HtmlMinimizerResponder(contentResponder) :: contentResponder :: Nil

  final lazy val contentResponder: Responder = contentResponders()

  def contentResponders(): List[Responder] = staticContentResponders()

  final lazy val staticContentResponder: Responder = staticContentResponders()

  def staticContentResponders(): List[Responder] = classLoaderResponder :: new LessVarsResponder() :: Nil

  def classLoaderResponder: ClassLoaderResponder = new ClassLoaderResponder(getClass.getClassLoader, "/META-INF/resources")
}
