package twibs.util

import com.ibm.icu.text.MessageFormat
import com.ibm.icu.util.ULocale
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

trait Translator {
  def kind(kind: String): Translator

  def usage(appendedPrefixes: String*): Translator = usage(appendedPrefixes.toList)

  def usage(appendedPrefixes: List[String]): Translator

  def translate(key: String, default: => String, args: Any*): String

  def translateOrUseDefault(key: String, default: => String, args: Any*): String

  def translate(sc: StringContext, args: Any*): String = {
    sc.checkLengths(args)
    val seq = sc.parts
    val sa = seq.head.split(":\\s*", 2)
    if (sa.length != 2) throw new IllegalArgumentException("Translation string must be in form of 'key: default text'")

    val sb = new StringBuilder()
    var i = 0
    val remainingArgs = new ArrayBuffer[Any]
    val strings = sa(1) :: seq.tail.toList
    for ((string, arg) <- strings.zip(args)) {
      if (string.endsWith("#")) {
        sb.append(string.dropRight(1))
        sb.append(arg.toString)
      } else {
        remainingArgs += arg
        sb.append(string).append('{').append(i).append('}')
        i += 1
      }
    }
    sb.append(strings.last)
    translate(sa(0), sb.toString(), remainingArgs.toArray: _*)
  }
}

trait TranslationSupport {
  private final lazy val implictTranslator: Translator = translator

  def translator: Translator

  implicit def withTranslationFormatter(sc: StringContext) = new {
    def t(args: Any*): String = implictTranslator.translate(sc, args: _*)
  }
}

object Translator extends Loggable {
  implicit def unwrap(companion: Translator.type): Translator = Environment.current.translator

  implicit def withTranslationFormatter(sc: StringContext)(implicit translator: Translator = Environment.current.translator) = new {
    def t(args: Any*): String = translator.translate(sc, args: _*)
  }
}

class TranslatorResolver(locale: ULocale, configuration: Configuration) {
  val root: Translator = new TranslatorImpl("", List(""))

  protected def unresolved(key: String, default: String, args: Any*): Unit = Translator.logger.info(s"Unresolved $key: $default")

  protected def resolve(key: String): Option[String] = configuration.getString(key)

  // TODO: Implement caching
  private def resolve(prefixes: List[String], key: String, default: => String, args: Any*): String =
    prefixes.view.map(prefix => resolve(prefix + key).flatMap(msg => formatChecked(msg, args: _*))).flatten.headOption getOrElse {
      unresolved(prefixes(0) + key, default, args: _*)
      formatUnchecked(default, args: _*)
    }

  // TODO: Implement caching
  private def resolveSilent(prefixes: List[String], key: String, default: => String, args: Any*): String =
    prefixes.view.map(prefix => resolve(prefix + key).flatMap(msg => formatChecked(msg, args: _*))).flatten.headOption getOrElse {
      formatUnchecked(default, args: _*)
    }

  private def formatChecked(messageFormatString: String, args: Any*): Option[String] =
    try {
      Some(formatUnchecked(messageFormatString, args: _*))
    } catch {
      case e: IllegalArgumentException =>
        Translator.logger.error("Invalid format", e)
        None
    }

  private def formatUnchecked(messageFormatString: String, args: Any*): String = new MessageFormat(messageFormatString, locale).format(args.toArray)

  private val cache = mutable.Map[String, Translator]()

  private def getTranslator(nid: String, prefixes: => List[String]): Translator =
    cache.getOrElseUpdate(nid, new TranslatorImpl(nid, prefixes))

  private class TranslatorImpl(id: String, prefixes: List[String]) extends Translator {
    def kind(kind: String): Translator =
      if (kind.isEmpty) this
      else getTranslator(id + kind, prefixes :+ (kind + "."))

    def usage(appendedPrefixes: List[String]): Translator = appendedPrefixes.distinct.filterNot(_.isEmpty) match {
      case Nil => this
      case ps => getTranslator(id + ps.mkString, appendPrefixes(ps))
    }

    private def appendPrefixes(ps: List[String]) = prefixes.map(parent => ps.map(prefix => parent + prefix + ".")).flatten ::: prefixes

    def translate(key: String, default: => String, args: Any*): String = resolve(prefixes, key, default, args: _*)

    def translateOrUseDefault(key: String, default: => String, args: Any*): String = resolveSilent(prefixes, key, default, args: _*)
  }

}
