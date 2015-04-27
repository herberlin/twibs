/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.testutil

import java.io.{File, FileOutputStream}
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream

import com.google.common.io.ByteStreams
import net.twibs.util.Predef._
import net.twibs.util.SystemSettings
import org.apache.commons.io.FileUtils
import org.openqa.selenium.Dimension
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}

object SeleniumDriver {
  private val driverVersion = "2.15"
  private val baseUrl = s"http://chromedriver.storage.googleapis.com/$driverVersion/chromedriver"
  private val linuxUrl = baseUrl + "_linux64.zip"
  private val windowsUrl = baseUrl + "_win32.zip"
  private val macUrl = baseUrl + "_mac32.zip"

  private lazy val service: ChromeDriverService = {
    val chromeDriverLibrary = if (SystemSettings.os.isMac)
      loadDriverFrom(macUrl, s"chromedriver_mac_$driverVersion")
    else if (SystemSettings.os.isWindows)
      loadDriverFrom(windowsUrl, s"chromedriver_win_$driverVersion")
    else
      loadDriverFrom(linuxUrl, s"chromedriver_linux_$driverVersion")
    new ChromeDriverService.Builder()
      .usingAnyFreePort()
      .usingDriverExecutable(chromeDriverLibrary)
      .withSilent(true).build()
  }

  private var driverVar: RemoteWebDriver = null

  def driver = driverVar

  def initWebDriver(): Unit = {
    service.start()
    driverVar = new RemoteWebDriver(service.getUrl, DesiredCapabilities.chrome())

    driver.manage.window.setSize(new Dimension(1024, 1024))
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
  }

  def discardWebDriver(): Unit = {
    driver.quit()
    driverVar = null
    service.stop()
  }

  def loadDriverFrom(url: String, tempFileName: String): File =
    downloadZipAndExtractFirstEntryIfFileIsMissing(new URL(url), new File(FileUtils.getTempDirectory, tempFileName))

  def downloadZipAndExtractFirstEntryIfFileIsMissing(url: URL, file: File) = {
    if (!file.exists())
      url.openStream useAndClose {
        is => new ZipInputStream(is) useAndClose {
          zis => new FileOutputStream(file).useAndClose {
            os =>
              zis.getNextEntry
              ByteStreams.copy(zis, os)
              zis.closeEntry()
              file.setExecutable(true)
          }
        }
      }
    file
  }
}
