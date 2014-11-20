/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import java.io.InputStream
import java.net.URI

import net.twibs.util._

import com.ibm.icu.util.ULocale
import org.threeten.bp.LocalDateTime

trait Request extends Serializable with AttributeContainer {
  def timestamp: LocalDateTime

  def method: RequestMethod

  def domain: String

  def path: String

  def doesClientSupportGzipEncoding: Boolean

  def accept: List[String]

  //  def referrerOption: Option[String]
  //
  //  def remoteUserOption: Option[String]
  //
  //  def doesClientSupportGzipEncoding: Boolean
  //
  def remoteAddress: String

  def remoteHost: String

  def userAgent: String

  //
  //  def uri: String
  //
  def parameters: Parameters

  def uploads: Map[String, Seq[Upload]]

  def cacheKey = new RequestCacheKey(path, method, domain, parameters)

  def session: Session

  def useCache: Boolean

  def use[R](f: => R): R = Request.use(this)(f)

  def useIt[R](f: (Request) => R): R = Request.use(this)(f(this))

  def relative(relativePath: String) = new RequestWrapper(this) {
    override def path = new URI(super.path).resolve(relativePath).normalize().toString
  }

  override def toString = s"Request[$path|$method]"

  def desiredLocale: ULocale

  def getCookie(name: String): Option[String]

  def removeCookie(name: String): Unit

  def setCookie(name: String, value: String): Unit
}

class RequestWrapper(val delegatee: Request) extends Request {
  def timestamp = delegatee.timestamp

  def path = delegatee.path

  def domain = delegatee.domain

  def method = delegatee.method

  def accept = delegatee.accept

  def remoteAddress = delegatee.remoteAddress

  def remoteHost = delegatee.remoteHost

  def userAgent = delegatee.userAgent

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

  def getCookie(name: String) = delegatee.getCookie(name)

  def removeCookie(name: String): Unit = delegatee.removeCookie(name)

  def setCookie(name: String, value: String) = delegatee.setCookie(name, value)
}

object Request extends DynamicVariableWithDynamicDefault[Request] {
  override def createFallback: Request = ImmutableRequest

  def now() = LocalDateTime.now()
}

private object ImmutableRequest extends Request {
  val timestamp = Request.now()

  def method: RequestMethod = GetMethod

  def domain: String = "localhost"

  def path: String = "/"

  def accept: List[String] = Nil

  def remoteAddress: String = "::1"

  def remoteHost: String = "localhost"

  def userAgent: String = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/34.0.1847.116 Chrome/34.0.1847.116 Safari/537.36"

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

  def getCookie(name: String) = None

  def removeCookie(name: String) = Unit

  def setCookie(name: String, value: String) = Unit
}

trait Upload {
  val id: String = IdGenerator.next()

  def name: String

  def size: Long

  def stream: InputStream

  def mimeTypeString: String = ApplicationSettings.tika.detect(stream, name)

  def sizeAsHumanReadableString = Formatters.getHumanReadableByteCountSi(size)
}
