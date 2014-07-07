/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import java.awt.Desktop
import java.io.File

import twibs.TwibsTest

import com.google.common.base.Charsets
import com.google.common.io.Files

class EmailBoilerPlateTest extends TwibsTest {
  test("Convert to id") {
    val file = new File("target/inkemail.html")
    Files.write(new EmailBoilerPlate().toString("BESTELL", <p>Header</p>, <p>Content</p>, <p>Footer</p>), file, Charsets.UTF_8)
    Desktop.getDesktop.open(file)
  }
}
