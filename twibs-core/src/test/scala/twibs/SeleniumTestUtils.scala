package twibs

import org.openqa.selenium.support.ui.{ExpectedCondition, WebDriverWait}
import org.openqa.selenium.{WebDriver, By}

trait SeleniumTestUtils {
  def baseUrl: String

  def driver = SeleniumDriver.driver

  protected def open(path: String): Unit =
    driver.get(baseUrl + path)

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
    findCompletelyVisible(".modal")

  protected def findCompletelyVisible(selector: String) = {
    new WebDriverWait(driver, 5).until(
      new ExpectedCondition[Boolean]() {
        override def apply(webDriver: WebDriver) = find(selector).getCssValue("opacity").equals("1")
      }
    )
    find(selector)
  }
}

abstract class AbstractSeleniumTestUtils extends SeleniumTestUtils