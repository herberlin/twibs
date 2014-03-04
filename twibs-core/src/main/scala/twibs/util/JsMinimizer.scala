package twibs.util

import com.google.javascript.jscomp._
import scala.collection.JavaConverters._

class JsMinimizer {
  def minimize(path: String, javascript: String) = {
    val compiler = new Compiler()
    val modules = List(SourceFile.fromCode(path, javascript)).asJava
    compiler.compile(externs, modules, options)

    if (compiler.hasErrors) {
      val formatter = new LightweightMessageFormatter(compiler)

      throw new JsMinimizerException(compiler.getErrors.toList.map(_.format(CheckLevel.ERROR, formatter)).mkString("\n"))
    }
    compiler.toSource
  }

  private def options = {
    val ret = new CompilerOptions()
    CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(ret)
    ret.setErrorFormat(ErrorFormat.MULTILINE)
    ret.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC, CheckLevel.OFF)
    ret.setPreferSingleQuotes(true)
    ret.setPrettyPrint(prettyPrint)
    ret
  }

  private def externs = CommandLineRunner.getDefaultExterns

  def prettyPrint: Boolean = RunMode.isDevelopment || RunMode.isTest
}

class JsMinimizerException(message: String) extends Exception(message)
