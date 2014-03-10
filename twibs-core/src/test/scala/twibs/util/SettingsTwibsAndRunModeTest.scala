package twibs.util

import com.ibm.icu.util.ULocale
import twibs.TwibsTest

class SettingsTwibsAndRunModeTest extends TwibsTest {
  test("Override settings values") {
    val sys = new SystemSettings() {
      override val runMode: RunMode = RunMode.PRODUCTION
    }
    sys.Twibs.version should be("0")
    SystemSettings.Twibs.version should be("0.5")
  }

  test("Default Mode") {
    RunMode.isProduction should beFalse
    RunMode.isStaging should beFalse
    RunMode.isDevelopment should beFalse
    RunMode.isTest should beTrue
  }

  test("Default loaded configuration is test") {
    Configuration.getString("v").get should be("test")
    ApplicationSettings.translators(ULocale.GERMAN).translate("v", "") should be("test")
  }

  test("Configuration for production mode is default") {
    val config = ConfigurationForTypesafeConfig.forSettings(ApplicationSettings.DEFAULT_NAME, new SystemSettings() {
      override val runMode = RunMode.PRODUCTION
    })

    config.getString("v").get should be("default")
    new TranslatorResolver(ULocale.GERMAN, config).root.translate("v", "") should be("default")
  }

  test("Configuration for host overrides and joins configuration") {
    val config = ConfigurationForTypesafeConfig.forSettings(ApplicationSettings.DEFAULT_NAME, new SystemSettings() {
      override val hostName = "twibs-test-host"
    })

    val applicationSettings = new ApplicationSettings(ApplicationSettings.DEFAULT_NAME) {
      override lazy val configuration: Configuration = config
    }

    applicationSettings.locales should be(List(ULocale.GERMAN, ULocale.ENGLISH, ULocale.FRENCH))
    config.getString("v").get should be("host")
    config.getString("r").get should be("test")
    applicationSettings.translators(ULocale.GERMAN).translate("v", "") should be("host")

    ApplicationSettings.translators(ULocale.GERMAN).translate("v", "") should be("test")
  }

  test("Find configured applications") {
    SettingsFactory.applicationSettingsMap.toList.map(_._1).sorted should be(List("t1", "t2"))
  }

  test("Find application settings by path") {
    SettingsFactory.applicationSettingsForPath("/content/t1").name should be ("t1")
    SettingsFactory.applicationSettingsForPath("/content/t").name should be (ApplicationSettings.DEFAULT_NAME)
  }
}
