package twibs.web

import com.ibm.icu.util.ULocale
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import org.apache.tika.Tika
import scala.concurrent.duration._
import twibs.TwibsTest
import twibs.util.Parameters._
import twibs.util.{RequestSettings, Parameters}

class ResponderTest extends TwibsTest {
  private implicit def toRequest(pathArg: String): Request = toRequest(pathArg, "localhost", Parameters())

  private def toRequest(pathArg: String, theDomain: String, theParameters: Parameters, theUseCache: Boolean = true): Request =
    new StaticRequest(pathArg, theDomain, theParameters, theUseCache)

  val defaultFileResponder: Responder = new FileResponder(new File("src/test/webapp/default"))
  val www1FileResponder: Responder = new FileResponder(new File("src/test/webapp/www1")) :: defaultFileResponder :: Nil
  val jsMergerResponder = new JsMergerResponder(www1FileResponder)

  test("default file responder") {
    defaultFileResponder.respond("dir") should be('empty)
    defaultFileResponder.respond("missing.txt") should be('empty)

    load(defaultFileResponder, "default.txt") should be("This is the default file")
    load(defaultFileResponder, "file.txt") should be("This is a file")
  }

  test("www1 file responder") {
    load(www1FileResponder, "default.txt") should be("This is the default file")
    load(www1FileResponder, "file.txt") should be("This is a www1 file")

    www1FileResponder.respond("/simple.css").get.mimeType should be("text/css")
  }

  test("javascript merging") {
    load(jsMergerResponder, "/includeSimpleTwice.js") should be(
      """function f() {
        |    return "$www1";
        |}
        |;
        |
        |// Included file 'does_not_exists.js' does not exist
        |
        | function f() {
        |    return "$www1";
        |}
        |;
        |
        |  // includeFile("simple.js")
        | """.stripMargin.dropRight(1))

    load(jsMergerResponder, "/simple.js") should be( """function f() {
                                                       |    return "$www1";
                                                       |}
                                                       | """.stripMargin.dropRight(1))
  }

  test("javascript minimizing") {
    val responder = new JsMinimizerResponder(jsMergerResponder)
    load(responder, "/includeSimpleTwice.js") should be(
      """function f() {
        |  return'$www1';
        |}
        |function f() {
        |  return'$www1';
        |}
        |;""".stripMargin)

    load(responder, "/withErrors.js") should be("// /withErrors.js:2: ERROR - Parse error. missing ) after argument list\n//     return f(\"www1\";\n//                    ^\n// ")

    val response = responder.respond("/includeSimpleTwice.js").get
    response.isModified should beFalse
    new File("src/test/webapp/www1/simple.js").setLastModified(System.currentTimeMillis)
    response.isModified should beTrue
  }

  test("css minimizing") {
    val responder = new LessCssParserResponder(www1FileResponder)
    load(responder, "/simple.css") should be(
      """a{font-size:12px}b{color:red}""".stripMargin)
    load(responder, "/withErrorsCss.css") should be( """// Parse Error: missing closing `}` in '/withErrorsCss.less' (line 2, column 2) near
                                                       |// /* What */
                                                       |// a {
                                                       |//     c:::f.?=)/)/%&%&$)(/=)ont-size: 12px;""".stripMargin)
    responder.respond("/simple.css").get.mimeType should be("text/css")
  }

  test("html minimizing") {
    val responder = new HtmlMinimizerResponder(www1FileResponder)
    load(responder, "/a.html") should be( """<a c="d" e="f"><b> </b></a>""")

    load(responder, "/withErrors.html") should be( """<html <html <bo></</ bo>""")

    responder.respond("/a.html").get.mimeType should be(MimeTypes.HTML)
  }

  test("less compiling include failed") {
    val responder = new LessCssParserResponder(www1FileResponder)
    responder.respond("/import-missing-variable.css").get.asString should equal(
      """// Name Error: variable @color-red is undefined in '/missing-variable.less' (line 2, column 9) near
        |// a {
        |//   color: @color-red; // Missing variable
        |// }""".stripMargin)
  }

  test("simple less compiling") {
    val responder = new LessCssParserResponder(www1FileResponder)
    responder.respond("/simple.css").get.asString should equal("a{font-size:12px}b{color:red}")
  }

  test("static not found responder") {
    val responder = new StaticNotFoundResponder(www1FileResponder)
    load(responder, "/not-found.txt") should be("Not found")
    responder.respond("/not-found.txt").get should be(anInstanceOf[NotFoundResponse])
  }

  test("static error responder") {
    val responder = new StaticErrorResponder(new LessCssParserResponder(www1FileResponder) :: www1FileResponder :: Nil)
    load(responder, "/withErrors.css") should be( """// Parse Error: missing closing `}` in '/withErrors.less' (line 2, column 2) near
                                                    |// /* What */
                                                    |// a {
                                                    |//     c:::f.?=)/)/%&%&$)(/=)ont-size: 12px;""".stripMargin)
    responder.respond("/withErrors.css").get should be(anInstanceOf[ErrorResponse])
  }

  test("recursive error responder") {
    load(www1FileResponder, "/l1/l2/withErrors.less") should be("/* What */\na {\n    c:::f.?=)/)/%&%&$)(/=)ont-size: 12px;")
    val contentResponder = new LessCssParserResponder(www1FileResponder)

    load(contentResponder, "/l1/l2/withErrors.css") should be( """// Parse Error: missing closing `}` in '/l1/l2/withErrors.less' (line 2, column 2) near
                                                                 |// /* What */
                                                                 |// a {
                                                                 |//     c:::f.?=)/)/%&%&$)(/=)ont-size: 12px;""".stripMargin)

    val responder = new ErrorResponder(contentResponder, www1FileResponder)
    load(responder, "/simple.css") should be("a{font-size:12px}b{color:red}")
  }

  test("recursive not found responder") {
    val contentResponder: Responder = new LessCssParserResponder(www1FileResponder) :: www1FileResponder :: Nil
    contentResponder.respond("/l1/l2/does_not_exist.html") should be('empty)

    val responder = new NotFoundResponder(contentResponder, contentResponder)
    load(responder, "/l1/l2/does_not_exist.html") should be("l2 404 default")
    load(responder, "/l1/does_not_exist.html") should be("l1 404 www1")
    responder.respond("/l1/l2/does_not_exist.html").get should be(anInstanceOf[NotFoundResponse])
  }

  test("hide responder") {
    val responder = new HideResponder(www1FileResponder)
    load(responder, "/file.txt") should be("This is a www1 file")

    load(www1FileResponder, "/_file.txt") should be("This file is hidden")
    responder.respond("/_file.txt") should be('empty)
  }

  test("redirect responder") {
    val responder = new IndexRedirectResponder()
    responder.respond("/file.txt") should be('empty)
    load(responder, "/") should be("/index.html")
    load(responder, "/_RedirectToIndex") should be("/index.html")
    responder.respond("/_RedirectToIndex").get should be(anInstanceOf[RedirectResponse])
  }

  test("Relative path") {
    toRequest("/a/b/c/test.html").relative("../../a.html").path should be("/a/a.html")
  }

  test("Request key equality") {
    toRequest("/test.html").relative("x.html").cacheKey should equal(toRequest("/x.html").cacheKey)
    toRequest("/test.html").relative("x.html").cacheKey should not equal toRequest("/y.html").cacheKey
    toRequest("/a/b/c/test.html").relative("./../c/d/x.html").cacheKey should equal(toRequest("/a/b/c/d/x.html").cacheKey)
    toRequest("/a/b/c/test.html").relative("/d/x.html").cacheKey should equal(toRequest("/d/x.html").cacheKey)
    toRequest("/test.html", "localhost", Map("sEcho" -> List("1"))).relative("x.html").cacheKey should equal(toRequest("/x.html", "localhost", Map("sEcho" -> List("1"))).cacheKey)
    toRequest("/test.html", "localhost", Map("sEcho" -> List("2"))).relative("x.html").cacheKey should not equal toRequest("/x.html", "localhost", Map("sEcho" -> List("1"))).cacheKey
    toRequest("/test.html", "localhost", Map("sEcho" -> List("1"))).relative("x.html").cacheKey should not equal toRequest("/x.html", "otherhost", Map("sEcho" -> List("1"))).cacheKey
  }

  test("unique cache") {
    val responder = new UniqueCacheResponder(www1FileResponder)
    val first = responder.respond("/file.txt")
    responder.respond("/file.txt").get should be theSameInstanceAs first.get
    responder.respond("/file.txt").get should be theSameInstanceAs first.get
    responder.respond("/file.txt").get should be theSameInstanceAs first.get
    new File("src/test/webapp/www1/file.txt").setLastModified(System.currentTimeMillis)
    responder.respond("/file.txt").get should not be theSameInstanceAs(first.get)

    val baos = new ByteArrayOutputStream()
    responder.save(baos)
    val is = new ByteArrayInputStream(baos.toByteArray)

    val responder2 = new UniqueCacheResponder(new FileResponder(new File("/tmp/invalid/dir")))
    responder2.respond("/file.txt") should be('empty)
    responder2.load(is)
    load(responder2, "/file.txt") should be("This is a www1 file")
  }

  test("memory cache") {
    www1FileResponder.respond("/file.txt").get should not be 'inMemory
    val responder = new MemoryCachingResponder(www1FileResponder)
    val first = responder.respond("/file.txt")
    first.get should be('inMemory)
    new MemoryCachingResponder(www1FileResponder, maxFileSizeInBytes = 10).respond("/file.txt").get should not be 'inMemory
  }

  test("expiring cache") {
    val responder = new ExpiringCacheResponder(www1FileResponder, 100 millis)
    val first = responder.respond("/file.txt")
    responder.respond("/file.txt").get should be theSameInstanceAs first.get
    responder.respond("/file.txt").get should be theSameInstanceAs first.get
    responder.respond("/file.txt").get should be theSameInstanceAs first.get
    Thread.sleep(100)
    responder.respond("/file.txt").get should not be theSameInstanceAs(first.get)
  }

  test("expiring cache with no cache request") {
    val responder = new ExpiringCacheResponder(www1FileResponder, 100 seconds)
    val first = responder.respond("/file.txt")
    responder.respond("/file.txt").get should be theSameInstanceAs first.get
    responder.respond("/file.txt").get should be theSameInstanceAs first.get
    responder.respond(toRequest("/file.txt", "localhost", Parameters(), theUseCache = false)).get should not be theSameInstanceAs(first.get)
  }

  def load(responder: Responder, path: String) = {
    val responseOption = responder.respond(path)
    responseOption should not be 'empty
    responseOption.get.asString
  }

  test("class loader responder") {
    val responder = new ClassLoaderResponder(getClass.getClassLoader, "/db/migration")
    responder.getResourceOption("/V1__empty.sql") should not be 'empty
  }

  test("Tika mimetype for font") {
    new Tika().detect(getClass.getClassLoader.getResource("META-INF/resources/inc/bootstrap-fonts/glyphicons-halflings-regular.woff")) should be("application/font-woff")
  }
}
