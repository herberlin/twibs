/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.io.File

import com.google.common.base.Charsets
import com.google.common.io.Files
import net.twibs.testutil.TwibsTest

class EmailBoilerPlateTest extends TwibsTest {
  test("Convert to id") {
    val file = new File("target/inkemail.html")
    Files.write(EmailBoilerPlate.toString("BESTELL", <p>Header</p>, <p>Content</p>, <p>Footer</p>), file, Charsets.UTF_8)
//    Desktop.getDesktop.open(file)
  }
}
