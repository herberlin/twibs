/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs

import java.io.File
import java.util.concurrent.TimeUnit
import org.apache.commons.lang3.SystemUtils
import org.openqa.selenium.Dimension
import org.openqa.selenium.chrome.{ChromeDriver, ChromeDriverService}

object SeleniumDriver {
  private var driverVar: ChromeDriver = null

  def driver = driverVar

  def initWebDriver(): Unit = {
    val chromeDriverLibraryPath = if (SystemUtils.IS_OS_WINDOWS) "src/test/lib/chromedriver.exe" else "src/test/lib/chromedriver"
    System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, new File(chromeDriverLibraryPath).getAbsolutePath)

    driverVar = new ChromeDriver
    driver.manage.window.setSize(new Dimension(1024, 1024))
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
  }

  def discardWebDriver(): Unit = {
    driver.quit()
    driverVar = null
  }
}
