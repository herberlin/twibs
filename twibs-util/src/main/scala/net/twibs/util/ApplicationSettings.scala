/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.io.File
import java.net.{InetAddress, UnknownHostException}

import com.ibm.icu.util.{Currency, ULocale}
import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions}
import org.apache.tika.Tika
import org.threeten.bp.{ZoneId, ZonedDateTime}

import scala.collection.convert.wrapAsScala._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.xml.{NodeSeq, Unparsed}

private object ConfigHelper {

  implicit class EnhancedConfig(config: Config) {
    def childConfig(path: String) = if (config.hasPath(path)) config.getConfig(path).withFallback(config) else config
  }

}

import net.twibs.util.ConfigHelper._

case class SystemSettings(startedAt: Long,
                          hostName: String,
                          userName: String,
                          userHome: File,
                          locale: ULocale,
                          fullVersion: String,
                          runMode: RunMode,
                          os: OperatingSystem) {
  @transient
  private[util] lazy val configUnresolved = {
    ConfigFactory.invalidateCaches()

    def loadAndDecorate(loader: ClassLoader, name: String) =
      ConfigFactory.defaultOverrides(loader).withFallback(load(loader, name)).withFallback(ConfigFactory.defaultReference(loader))

    def load(loader: ClassLoader, name: String) =
      ConfigFactory.parseResourcesAnySyntax(name, ConfigParseOptions.defaults.setClassLoader(loader).setAllowMissing(true))

    val ccl = Thread.currentThread.getContextClassLoader
    val ocl = SystemSettings.getClass.getClassLoader
    val applicationConfigWithOsgiFallback = loadAndDecorate(ccl, "application") withFallback loadAndDecorate(ocl, "application")
    val hostConfig = applicationConfigWithOsgiFallback.childConfig(s"HOSTS.$hostName")
    val baseConfig = hostConfig.childConfig(s"RUN-MODES.${runMode.name}")
    val userConfigWithOsgiFallback = load(ccl, "user").withFallback(load(ocl, "user"))
    userConfigWithOsgiFallback.withFallback(baseConfig)
  }

  @transient
  lazy val applicationSettings = configUnresolved.getObject("APPLICATIONS").unwrapped().keySet().map(name => name -> new ApplicationSettings(name, this)).toMap

  @transient
  lazy val defaultApplicationSettings = applicationSettings(ApplicationSettings.DEFAULT_NAME)

  def applicationSettingsForPath(path: Path) = applicationSettings.values.collectFirst { case x if x.matches(path) => x } getOrElse defaultApplicationSettings

  val majorVersion = fullVersion.split("\\.")(0)

  val version = if (runMode.isProduction) majorVersion else fullVersion

  def use[T](f: => T) = Request.applicationSettings.copy(systemSettings = this).use(f)
}

object SystemSettings extends UnwrapCurrent[SystemSettings] with Loggable {
  Logger.init()

  @inline def current = ApplicationSettings.current.systemSettings

  private def isCalledFromTestClass = new Exception().getStackTrace.exists(_.getClassName.startsWith("org.scalatest"))

  def default = defaultCached()

  private val internalDefault = computeDefault()

  private val defaultCached = Memo(computeDefault()).recomputeAfter(if (internalDefault.runMode.isDevelopment) 15 second else 8 hours)

  private def computeDefault() = new SystemSettings(
    startedAt = System.currentTimeMillis,
    hostName =
      try InetAddress.getLocalHost.getHostName
      catch {
        case e: UnknownHostException => "localhost"
      },
    userName = System.getProperty("user.name"),
    userHome = new File(System.getProperty("user.home")),
    locale = ULocale.getDefault,
    fullVersion = Option(getClass.getPackage.getSpecificationVersion) getOrElse "0.0",
    runMode = Option(System.getProperty(RunMode.SYSTEM_PROPERTY_NAME)) match {
      case Some(RunMode.DEVELOPMENT.name) => RunMode.DEVELOPMENT
      case Some(RunMode.STAGING.name) => RunMode.STAGING
      case Some(RunMode.TEST.name) => RunMode.TEST
      case None if isCalledFromTestClass => RunMode.TEST
      case _ => RunMode.PRODUCTION
    },
    os = OperatingSystem(System.getProperty("os.name").toLowerCase)
  )

  logger.info(s"Run mode is '${default.runMode.name}'")
}

case class RunMode(name: String) {
  lazy val isDevelopment = this == RunMode.DEVELOPMENT

  lazy val isTest = this == RunMode.TEST

  lazy val isStaging = this == RunMode.STAGING

  lazy val isProduction = this == RunMode.PRODUCTION

  def isPublic = isStaging || isProduction

  def isPrivate = isDevelopment || isTest
}

object RunMode extends UnwrapCurrent[RunMode] {
  val SYSTEM_PROPERTY_NAME = "run.mode"

  val PRODUCTION = new RunMode("production")

  val STAGING = new RunMode("staging")

  val TEST = new RunMode("test")

  val DEVELOPMENT = new RunMode("development")

  @inline def current = SystemSettings.runMode
}

case class OperatingSystem(os: String) {
  val isWindows = os.indexOf("win") >= 0

  val isMac = os.indexOf("mac") >= 0

  val isUnix = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0

  val isSolaris = os.indexOf("sunos") >= 0
}

object OperatingSystem extends UnwrapCurrent[OperatingSystem] {
  @inline def current = SystemSettings.os
}

trait UserSettings {
  def name: String

  def locale: ULocale
}

