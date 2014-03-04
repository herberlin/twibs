package twibs.util

import com.ibm.icu.util.ULocale
import twibs.TwibsTest

class EnvironmentSettingsTwibsAndRunModeTest extends TwibsTest {
  test("Override settings values") {
    val default = Settings.current
    val sys = new Settings() {
      override val runMode: RunMode = RunMode.PRODUCTION
    }

    Settings.current should be(default)
    new Environment() {
      override lazy val settings = sys
    } use {
      Settings.current should be(sys)
      Settings.Twibs.version should be("5")
    }
    Settings.current should be(default)
    Settings.Twibs.version should be("5.0")
  }

  test("Default Mode") {
    RunMode.isProduction should beFalse
    RunMode.isStaging should beFalse
    RunMode.isDevelopment should beFalse
    RunMode.isTest should beTrue
  }

  test("Default loaded configuration is test") {
    Configuration.getString("v").get should be("test")
    Configuration.translators(ULocale.GERMAN).translate("v", "") should be("test")
  }

  test("Configuration for production mode is default") {
    new Environment() {
      override lazy val settings = new Settings() {
        override val runMode = RunMode.PRODUCTION
      }
    } use {
      Configuration.getString("v").get should be("default")
      Configuration.translators(ULocale.GERMAN).translate("v", "") should be("default")
    }
  }

  test("Configuration for host overrides and joins configuration") {
    new Environment() {
      override lazy val settings = new Settings() {
        override val hostName = "twibs-test-host"
      }
    } use {
      Configuration.locales should be(List(ULocale.GERMAN, ULocale.ENGLISH, ULocale.FRENCH))
      Configuration.getString("v").get should be("host")
      Configuration.getString("r").get should be("test")
      Configuration.translators(ULocale.GERMAN).translate("v", "") should be("host")
    }
    Configuration.translators(ULocale.GERMAN).translate("v", "") should be("test")
  }
}
