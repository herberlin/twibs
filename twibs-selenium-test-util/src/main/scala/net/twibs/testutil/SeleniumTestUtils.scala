/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.testutil

import org.openqa.selenium.support.ui.{ExpectedCondition, WebDriverWait}
import org.openqa.selenium.{NoSuchWindowException, NoSuchElementException, By, WebDriver}

trait SeleniumTestUtils {
  def baseUrl: String

  def driver = SeleniumDriver.driver

  protected def open(path: String): Unit = driver.get(baseUrl + path)

  protected def click(selector: String) = {
    val elem = find(selector)
    elem.click()
    elem
  }

  protected def selectChosen(selector: String, entry: Int) = {
    find(s"$selector + div.chosen-container .chosen-single").click()
    find(s"$selector + div.chosen-container .chosen-results > li:nth-child($entry)").click()
  }

  protected def enterAndBlur(selector: String, text: String): Unit = {
    enter(selector, text)
    click("body")
    Thread.sleep(300)
  }

  protected def enter(selector: String, text: String): Unit = {
    val elem = click(selector)
    elem.clear()
    elem.sendKeys(text)
  }

  protected def find(selector: String) = driver.findElement(By.cssSelector(selector))

  protected def waitForModal(): Unit =
    findCompletelyVisible(".modal.in")

  protected def findCompletelyVisible(selector: String) = {
    new WebDriverWait(driver, 5).until(
      new ExpectedCondition[Boolean]() {
        override def apply(webDriver: WebDriver) = find(selector).getCssValue("opacity").equals("1")
      }
    )
    find(selector)
  }

  def waitForWindowClosed(): Unit =
    while (windowIsOpen()) {
      Thread.sleep(100)
    }

  def windowIsOpen() =
    try {
      find(".will-never-appear")
      true
    } catch {
      case e: NoSuchElementException => true
      case e: NoSuchWindowException => false
    }
}

abstract class AbstractSeleniumTestUtils extends SeleniumTestUtils