case class ApplicationSettings(name: String, systemSettings: SystemSettings) {
  @transient
  private lazy val configUnresolved = systemSettings.configUnresolved.childConfig("APPLICATIONS." + name)

  @transient
  lazy val configuration: Configuration = new ConfigurationForTypesafeConfig(configUnresolved.resolve())

  @transient
  lazy val locales = configuration.getStringList("locales").fold(List(systemSettings.locale))(_.map(localeId => new ULocale(localeId)))

  @transient
  lazy val translators: Map[ULocale, Translator] = locales.map(locale => locale -> new TranslatorResolver(locale, new ConfigurationForTypesafeConfig(configUnresolved.childConfig("LOCALES." + locale.toString).resolve())).root.usage(name)).toMap

  @transient
  lazy val defaultRequest = Request(this)

  @transient
  lazy val tika = new Tika()

  def use[T](f: => T) = Request.copy(applicationSettings = this).use(f)

  def matches(path: Path) = configuration.getStringList("pathes", Nil).exists(path.string.startsWith)

  def lookupLocale(desiredLocale: ULocale) = if (locales.isEmpty) desiredLocale else LocaleUtils.lookupLocale(locales, desiredLocale)
}

object ApplicationSettings extends UnwrapCurrent[ApplicationSettings] {
  val PN_NAME = "t-context"

  val DEFAULT_NAME = "default"

  @inline def current = Request.applicationSettings
}

trait Session extends AttributeContainer {
  def invalidate(): Unit

  import net.twibs.util.Session._

  def addNotificationToSession(notificationScript: String): Unit =
    setAttribute(NOTIFICATIONS_PARAMETER_NAME, notificationScriptsFromSession + notificationScript)

  private def notificationScriptsFromSession = getAttribute(NOTIFICATIONS_PARAMETER_NAME, "")

  def notifications = {
    val notificationsString = notificationScriptsFromSession
    removeAttribute(NOTIFICATIONS_PARAMETER_NAME)
    if (notificationsString.isEmpty) NodeSeq.Empty
    else <script>{Unparsed("$" + s"(function () {$notificationsString});")}</script>
  }
}

class SimpleSession extends SimpleAttributeContainer with Session {
  def invalidate(): Unit = ()
}

object Session extends UnwrapCurrent[Session] {
  private val NOTIFICATIONS_PARAMETER_NAME: String = "NOTIFICATIONS"

  def current = Request.session
}

case class User(userName: String, roles: Seq[String])

object User extends UnwrapCurrent[User] {
  def current = Request.user

  def anonymous = User("anonymous", Seq(Roles.EVERYONE))
}

object Roles {
  val EVERYONE = "everyone"
}

trait CurrentRequest {
  final val request = Request.current

  def locale = request.locale

  def translator = request.translator

  def formatters = request.formatters
}

case class ResponseRequest(method: RequestMethod = GetMethod,
                           protocol: String = "http",
                           domain: String = "localhost",
                           port: Int = 80,
                           contextPath: String = "",
                           path: Path = "/",
                           parameters: Parameters = Parameters())

/**
 * '''Note:''' Use [[ResponseRequest]] as cache key and for serialisation.
 */
case class Request private(applicationSettings: ApplicationSettings,
                           session: Session = new SimpleSession(),
                           cookies: CookieContainer = new SimpleCookieContainer(),
                           attributes: AttributeContainer = new SimpleAttributeContainer(),
                           timestamp: ZonedDateTime = ZonedDateTime.now(),
                           method: RequestMethod = GetMethod,
                           protocol: String = "http",
                           domain: String = "localhost",
                           port: Int = 80,
                           contextPath: String = "",
                           path: Path = "/",
                           parameters: Parameters,
                           uploads: Map[String, Seq[Upload]] = Map(),
                           user: User = User.anonymous,
                           remoteAddress: String = "::1",
                           remoteHost: String = "localhost",
                           userAgent: String = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/34.0.1847.116 Chrome/34.0.1847.116 Safari/537.36",
                           desiredLocale: ULocale,
                           doesClientSupportGzipEncoding: Boolean = true,
                           accept: List[String] = Nil,
                           useCache: Boolean = true,
                           zoneId: ZoneId = ZoneId.systemDefault()) {
  @transient
  lazy val locale = applicationSettings.lookupLocale(desiredLocale)

  @transient
  lazy val responseRequest = ResponseRequest(method, protocol, domain, port, contextPath, path, parameters)

  @transient
  lazy val translator: Translator = applicationSettings.translators(locale)

  @transient
  lazy val formatters = new Formatters(translator, locale, Currency.getInstance("EUR"), zoneId)

  def use[T](f: => T) = Request.use(this)(f)

  def useIt[R](f: (Request) => R): R = Request.use(this)(f(this))

  def relative(relativePath: String) = this.copy(path = path.resolve(relativePath))

  def dropFirstPathPart = this.copy(path = path.tail)

  def activate() = Request.activate(this)

  def toDomainURL = s"$protocol://$domain${if (port != 80) s":$port" else ""}"

  def toURLString = toDomainURL + path + parameters.toURLString

  Request.requireValidContextPath(contextPath)
}

object Request extends DynamicVariableWithDynamicDefault[Request] {
  def createFallback: Request = Request(SystemSettings.default.defaultApplicationSettings)

  def apply(applicationSettings: ApplicationSettings): Request = Request(applicationSettings, desiredLocale = applicationSettings.locales.head, parameters = Parameters())

  @inline def requireValidContextPath(contextPath: String) = {
    require(contextPath != "/", "contextPath must not be /")
    require(contextPath.isEmpty || contextPath.startsWith("/"), s"contextPath '$contextPath' must start with /")
    require(contextPath.isEmpty || "/" + UrlUtils.encodeUrl(UrlUtils.decodeUrl(contextPath.substring(1))) == contextPath, s"contextPath '$contextPath' is invalid")
  }
}
