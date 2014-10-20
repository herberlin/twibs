/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import org.scalatest.{FunSuite, Matchers}

class UrlUtilsTest extends FunSuite with Matchers {
  test("Generate isValidUrlPath") {
    UrlUtils.isValidUrlPath("") should equal(true)
    UrlUtils.isValidUrlPath("/") should equal(true)
    UrlUtils.isValidUrlPath("//") should equal(true)
    UrlUtils.isValidUrlPath("/a") should equal(true)
    UrlUtils.isValidUrlPath("/a/b") should equal(true)
    UrlUtils.isValidUrlPath("/a//b") should equal(true)
    UrlUtils.isValidUrlPath("/a+a") should equal(true)
    UrlUtils.isValidUrlPath("/%2Fa+/b%2Fc") should equal(true)

    intercept[NullPointerException] {
      UrlUtils.isValidUrlPath(null)
    }
    UrlUtils.isValidUrlPath("/a a") should equal(false)
    UrlUtils.isValidUrlPath(" ") should equal(false)
  }

  test("Test encode") {
    intercept[NullPointerException] {
      UrlUtils.encodeUrl(null)
    }
    intercept[NullPointerException] {
      UrlUtils.decodeUrl(null)
    }
    UrlUtils.encodeUrl("/a b/c") should be("%2Fa+b%2Fc")
    UrlUtils.decodeUrl("%2Fa+b%2Fc") should be("/a b/c")
  }

  test("Create url from path only") {
    UrlUtils.createUrlWithParameters("/index.html") should equal("/index.html")
  }

  test("Create url without path") {
    UrlUtils.createUrlWithParameters("") should equal("/")
  }

  test("Create url with parameters included") {
    UrlUtils.createUrlWithParameters("/index.html?a=b") should equal("/index.html?a=b")
  }

  test("Create url with one parameter") {
    UrlUtils.createUrlWithParameters("/index.html", Map("c" -> Array("d"))) should equal("/index.html?c=d")
  }

  test("Create url with one parameter and parameters in path") {
    UrlUtils.createUrlWithParameters("/index.html?a=b", Map("c" -> Array("d"))) should equal("/index.html?a=b&c=d")
  }

  test("Create url with more parameter and parameters in path") {
    UrlUtils.createUrlWithParameters("/index.html?a=b", Map("c" -> Array("d", "e"), "f" -> List("g", "", "h"))) should equal("/index.html?a=b&c=d&c=e&f=g&f=&f=h")
  }

  test("Create url with parameters to encode") {
    UrlUtils.createUrlWithParameters("/index.html?a a=b b", Map("c c" -> Array("d d"), "f%f" -> List("g%g", " "))) should equal("/index.html?a a=b b&c+c=d+d&f%25f=g%25g&f%25f=+")
  }
}
