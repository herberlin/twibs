/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import com.ibm.icu.util.ULocale
import net.twibs.testutil.TwibsTest

class SettingsTest extends TwibsTest {
  test("Check version for run modes") {
    val sys = SystemSettings.copy(runMode = RunMode.PRODUCTION)
    sys.version should be(sys.majorVersion)
    SystemSettings.version should be(sys.fullVersion)
  }
  SystemSettings.runMode.isTest shouldBe true

  test("Default Mode") {
    RunMode.isProduction shouldBe false
    RunMode.isStaging shouldBe false
    RunMode.isDevelopment shouldBe false
    RunMode.isTest shouldBe true
  }

  test("Default loaded configuration is Default application") {
    ApplicationSettings.translators(ULocale.GERMAN).translate("message", "") should be("Runmode test Host unknown User tester Lang German (test) App Default")
  }

  test("Configuration for production mode is default") {
    SystemSettings.default.copy(runMode = RunMode.PRODUCTION).use {
      ApplicationSettings.translators(ULocale.GERMAN).translate("message", "") should be("Runmode production Host unknown User tester Lang German App Default")
    }
  }

  test("Configuration for host overrides and joins configuration") {
    SystemSettings.default.copy(hostName = "twibs-test-host").use {
      ApplicationSettings.translators(ULocale.GERMAN).translate("message", "") should be("Runmode test Host testhost User tester Lang German (test on testhost) App Default")
    }
  }

  test("Different locales for applications") {
    SystemSettings.default.use {
      ApplicationSettings.locales should be(List(ULocale.GERMAN, ULocale.ENGLISH, ULocale.FRENCH))
      SystemSettings.applicationSettings("t1").locales should be(List(ULocale.GERMAN))
      SystemSettings.applicationSettings("t2").locales should be(List(SystemSettings.default.locale))
    }
  }

  test("Find configured applications") {
    SystemSettings.default.use {
      SystemSettings.applicationSettings.values.map(_.name).toList.sorted should be(List("default", "t1", "t2"))
    }
  }

  test("Find application settings by path") {
    SystemSettings.default.use {
      SystemSettings.applicationSettingsForPath("/content/t1").name should be("t1")
      SystemSettings.applicationSettingsForPath("/content/t").name should be(ApplicationSettings.DEFAULT_NAME)
    }
  }

  test("Validate context path") {
    intercept[NullPointerException] {
      Request.requireValidContextPath(null)
    }
    intercept[IllegalArgumentException] {
      Request.requireValidContextPath("/")
    }.getMessage should include("not be /")
    intercept[IllegalArgumentException] {
      Request.requireValidContextPath("nix")
    }.getMessage should include("start with /")
    intercept[IllegalArgumentException] {
      Request.requireValidContextPath("/x x")
    }.getMessage should include("invalid")
  }
}
