/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.testutil

import net.twibs.util.Logger
import org.scalatest.{FunSuite, Matchers}

trait TwibsTest extends FunSuite with Matchers {
  Logger.init()
}
