/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.io.{ObjectOutputStream, ObjectInputStream, ByteArrayInputStream, ByteArrayOutputStream}

import net.twibs.testutil.TwibsTest
import net.twibs.util.Predef._

import scala.concurrent.duration._

class MemoTest extends TwibsTest {
  test("Test timeout cache") {
    var x = 0

    val cache = Memo {
      x = x + 1
      x
    }.recomputeAfter(40 millis)

    cache() shouldBe 1
    cache() shouldBe 1
    cache.reset()
    cache() shouldBe 2
    Thread.sleep(41)
    cache() shouldBe 3
  }

  test("Serializing Memo") {
    val org = Memo(12)
    org() shouldBe 12
    val baos = new ByteArrayOutputStream
    new ObjectOutputStream(baos).useAndClose(_.writeObject(org))
    val memo = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray)).useAndClose { _.readObject.asInstanceOf[Memo[Int]]}
    memo() shouldBe 12
  }

  test("Serializing WeakMemo") {
    val org = WeakMemo("A")
    org() shouldBe "A"
    val baos = new ByteArrayOutputStream
    new ObjectOutputStream(baos).useAndClose(_.writeObject(org))
    val memo = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray)).useAndClose { _.readObject.asInstanceOf[Memo[String]]}
    memo() shouldBe "A"
  }
}
