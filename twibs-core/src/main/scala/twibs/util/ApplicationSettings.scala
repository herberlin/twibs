package twibs.util

import collection.JavaConverters._
import com.ibm.icu.util.ULocale
import concurrent.duration._
import java.net.{UnknownHostException, InetAddress}
import org.apache.tika.Tika

class SettingsFactory {
  val defaultSystemSettings: SystemSettings = new SystemSettings with Loggable {
    logger.info(s"Run mode is '${runMode.name}'")
  }

  val defaultUserSettings: UserSettings = new UserSettings {
    val name = "anonymous"

    val locale = defaultSystemSettings.locale
  }

  def defaultApplicationSettings: ApplicationSettings = defaultApplicationSettingsCached.value

  def applicationSettingsMap = applicationSettingsMapCache.value

  private val applicationSettingsMapCache = LazyCache(if (defaultSystemSettings.runMode.isDevelopment) 15 second else 1 day) {
    ConfigurationForTypesafeConfig.baseConfig().getObject("APPLICATIONS").unwrapped().keySet().asScala.filter(_ != ApplicationSettings.DEFAULT_NAME).map(name => name -> new ApplicationSettings(name)).toMap
  }

  private val defaultApplicationSettingsCached = LazyCache(if (defaultSystemSettings.runMode.isDevelopment) 15 second else 1 day) {new ApplicationSettings(ApplicationSettings.DEFAULT_NAME)}

  def applicationSettingsForPath(path: String) =
    SettingsFactory.applicationSettingsMap.values.collectFirst {case x if x.matches(path) => x} getOrElse defaultApplicationSettings
}

object SettingsFactory {
  implicit def unwrap(companion: SettingsFactory.type): SettingsFactory = current

  def current = currentVar

  private val currentVar = new SettingsFactory()
}

class SystemSettings {
  val startedAt = System.currentTimeMillis

  val hostName =
    try InetAddress.getLocalHost.getHostName
    catch {
      case e: UnknownHostException => "localhost"
    }

  val userName = System.getProperty("user.name")

  import RunMode._

  val runMode = Option(System.getProperty("run.mode")) match {
    case Some(DEVELOPMENT.name) => DEVELOPMENT
    case Some(STAGING.name) => STAGING
    case None if isCalledFromTestClass => TEST
    case _ => PRODUCTION
  }

  val locale = ULocale.getDefault

  private def isCalledFromTestClass = new Exception().getStackTrace.exists(_.getClassName == "org.scalatest.tools.Runner")

  object Twibs {
    val fullVersion: String = "0.5"

    val majorVersion = fullVersion.split("\\.")(0)

    val version = if (runMode.isProduction) majorVersion else fullVersion
  }

}

object SystemSettings {
  implicit def unwrap(companion: SystemSettings.type) = current

  def current = SettingsFactory.current.defaultSystemSettings
}

object RunMode {
  val PRODUCTION = new RunMode("production")

  val STAGING = new RunMode("staging")

  val TEST = new RunMode("test")

  val DEVELOPMENT = new RunMode("development")

  implicit def unwrap(companion: RunMode.type): RunMode = current

  def current = SystemSettings.current.runMode
}

class RunMode(val name: String) {
  val isDevelopment = name == "development"

  val isTest = name == "test"

  val isStaging = name == "staging"

  val isProduction = name == "production"
}

trait UserSettings {
  def name: String

  def locale: ULocale
}

class ApplicationSettings(val name: String) {
  lazy val configuration: Configuration = ConfigurationForTypesafeConfig.forSettings(name)

  lazy val locales = configuration.getStringList("locales").map(_.map(localeId => new ULocale(localeId))) getOrElse List(SystemSettings.locale)

  lazy val translators: Map[ULocale, Translator] = locales.map(locale => locale -> new TranslatorResolver(locale, configuration.configurationForLocale(locale)).root.usage(name)).toMap

  lazy val tika = new Tika()

  def use[T](f: => T) = ApplicationSettings.use(this)(f)

  def matches(path: String) = configuration.getStringList("pathes", Nil).exists(path.startsWith)
}

object ApplicationSettings extends DynamicVariableWithDefault[ApplicationSettings] {
  val PN_NAME = "application-name"

  val DEFAULT_NAME = "default"

  def default: ApplicationSettings = SettingsFactory.defaultApplicationSettings
}

trait CurrentRequestSettings {
  final val requestSettings = RequestSettings.current

  def locale = requestSettings.locale

  def translator = requestSettings.translator

  def formatters = requestSettings.formatters
}

class RequestSettings(val applicationSettings: ApplicationSettings = ApplicationSettings.current) {
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
  override def default: RequestSettings = new RequestSettings()
}
