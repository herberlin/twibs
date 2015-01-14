/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import scala.collection.JavaConversions._
import scala.collection.concurrent.TrieMap

import com.typesafe.config.Config
import com.typesafe.config.ConfigException.Missing

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
}

object Configuration extends UnwrapCurrent[Configuration] {
  def current = ApplicationSettings.current.configuration
}

private[util] class ConfigurationForTypesafeConfig(config: Config) extends Configuration {
  private val cache = TrieMap[String, Any]()

  private def store[T](key: String, f: => T): Option[T] = cache.getOrElseUpdate(key, tryo(key, f)).asInstanceOf[Option[T]]

  private def tryo[T](key: String, f: => T): Option[T] = if (config.hasPath(key)) try Some(f) catch {case e: Missing => None} else None

  def getStringList(key: String) = store(key, config.getStringList(key).toList)

  def getString(key: String) = store(key, config.getString(key))

  def getBooleanList(key: String) = store(key, config.getBooleanList(key).toList.map(Boolean.unbox))

  def getBoolean(key: String) = store(key, config.getBoolean(key))

  def getIntList(key: String) = store(key, config.getIntList(key).toList.map(Int.unbox))

  def getInt(key: String) = store(key, config.getInt(key))
}
