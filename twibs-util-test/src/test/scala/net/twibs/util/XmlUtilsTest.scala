/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest

import scala.xml.NodeSeq

class XmlUtilsTest extends TwibsTest {

  import XmlUtils._

  test("Add class") {
    <span></span>
      .set("id", "1")
      .set(condition = true, "ok", "ok1")
      .set(condition = true, "ok", "ok2")
      .set(condition = false, "disabled", "disabled")
      .setIfMissing("name", "myself")
      .setIfMissing("name", "more")
      .setIfMissing(condition = true, "value", "2")
      .addClass("btn")
      .addClasses("btn" :: "btn-primary" :: Nil)
      .addClass(condition = true, "disabled")
      .addClass(condition = false, "active")
      .addClasses(condition = true, "a" :: "b" :: Nil) should be(
        <span id="1" ok="ok2" name="myself" value="2" class="btn btn-primary disabled a b"></span>)

    <span class={" btn " :: "" :: "a " :: "a" :: Nil}></span> should be(<span class="btn a"></span>)

    <span class="btn a"></span>.removeClass("btn") should equal(<span class="a"></span>)
  }

  test("Remove attribute") {
      <span a="a" b="b"/>.removeAttribute("b") should be(<span a="a" />)
      <span a="a" b="b"/>.removeAttribute("c") should be(<span a="a" b="b"/>)
  }

  test("Delete if empty") {
    <div a="a"><div></div></div>.removeIfEmpty should be( <div a="a"><div></div></div>)
    <div a="a"></div>.removeIfEmpty should be(NodeSeq.Empty)
  }
}
