/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import net.twibs.testutil.TwibsTest

class PathTest extends TwibsTest {
  test("Path path") {
    Path.current.string shouldBe "./"
    Path.root.string shouldBe "/"

    Path.current.dropFirstPart.string shouldBe "./"
    Path.root.dropFirstPart.string shouldBe "/"

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
    Path("/x/").dropFirstPart shouldBe Path.root
    Path("/x.html").dropFirstPart shouldBe Path.root
    Path("x/").dropFirstPart shouldBe Path.current
    Path("x.html").dropFirstPart shouldBe Path.current

    Path("/y/x/").dropFirstPart shouldBe Path("/x/")
    Path("/y/x.html").dropFirstPart shouldBe Path("/x.html")
    Path("y/x/").dropFirstPart shouldBe Path("x/")
    Path("y/x.html").dropFirstPart shouldBe Path("x.html")
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
}
