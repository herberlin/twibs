/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest

class PathTest extends TwibsTest {
  test("Path path") {
    Path.current.string shouldBe "./"
    Path.root.string shouldBe "/"

    Path.current.tail.string shouldBe "./"
    Path.root.tail.string shouldBe "/"

    Path("") shouldBe Path.current
    Path(".") shouldBe Path.current
    Path("./") shouldBe Path.current

    Path("/") shouldBe Path.root
    Path("/../b").string shouldBe "/../b"

    Path("/y/x") shouldBe Path(Seq("y", "x"), "", absolute = true)
    Path("/y/x").string shouldBe "/y/x"
    Path("/y/x.html") shouldBe Path(Seq("y", "x"), "html", absolute = true)
    Path("/y/x.html").string shouldBe "/y/x.html"
    Path("/y/x/") shouldBe Path(Seq("y", "x"), "/", absolute = true)
    Path("/y/x/").string shouldBe "/y/x/"

    Path("y/x") shouldBe Path(Seq("y", "x"), "", absolute = false)
    Path("y/x").string shouldBe "y/x"
    Path("y/x.html") shouldBe Path(Seq("y", "x"), "html", absolute = false)
    Path("y/x.html").string shouldBe "y/x.html"
    Path("y/x/") shouldBe Path(Seq("y", "x"), "/", absolute = false)
    Path("y/x/").string shouldBe "y/x/"

    Path("/x.html") shouldBe Path(Seq("x"), "html", absolute = true)
    Path("/x.html").string shouldBe "/x.html"
    Path("/x/") shouldBe Path(Seq("x"), "/", absolute = true)
    Path("/x/").string shouldBe "/x/"

    Path("x.html") shouldBe Path(Seq("x"), "html", absolute = false)
    Path("x.html").string shouldBe "x.html"
    Path("x/") shouldBe Path(Seq("x"), "/", absolute = false)
    Path("x/").string shouldBe "x/"

    Path("b/.././b/c.html").string shouldBe "b/c.html"
    Path("b/././b/c.html").string shouldBe "b/b/c.html"
    Path(".././b/c.html").string shouldBe "../b/c.html"
    Path("../b/../b/c.html").string shouldBe "../b/c.html"
  }

  test("Drop first part") {
    Path("/x/").tail shouldBe Path.root
    Path("/x.html").tail shouldBe Path.root
    Path("x/").tail shouldBe Path.current
    Path("x.html").tail shouldBe Path.current

    Path("/y/x/").tail shouldBe Path("/x/")
    Path("/y/x.html").tail shouldBe Path("/x.html")
    Path("y/x/").tail shouldBe Path("x/")
    Path("y/x.html").tail shouldBe Path("x.html")
  }

  test("Is dir") {
    Path.root.isDir shouldBe true
    Path.current.isDir shouldBe true
    Path("y/x.html").isDir shouldBe false
    Path("y/x").isDir shouldBe false
    Path("y/x/").isDir shouldBe true
    Path("/y/x.html").isDir shouldBe false
    Path("/y/x").isDir shouldBe false
    Path("/y/x/").isDir shouldBe true
  }

  test("Parent dir") {
    Path.root.parentDir shouldBe Path.root
    Path.current.parentDir shouldBe Path.current
    Path("y/x.html").parentDir shouldBe Path("y/")
    Path("y/x").parentDir shouldBe Path("y/")
    Path("y/x/").parentDir shouldBe Path("y/")
    Path("/y/x.html").parentDir shouldBe Path("/y/")
    Path("/y/x").parentDir shouldBe Path("/y/")
    Path("/y/x/").parentDir shouldBe Path("/y/")
  }

  test("Current dir") {
    Path.root.currentDir shouldBe Path.root
    Path.current.currentDir shouldBe Path.current
    Path("x.html").currentDir shouldBe Path.current
    Path("x/").currentDir shouldBe Path("x/")
    Path("/x.html").currentDir shouldBe Path.root
    Path("/x.html").currentDir shouldBe Path.root

    Path("y/x.html").currentDir shouldBe Path("y/")
    Path("y/x").currentDir shouldBe Path("y/")
    Path("y/x/").currentDir shouldBe Path("y/x/")
    Path("/y/x.html").currentDir shouldBe Path("/y/")
    Path("/y/x").currentDir shouldBe Path("/y/")
    Path("/y/x/").currentDir shouldBe Path("/y/x/")

    Path("/a/b/c.html").currentDir.string shouldBe "/a/b/"
    Path("a/b/c.html").currentDir.string shouldBe "a/b/"
  }

