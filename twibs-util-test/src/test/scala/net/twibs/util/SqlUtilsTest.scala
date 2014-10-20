/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest

class SqlUtilsTest extends TwibsTest {
  test("Escape for like") {
    SqlUtils.escapeForLike( """\%_""") should be( """\\\%\_""")
  }
}
