package twibs.util

import RunMode._
import com.ibm.icu.util.ULocale
import concurrent.duration._
import java.net.{UnknownHostException, InetAddress}
import org.apache.tika.Tika
import scala.util.DynamicVariable

class Environment {
  lazy val settings: Settings = DefaultSettings

  lazy val configuration: Configuration = ConfigurationForTypesafeConfig.forSettings(settings)

  lazy val locale = configuration.locales.head

  lazy val translator = configuration.translators(locale)

  lazy val formatters = new Formatters(translator, locale, "EUR")

  def use[T](f: => T) = Environment.currentVar.withValue(Some(this))(f)

  def withLocale(localeArg: ULocale) = {
    def o = this
    new Environment {
      override lazy val settings = o.settings

      override lazy val configuration = o.configuration

      override lazy val locale = localeArg
    }
  }

  lazy val tika = new Tika()
}

object Environment {
  implicit def unwrap(companion: Environment.type): Environment = current

  def current = currentVar.value getOrElse default

  private val currentVar = new DynamicVariable[Option[Environment]](None)

  def default: Environment = cachedDefault.value

  private val cachedDefault = LazyCache(if (DefaultSettings.runMode.isDevelopment) 15 second else 1 day) {new Environment()}
}

trait CurrentEnviroment {
  final val environment = Environment.current

  def settings: Settings = environment.settings

  def configuration: Configuration = environment.configuration

  def locale = environment.locale

  def translator = environment.translator

  def formatters = environment.formatters

  def tika = environment.tika
}

class Settings {
  val startedAt = System.currentTimeMillis

  val hostName =
    try InetAddress.getLocalHost.getHostName
    catch {
      case e: UnknownHostException => "localhost"
    }

  val userName = System.getProperty("user.name")

  val runMode = Option(System.getProperty("run.mode")) match {
    case Some(DEVELOPMENT.name) => DEVELOPMENT
    case Some(STAGING.name) => STAGING
    case None if isCalledFromTestClass => TEST
    case _ => PRODUCTION
  }

  private def isCalledFromTestClass = new Exception().getStackTrace.exists(_.getClassName == "org.scalatest.tools.Runner")

  object Twibs {
    val fullVersion: String = "5.0"

    val majorVersion = fullVersion.split("\\.")(0)

    val version = if (runMode.isProduction) majorVersion else fullVersion
  }

}

object Settings {
  implicit def unwrap(companion: Settings.type): Settings = current

  def current = Environment.current.settings
}

object DefaultSettings extends Settings with Loggable {
  logger.info(s"Run mode is '${runMode.name}'")
}

class SettingsWrapper(delegatee: Settings) extends Settings {
  override val runMode: RunMode = delegatee.runMode

  override val userName: String = delegatee.userName

  override val hostName: String = delegatee.hostName

  override val startedAt: Long = delegatee.startedAt
}

object RunMode {
  val PRODUCTION = new RunMode("production")

  val STAGING = new RunMode("staging")

  val TEST = new RunMode("test")

  val DEVELOPMENT = new RunMode("development")

  implicit def unwrap(companion: RunMode.type): RunMode = Environment.current.settings.runMode
}

class RunMode(val name: String) {
  val isDevelopment = name == "development"

  val isTest = name == "test"

  val isStaging = name == "staging"

  val isProduction = name == "production"
}
