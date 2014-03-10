package twibs.web

import com.google.common.base.Charsets
import com.google.common.io.{CharStreams, ByteStreams}
import concurrent.duration._
import java.io._
import java.util.zip.GZIPOutputStream
import twibs.util.IOUtils._
import twibs.util.{ApplicationSettings, RunMode}

trait Response extends Serializable {
  def asInputStream: InputStream

  def asBytes: Array[Byte]

  def asString: String

  def length: Long

  def lastModified: Long

  def mimeType: String

  def isModified: Boolean

  def isCacheable: Boolean

  def expiresOnClientAfter: Duration

  def isWrappable: Boolean = true

  def isInMemory: Boolean

  lazy val gzippedOption: Option[Array[Byte]] = {
    val bytes = using(asInputStream) {
      is => compressWithGzip(is)
    }
    if (bytes.length < length)
      Some(bytes)
    else None
  }

  private def compressWithGzip(uncompressed: InputStream) = {
    val baos = new ByteArrayOutputStream()
    using(new GZIPOutputStream(baos)) {
      os => ByteStreams.copy(uncompressed, os)
    }
    baos.toByteArray
  }

}

class ResponseWrapper(delegate: Response) extends Response {
  def asInputStream: InputStream = delegate.asInputStream

  def asBytes: Array[Byte] = delegate.asBytes

  def asString: String = delegate.asString

  def length: Long = delegate.length

  def lastModified: Long = delegate.lastModified

  def mimeType: String = delegate.mimeType

  def isModified: Boolean = delegate.isModified

  def expiresOnClientAfter: Duration = delegate.expiresOnClientAfter

  def isCacheable: Boolean = delegate.isCacheable

  def isInMemory: Boolean = delegate.isInMemory
}

trait InputStreamResponse extends Response {
  def asBytes: Array[Byte] = ByteStreams.toByteArray(asInputStream)

  def asString: String = CharStreams.toString(new InputStreamReader(asInputStream, Charsets.UTF_8))

  def isInMemory: Boolean = false
}

trait FileResponse extends InputStreamResponse {
  def file: File

  def asInputStream = new FileInputStream(file)

  val length = file.length()

  val lastModified = file.lastModified()

  def isModified = !file.exists || file.lastModified() != lastModified

  lazy val mimeType = ApplicationSettings.tika.detect(file)
}

trait StringResponse extends Response {
  def asBytes: Array[Byte] = asString.getBytes(Charsets.UTF_8)

  def asInputStream: InputStream = new ByteArrayInputStream(asBytes)

  def length: Long = asBytes.length

  def isInMemory: Boolean = true
}

trait ByteArrayResponse extends Response {
  def asString = new String(asBytes, Charsets.UTF_8)

  def asInputStream: InputStream = new ByteArrayInputStream(asBytes)

  def length: Long = asBytes.length

  def isInMemory: Boolean = true
}

class RedirectResponse(val asString: String) extends StringResponse {
  def lastModified: Long = 0

  def mimeType: String = ""

  def isModified: Boolean = true

  def isCacheable: Boolean = false

  def expiresOnClientAfter: Duration = 8 hours

  override def isWrappable: Boolean = false
}

trait ErrorResponse extends Response

trait NotFoundResponse extends Response

trait CacheableResponse extends Response {
  def isCacheable = true

  def expiresOnClientAfter = if (RunMode.isDevelopment) 1 seconds else 8 hours
}

trait NotCacheableResponse extends Response {
  def isCacheable: Boolean = false

  def expiresOnClientAfter = 0 seconds
}

trait VolatileResponse extends NotCacheableResponse {
  def isModified: Boolean = true

  val lastModified: Long = System.currentTimeMillis()
}

trait CalculatedLastModifiedResponse extends CacheableResponse {
  val lastModified: Long = calculateModified

  def isModified: Boolean = lastModified != calculateModified

  def calculateModified: Long
}

trait CompilationTimeResponse extends CalculatedLastModifiedResponse {
  def calculateModified: Long = if (RunMode.isDevelopment) System.currentTimeMillis() else compilationTime

  def compilationTime: Long
}
