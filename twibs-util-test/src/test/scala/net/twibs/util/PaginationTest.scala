/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest

class PaginationTest extends TwibsTest {
  implicit def withPagingList(l: Seq[Pagination.Page]) = new {
    def asString = l.map {
      case Pagination.Page(-1, _, false, true) => ".."
      case Pagination.Page(n, _, true, false) => s"!$n!"
      case Pagination.Page(n, _, false, false) => n
      case Pagination.Page(n, _, _, true) => s"?$n?"
    }.mkString("")
  }

  test("Pages") {
    new Pagination(0, 0, 0, 0).pages.asString should equal("?0?!0!?0?")
    new Pagination(0, 10, 100, 10).pages.asString should equal("?0?!0!?0?")
    new Pagination(100, 11, 11, 10).pages.asString should equal("00!10!?10?")
    new Pagination(20, 1000, 10000, 10).pages.asString should equal("10010!20!..99030")
    new Pagination(30, 70, 100, 10).pages.asString should equal("200..!30!..6040")
    new Pagination(40, 70, 100, 10).pages.asString should equal("300..!40!506050")
    new Pagination(200, 70, 100, Int.MaxValue).pages.asString should equal("?0?!0!?0?")
    new Pagination(200, 70, 100, 10).pages.asString should equal("500..4050!60!?60?")
  }

  test("Pagination") {
    new Pagination(0, 10, 20, 10).currentPageNumber should be(0)
    new Pagination(0, 10, 20, 10).pageCount should be(1)
    new Pagination(9, 11, 20, 10).currentPageNumber should be(0)
    new Pagination(10, 11, 20, 10).currentPageNumber should be(1)
    new Pagination(10, 11, 20, 10).pageCount should be(2)

    new Pagination(-1, -1, -1, -1).pageCount should be(1)
    new Pagination(-1, -1, -1, -1).firstElementNumber should be(0)
    new Pagination(-1, -1, -1, -1).pageSize should be(1)
    new Pagination(-1, -1, -1, -1).displayedElementCount should be(0)
    new Pagination(-1, -1, -1, -1).totalElementCount should be(0)
  }
}
