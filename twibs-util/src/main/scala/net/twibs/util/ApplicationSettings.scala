/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.io.File
import java.net.{InetAddress, UnknownHostException}

import com.ibm.icu.util.{Currency, ULocale}
import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions}
import org.apache.tika.Tika
import org.threeten.bp.ZoneId

import scala.collection.JavaConverters._
import scala.concurrent.duration._

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
                          os: OperatingSystem,
                          zoneId: ZoneId) {
  private[util] val configUnresolved = {
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

  val applicationSettings = configUnresolved.getObject("APPLICATIONS").unwrapped().keySet().asScala.map(name => name -> new ApplicationSettings(name, this)).toMap

  val defaultApplicationSettings = applicationSettings(ApplicationSettings.DEFAULT_NAME)

  def applicationSettingsForPath(path: String) = applicationSettings.values.collectFirst { case x if x.matches(path) => x} getOrElse defaultApplicationSettings

  val majorVersion = fullVersion.split("\\.")(0)

  val version = if (runMode.isProduction) majorVersion else fullVersion

  def use[T](f: => T) = RequestSettings.applicationSettings.copy(systemSettings = this).use(f)
}

object SystemSettings extends Loggable {
  @inline implicit def unwrap(companion: SystemSettings.type) = current

  @inline def current = ApplicationSettings.current.systemSettings

  private def isCalledFromTestClass = new Exception().getStackTrace.exists(_.getClassName.startsWith("org.scalatest"))

  def default = defaultCached.value

  private val internalDefault = computeDefault()

  private val defaultCached = LazyCache(if (internalDefault.runMode.isDevelopment) 15 second else 8 hours)(computeDefault())

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
    os = OperatingSystem(System.getProperty("os.name").toLowerCase),
    zoneId = ZoneId.systemDefault()
  )

  logger.info(s"Run mode is '${default.runMode.name}'")
}

case class RunMode(name: String) {
  lazy val isDevelopment = this == RunMode.DEVELOPMENT

  lazy val isTest = this == RunMode.TEST

  lazy val isStaging = this == RunMode.STAGING

  lazy val isProduction = this == RunMode.PRODUCTION
}

object RunMode {
  val SYSTEM_PROPERTY_NAME = "run.mode"

  val PRODUCTION = new RunMode("production")

  val STAGING = new RunMode("staging")

  val TEST = new RunMode("test")

  val DEVELOPMENT = new RunMode("development")

  @inline implicit def unwrap(companion: RunMode.type): RunMode = current

  @inline def current = SystemSettings.runMode
}

case class OperatingSystem(os: String) {
  val isWindows = os.indexOf("win") >= 0

  val isMac = os.indexOf("mac") >= 0

  val isUnix = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0

  val isSolaris = os.indexOf("sunos") >= 0
}

object OperatingSystem {
  @inline implicit def unwrap(companion: OperatingSystem.type): OperatingSystem = current

  @inline def current = SystemSettings.os
}

trait UserSettings {
  def name: String

  def locale: ULocale
}

case class ApplicationSettings(name: String, systemSettings: SystemSettings) {
  private val configUnresolved = systemSettings.configUnresolved.childConfig("APPLICATIONS." + name)

  val configuration: Configuration = new ConfigurationForTypesafeConfig(configUnresolved.resolve())

  val locales = configuration.getStringList("locales").fold(List(systemSettings.locale))(_.map(localeId => new ULocale(localeId)))

  val translators: Map[ULocale, Translator] = locales.map(locale => locale -> new TranslatorResolver(locale, new ConfigurationForTypesafeConfig(configUnresolved.childConfig("LOCALES." + locale.toString).resolve())).root.usage(name)).toMap

  val defaultRequestSettings = RequestSettings(this)

  lazy val tika = new Tika()

  def use[T](f: => T) = RequestSettings.copy(applicationSettings = this).use(f)

  def matches(path: String) = configuration.getStringList("pathes", Nil).exists(path.startsWith)
}

object ApplicationSettings {
  val PN_NAME = "application-name"

  val DEFAULT_NAME = "default"

  @inline implicit def unwrap(companion: ApplicationSettings.type): ApplicationSettings = current

  @inline def current = RequestSettings.applicationSettings
}

trait CurrentRequestSettings {
  final val requestSettings = RequestSettings.current

  def locale = requestSettings.locale

  def translator = requestSettings.translator

  def formatters = requestSettings.formatters
}

case class RequestSettings private(applicationSettings: ApplicationSettings, locale: ULocale, contextPath: String = "") {
  lazy val translator: Translator = applicationSettings.translators(locale)

  lazy val formatters = new Formatters(translator, locale, Currency.getInstance("EUR"), applicationSettings.systemSettings.zoneId)

  def use[T](f: => T) = RequestSettings.use(this)(f)

  RequestSettings.assertThatContextPathIsValid(contextPath)
}

object RequestSettings extends DynamicVariableWithDynamicDefault[RequestSettings] {
  def createFallback: RequestSettings = RequestSettings(SystemSettings.default.defaultApplicationSettings)

  def apply(applicationSettings: ApplicationSettings): RequestSettings = RequestSettings(applicationSettings, applicationSettings.locales.head)

  def assertThatContextPathIsValid(contextPath: String) = {
    if (!contextPath.isEmpty) {
      assert(contextPath != "/", "contextPath must not be /")
      assert(contextPath.startsWith("/"), s"contextPath '$contextPath' must start with /")
      assert("/" + UrlUtils.encodeUrl(UrlUtils.decodeUrl(contextPath.substring(1))) == contextPath, s"contextPath '$contextPath' is invalid")
    }
    contextPath
  }
}
