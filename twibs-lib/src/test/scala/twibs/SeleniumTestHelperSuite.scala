package twibs

import org.scalatest.BeforeAndAfterAll

trait SeleniumTestHelperSuite extends TwibsTest with SeleniumTestUtils with BeforeAndAfterAll {
  override protected def beforeAll(): Unit =
    SeleniumDriver.initWebDriver()

  override protected def afterAll(): Unit =
    SeleniumDriver.discardWebDriver()
}
