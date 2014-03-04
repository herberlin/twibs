package twibs.util

import twibs.TwibsTest

class SqlUtilsTest extends TwibsTest {
  test("Escape for like") {
    SqlUtils.escapeForLike( """\%_""") should be( """\\\%\_""")
  }
}
