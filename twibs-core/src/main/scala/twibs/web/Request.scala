package twibs.web

import com.ibm.icu.util.ULocale
import java.io.InputStream
import java.net.URI
import org.threeten.bp.{LocalDateTime, Clock}
import twibs.util._

trait Request extends Serializable with AttributeContainer {
  def timestamp: LocalDateTime

  def method: RequestMethod

  def domain: String

  def path: String

  def doesClientSupportGzipEncoding: Boolean

  def accept: List[String]

  //  def referrerOption: Option[String]
  //
  //  def userAgentOption: Option[String]
  //
  //  def remoteUserOption: Option[String]
  //
  //  def doesClientSupportGzipEncoding: Boolean
  //
  //  def remoteAddress: String
  //
  //  def uri: String
  //
  def parameters: Parameters

  def uploads: Map[String, Seq[Upload]]

  def cacheKey = new RequestCacheKey(path, method, domain, parameters)

  def session: Session

  def useCache: Boolean

  def use[R](f: => R): R = Request.use(this)(f)

  def relative(relativePath: String) = new RequestWrapper(this) {
    override def path = new URI(super.path).resolve(relativePath).normalize().toString
  }

  override def toString = s"Request[$path|$method]"

  def desiredLocale: ULocale
}

class RequestWrapper(val delegatee: Request) extends Request {
  def timestamp = delegatee.timestamp

  def path = delegatee.path

  def domain = delegatee.domain

  def method = delegatee.method

  def accept = delegatee.accept

  def doesClientSupportGzipEncoding = delegatee.doesClientSupportGzipEncoding

  def parameters = delegatee.parameters

  def uploads = delegatee.uploads

  def session = delegatee.session

  def setAttribute(name: String, value: Any): Unit = delegatee.setAttribute(name, value)

  def getAttribute(name: String): Option[Any] = delegatee.getAttribute(name)

  def removeAttribute(name: String): Unit = delegatee.removeAttribute(name)

  def useCache = delegatee.useCache

  def desiredLocale = delegatee.desiredLocale

  def useAndRespond(responder: Responder) = use {
    responder.respond(this)
  }
}

object Request extends DynamicVariableWithDynamicDefault[Request](ImmutableRequest) {
  val clock = Clock.systemUTC()

  def now() = LocalDateTime.now(clock)
}

private object ImmutableRequest extends Request {
  val timestamp = Request.now()

  def method: RequestMethod = GetMethod

  def domain: String = "localhost"

  def path: String = "/"

  def accept: List[String] = Nil

  def doesClientSupportGzipEncoding: Boolean = true

  def parameters: Parameters = Parameters()

  def uploads = Map[String, Seq[Upload]]()

  def session: Session = new Session() {
    def setAttribute(name: String, value: Any): Unit = Unit

    def getAttribute(name: String): Option[Any] = None

    def removeAttribute(name: String): Unit = Unit

    def invalidate(): Unit = Unit
  }

  def setAttribute(name: String, value: Any): Unit = Unit

  def getAttribute(name: String): Option[Any] = None

  def removeAttribute(name: String): Unit = Unit

  def useCache = true

  val desiredLocale = SystemSettings.locale
}

trait Upload {
  val id: String = IdGenerator.next()

  def name: String

  def size: Long

  def stream: InputStream

  def mimeTypeString: String = ApplicationSettings.tika.detect(stream, name)

  def sizeAsHumanReadableString = Formatters.getHumanReadableByteCountSi(size)
}
