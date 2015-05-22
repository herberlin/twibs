/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.testutil

import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.{ExpectedConditions, ExpectedCondition, WebDriverWait}
import org.openqa.selenium.{By, NoSuchElementException, NoSuchWindowException, WebDriver}

trait SeleniumTestUtils {
  implicit def convert(selector: String): By = By.cssSelector(selector)

  def baseUrl: String

  def driver = SeleniumDriver.driver

  def open(path: String): Unit = driver.get(baseUrl + path)

  def selectChosen(selector: String, entry: Int) = {
    click(s"$selector + div.chosen-container .chosen-single")
    findLazy(s"$selector + div.chosen-container .chosen-results > li:nth-child($entry)").click()
  }

  def enterAndBlur(by: By, text: String): Unit = {
    enter(by, text)
    click("body")
    Thread.sleep(300)
  }

  def enter(by: By, text: String) = {
    val elem = click(by)
    elem.clear()
    elem.sendKeys(text)
    elem
  }

  def upload(by: By, text: String) = {
    val elem = find(by)
    elem.sendKeys(text + "\t")
    elem
  }

  def find(by: By) = driver.findElement(by)

  def findLazy(by: By) = {
    new WebDriverWait(driver,1).until(ExpectedConditions.visibilityOfElementLocated(by))
    find(by)
  }


  def click(by: By) = {
    val elem = find(by)
    new Actions(driver).moveToElement(elem).click().build().perform()
    elem
  }

  def waitForModal(): Unit = findCompletelyVisible(".modal.in")

  def findCompletelyVisible(by: By) = {
    new WebDriverWait(driver, 5).until(
      new ExpectedCondition[Boolean]() {
        override def apply(webDriver: WebDriver) = find(by).getCssValue("opacity").equals("1")
      }
    )
    find(by)
  }

  def waitForWindowClosed(): Unit = while (windowIsOpen()) {Thread.sleep(100)}

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