/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.io.File

import net.twibs.testutil.TwibsTest
import org.apache.tika.Tika

class TikaTest extends TwibsTest {
  test("Tika mimetype for font") {
    new Tika().detect(new File("src/test/webapp/glyphicons-halflings-regular.woff")) should be("application/font-woff")
  }
}
