/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest

class GraphUtilsTest extends TwibsTest {
  test("BreadthFirstSearch") {
    class Node(var name: String, var nodes: Seq[Node] = Seq()) {
      override def toString: String = name
    }

    val leaf1 = new Node("3_1")
    val leaf2 = new Node("3_2")
    val leaf3 = new Node("3_3")
    val level2_1 = new Node("2_1", Seq(leaf1, leaf2))
    val level2_2 = new Node("2_2", Seq(leaf1, leaf3))
    val level2_3 = new Node("2_3")
    val level1_1 = new Node("1_1", Seq(level2_1))
    val level1_2 = new Node("1_2", Seq(level2_2, level2_3))
    val root = new Node("r", Seq(level1_1, level1_2))
    level2_3.nodes = Seq(root, leaf2, leaf2, leaf1)

    GraphUtils.breadthFirstSearch(root)(_.nodes).map(_.head).mkString(",") shouldBe "r,1_1,1_2,2_1,2_2,2_3,3_1,3_2,3_3"
  }
}