  test("As dir") {
    Path.root.asDir shouldBe Path.root
    Path.current.asDir shouldBe Path.current
    Path("y/x.html").asDir shouldBe Path("y/x/")
    Path("y/x").asDir shouldBe Path("y/x/")
    Path("y/x/").asDir shouldBe Path("y/x/")
    Path("/y/x.html").asDir shouldBe Path("/y/x/")
    Path("/y/x").asDir shouldBe Path("/y/x/")
    Path("/y/x/").asDir shouldBe Path("/y/x/")
  }

  test("Resolve") {
    Path.root.resolve("/b/d.html").string shouldBe "/b/d.html"
    Path.current.resolve("/b/d.html").string shouldBe "/b/d.html"
    Path("/x").resolve("/b/d.html").string shouldBe "/b/d.html"
    Path("x").resolve("/b/d.html").string shouldBe "/b/d.html"

    Path.root.resolve("../b/d.html").string shouldBe "/../b/d.html"
    Path.current.resolve("../b/d.html").string shouldBe "../b/d.html"
    Path("/x").resolve("../b/d.html").string shouldBe "/../b/d.html"
    Path("/x/").resolve("../b/d.html").string shouldBe "/b/d.html"
    Path("/x/y").resolve("../b/d.html").string shouldBe "/b/d.html"
    Path("x").resolve("../b/d.html").string shouldBe "../b/d.html"
    Path("x/").resolve("../b/d.html").string shouldBe "b/d.html"
    Path("x/y").resolve("../b/./d.html").string shouldBe "b/d.html"
  }

  test("Relativize") {
    Path.root.relativize("/b/d.html").string shouldBe "b/d.html"
    Path.current.relativize("/b/d.html").string shouldBe "/b/d.html"

    Path("x").relativize("/b/d.html").string shouldBe "/b/d.html"
    Path("/x").relativize("/b/d.html").string shouldBe "b/d.html"
    Path("/x/").relativize("/b/d.html").string shouldBe "/b/d.html"
    Path("/x/").relativize("/x/d.html").string shouldBe "d.html"
    Path("/a/b.html").relativize("/a/b/c.html").string shouldBe "b/c.html"
    Path("/a/b/").relativize("/a/b/c.html").string shouldBe "c.html"
  }

  test("Starts with") {
    Path("/x/y.html").startsWith(Path.root) shouldBe true
    Path("/x/y.html").startsWith(Path.current) shouldBe false
    Path("/x/y.html").startsWith("/x") shouldBe false
    Path("/x/y.html").startsWith("/x/") shouldBe true
    Path("/z/y.html").startsWith("/x/") shouldBe false
    Path("/x/y.html").startsWith("x/") shouldBe false

    Path("/x/").startsWith(Path.root) shouldBe true
    Path("/x/").startsWith(Path.current) shouldBe false
    Path("/x/").startsWith("/x") shouldBe false
    Path("/x/").startsWith("/x/") shouldBe true
    Path("/z/").startsWith("/x/") shouldBe false
    Path("/x/").startsWith("x/") shouldBe false

    Path("x/").startsWith(Path.root) shouldBe false
    Path("x/").startsWith(Path.current) shouldBe true
    Path("x/").startsWith("/x/") shouldBe false
    Path("x/").startsWith("x/") shouldBe true
    Path("x/").startsWith("x") shouldBe false
    Path("z/").startsWith("x/") shouldBe false

    Path("/x").startsWith("/x/") shouldBe false
  }

  test("Appending with ++") {
    Path.root ++ Path.current shouldBe Path.root
    Path.current ++ Path.root shouldBe Path.current
    Path.root ++ Path("/x/y.html") shouldBe Path("/x/y.html")
    Path.current ++ Path("/x/y.html") shouldBe Path("x/y.html")

    Path("/x/y.html") ++ Path("/a/b.html") shouldBe Path("/x/a/b.html")
    Path("/x/y.html") ++ Path.root shouldBe Path("/x/")
    Path("/x/y.html") ++ Path.current shouldBe Path("/x/")
  }

  test("Matching") {
    val m = Path("/designs/mb/twibs.css") match {
      case Path("designs" +: name +: _, _, true) if name == "twibs" => true
      case _ => false
    }
    m shouldBe false
  }
}

