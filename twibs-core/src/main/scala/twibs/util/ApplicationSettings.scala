/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import java.net.{InetAddress, UnknownHostException}

import com.ibm.icu.util.ULocale
import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions}
import org.apache.tika.Tika
import scala.collection.JavaConverters._

private object ConfigHelper {

  implicit class EnhancedConfig(config: Config) {
    def childConfig(path: String) = if (config.hasPath(path)) config.getConfig(path).withFallback(config) else config
  }

}

import twibs.util.ConfigHelper._

case class SystemSettings(startedAt: Long,
                          hostName: String,
                          userName: String,
                          locale: ULocale,
                          fullVersion: String,
                          runMode: RunMode,
                          os: OperatingSystem) {
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

  def use(f: => Unit): Unit = {
    val was = SystemSettings.current
    this.activate()
    try {f} finally {SystemSettings._current = was}
  }

  def activate(): Unit = SystemSettings._current = this
}

object SystemSettings extends Loggable {
  implicit def unwrap(companion: SystemSettings.type) = current

  def current = _current

  val default = computeDefault()

  def computeDefault() = new SystemSettings(
    startedAt = System.currentTimeMillis,
    hostName =
      try InetAddress.getLocalHost.getHostName
      catch {
        case e: UnknownHostException => "localhost"
      },
    userName = System.getProperty("user.name"),
    locale = ULocale.getDefault,
    fullVersion = Option(getClass.getPackage.getSpecificationVersion) getOrElse "0.0",
    runMode = Option(System.getProperty("run.mode")) match {
      case Some(RunMode.DEVELOPMENT.name) => RunMode.DEVELOPMENT
      case Some(RunMode.STAGING.name) => RunMode.STAGING
      case Some(RunMode.TEST.name) => RunMode.TEST
      case None if isCalledFromTestClass => RunMode.TEST
      case _ => RunMode.PRODUCTION
    },
    os = OperatingSystem(System.getProperty("os.name").toLowerCase)
  )

  private var _current: SystemSettings = default

  private def isCalledFromTestClass = new Exception().getStackTrace.exists(_.getClassName.startsWith("org.scalatest"))
}

case class RunMode(name: String) {
  lazy val isDevelopment = this == RunMode.DEVELOPMENT

  lazy val isTest = this == RunMode.TEST

  lazy val isStaging = this == RunMode.STAGING

  lazy val isProduction = this == RunMode.PRODUCTION
}

object RunMode {
  val PRODUCTION = new RunMode("production")

  val STAGING = new RunMode("staging")

  val TEST = new RunMode("test")

  val DEVELOPMENT = new RunMode("development")

  implicit def unwrap(companion: RunMode.type): RunMode = current

  def current = SystemSettings.current.runMode
}

case class OperatingSystem(os: String) {
  val isWindows = os.indexOf("win") >= 0

  val isMac = os.indexOf("mac") >= 0

  val isUnix = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0

  val isSolaris = os.indexOf("sunos") >= 0
}

object OperatingSystem {
  implicit def unwrap(companion: OperatingSystem.type): OperatingSystem = current

  def current = SystemSettings.current.os
}

trait UserSettings {
  def name: String

  def locale: ULocale
}

class ApplicationSettings(val name: String, val systemSettings: SystemSettings) {
  private val configUnresolved = systemSettings.configUnresolved.childConfig("APPLICATIONS." + name)

  val configuration: Configuration = new ConfigurationForTypesafeConfig(configUnresolved.resolve())

  val locales = configuration.getStringList("locales").fold(List(systemSettings.locale))(_.map(localeId => new ULocale(localeId)))

  val translators: Map[ULocale, Translator] = locales.map(locale => locale -> new TranslatorResolver(locale, new ConfigurationForTypesafeConfig(configUnresolved.childConfig("LOCALES." + locale.toString).resolve())).root.usage(name)).toMap

  val defaultRequestSettings = new RequestSettings(this)

  lazy val tika = new Tika()

  def use[T](f: => T) = ApplicationSettings.use(this)(f)

  def matches(path: String) = configuration.getStringList("pathes", Nil).exists(path.startsWith)
}

object ApplicationSettings extends DynamicVariableWithDefault[ApplicationSettings] {
  val PN_NAME = "application-name"

  val DEFAULT_NAME = "default"

  override def default: ApplicationSettings = SystemSettings.defaultApplicationSettings
}

trait CurrentRequestSettings {
  final val requestSettings = RequestSettings.current

  def locale = requestSettings.locale

  def translator = requestSettings.translator

  def formatters = requestSettings.formatters
}

class RequestSettings(val applicationSettings: ApplicationSettings) {
  lazy val locale: ULocale = applicationSettings.locales.head

  lazy val translator: Translator = applicationSettings.translators(locale)

  lazy val formatters = new Formatters(translator, locale, "EUR")

  def use[T](f: => T) = RequestSettings.use(this)(f)

  def withLocale(localeArg: ULocale): RequestSettings =
    new RequestSettings(applicationSettings) {
      override lazy val locale = localeArg
    }
}

object RequestSettings extends DynamicVariableWithDefault[RequestSettings] {
  override def default: RequestSettings = ApplicationSettings.defaultRequestSettings
}
