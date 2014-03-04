package twibs.util

import java.io.{FileNotFoundException, InputStreamReader}
import org.mozilla.javascript._

object LessCssParserFactory extends Loggable {
  private val scope = inContext {
    cx =>
      cx.setOptimizationLevel(9)

      logger.debug(s"Implementation version: ${cx.getImplementationVersion}")

      val scope = cx.initStandardObjects()
      ("env" :: "cssmin" :: "less" :: "parser" :: Nil)
        .map(fileName => getClass.getClassLoader.getResource(s"META-INF/less-css-parser/$fileName.js"))
        .map(url => IOUtils.using(new InputStreamReader(url.openStream())) {cx.evaluateReader(scope, _, url.toString, 1, null)})
      scope
  }

  private class Adapter(doLoad: (String) => String) {
    def load(path: String): String =
      try doLoad(path) catch {
        case e: FileNotFoundException => throw new FileNotFoundException(path)
      }

    def debug(string: String): Unit = logger.debug(string)
  }

  class LessCssParser private[LessCssParserFactory](loadFunc: (String) => String) {
    /** This method is synchronized on LessCssParserFactory because the less compiler in javascript is not thread safe */
    def parse(path: String, compress: Boolean = true, optimization: Int = 1) = scope.synchronized {
      inContext {
        cx =>
          scope.put("twibs", scope, Context.javaToJS(new Adapter(loadFunc), scope))

          val parseLessCss = scope.get("parseLessCss", scope).asInstanceOf[Function]
          try {
            Context.call(null, parseLessCss, scope, scope, List(s"@import '$path';", path, compress, optimization).map(_.asInstanceOf[AnyRef]).toArray).toString
          } catch {
            case e: JavaScriptException => throw new LessCssParserException(e)
            case e: Exception => throw new LessCssParserException(e.getMessage)
          }
      }
    }
  }

  def createParser(loadFunc: (String) => String) = new LessCssParser(loadFunc)

  private def inContext[R](f: (Context) => R): R = try f(Context.enter()) finally Context.exit()
}

class LessCssParserException(message: String) extends Exception(message) {
  def this(exception: JavaScriptException) = this {
    val value: Scriptable = exception.getValue.asInstanceOf[Scriptable]
    val typ = ScriptableObject.getProperty(value, "type").toString + " Error"
    val message = ScriptableObject.getProperty(value, "message").toString
    def fileName = getPropertyOption("filename").map(_.toString) getOrElse ""
    def line = getPropertyOption("line").map(_.asInstanceOf[Double].intValue) getOrElse -1
    def column = getPropertyOption("column").map(_.asInstanceOf[Double].intValue) getOrElse -1
    def extract = getPropertyOption("extract").map(_.asInstanceOf[NativeArray]).map(extract =>
      for (i <- 0 until extract.getLength.toInt if extract.get(i, extract).isInstanceOf[String])
      yield extract.get(i, extract).asInstanceOf[String].replace("\t", " ")).filterNot(_.isEmpty).map(" near\n" + _.mkString("\n")) getOrElse ""

    def getPropertyOption(name: String): Option[Any] = {
      val property: AnyRef = ScriptableObject.getProperty(value, name)
      if (property != null && property != UniqueTag.NOT_FOUND) Some(property) else None
    }

    if (exception.getMessage.contains("JavaException")) message
    else s"$typ: $message in '$fileName' (line $line, column $column)$extract"
  }
}
