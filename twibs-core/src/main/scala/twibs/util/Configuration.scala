/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import scala.collection.JavaConversions._
import scala.collection.concurrent.TrieMap

import com.ibm.icu.util.ULocale
import com.typesafe.config.ConfigException.Missing
import com.typesafe.config.{Config, ConfigFactory}

trait Configuration {
  def getStringList(key: String): Option[List[String]]

  def getStringList(key: String, default: List[String]): List[String] = getStringList(key) getOrElse default

  def getString(key: String): Option[String]

  def getString(key: String, default: String): String = getString(key) getOrElse default

  def getBoolean(key: String): Option[Boolean]

  def getBoolean(key: String, default: Boolean): Boolean = getBoolean(key) getOrElse default

  def getBooleanList(key: String): Option[List[Boolean]]

  def getBooleanList(key: String, default: List[Boolean]): List[Boolean] = getBooleanList(key) getOrElse default

  def getInt(key: String): Option[Int]

  def getInt(key: String, default: Int): Int = getInt(key) getOrElse default

  def getIntList(key: String): Option[List[Int]]

  def getIntList(key: String, default: List[Int]): List[Int] = getIntList(key) getOrElse default

  def configurationForLocale(locale: ULocale): Configuration
}

object Configuration {
  implicit def unwrap(companion: Configuration.type): Configuration = current

  def current = ApplicationSettings.current.configuration
}

class ConfigurationForTypesafeConfig(config: Config) extends Configuration {
  private val cache = TrieMap[String, Any]()

  private def store[T](key: String, f: => T): Option[T] = cache.getOrElseUpdate(key, tryo(key, f)).asInstanceOf[Option[T]]

  private def tryo[T](key: String, f: => T): Option[T] = if (config.hasPath(key)) try Some(f) catch {case e: Missing => None} else None

  def getStringList(key: String) = store(key, config.getStringList(key).toList)

  def getString(key: String) = store(key, config.getString(key))

  def getBooleanList(key: String) = store(key, config.getBooleanList(key).toList.map(Boolean.unbox))

  def getBoolean(key: String) = store(key, config.getBoolean(key))

  def getIntList(key: String) = store(key, config.getIntList(key).toList.map(Int.unbox))

  def getInt(key: String) = store(key, config.getInt(key))

  override def configurationForLocale(locale: ULocale): Configuration = new ConfigurationForTypesafeConfig(ConfigurationForTypesafeConfig.childConfig(config, "LOCALES." + locale.toString))
}

object ConfigurationForTypesafeConfig {
  def baseConfig(settings: SystemSettings = SystemSettings) = {
    def config = {
      ConfigFactory.invalidateCaches()
      configWithFallbackForOsgi
    }

    def hostConfig = childConfig(config, "HOSTS." + settings.hostName)

    childConfig(hostConfig, "RUN-MODES." + settings.runMode.name)
  }

  def forSettings(applicationName: String, settings: SystemSettings = SystemSettings) =
    new ConfigurationForTypesafeConfig(wrapWithUserConfig(childConfig(baseConfig(settings), "APPLICATIONS." + applicationName)))

  private def wrapWithUserConfig(wrapped: Config) = userConfigWithFallbackForOsgi.withFallback(wrapped)

  private def childConfig(parent: Config, path: String) = if (parent.hasPath(path)) parent.getConfig(path).withFallback(parent) else parent

  private def configWithFallbackForOsgi = ConfigFactory.load() withFallback ConfigFactory.load(getClass.getClassLoader)

  private def userConfigWithFallbackForOsgi = ConfigFactory.parseResourcesAnySyntax("user").withFallback(ConfigFactory.parseResourcesAnySyntax(getClass.getClassLoader, "user"))
}
