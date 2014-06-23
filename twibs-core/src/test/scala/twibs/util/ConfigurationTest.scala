/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import twibs.TwibsTest

import com.typesafe.config.ConfigFactory

class ConfigurationTest extends TwibsTest {
  test("User configuration is loaded") {
    Configuration.getString("test.user").get should be("tester")
  }

  test("application.conf shadows reference.conf") {
    val key = "LOCALES.de.FIELD.placeholder"
    val value = "not from reference.conf"

    new ConfigurationForTypesafeConfig(ConfigFactory.load()).getString(key).get should be(value)
    new ConfigurationForTypesafeConfig(ConfigFactory.load() withFallback ConfigFactory.load(getClass.getClassLoader)).getString(key).get should be(value)
    Configuration.getString(key).get should be(value)
  }
}
