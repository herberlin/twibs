package twibs.util

import com.ibm.icu.text.MessageFormat
import com.ibm.icu.util.ULocale
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

abstract class Translator(id: String, prefixes: List[String]) {
  private val cache = mutable.Map[String, String]()

  def locale: ULocale

  def kind(kind: String) =
    if (kind.isEmpty) this
    else getTranslator(id + kind, prefixes :+ (kind + "."))

  def usage(appendedPrefixes: String*): Translator = usage(appendedPrefixes.toList)

  def usage(appendedPrefixes: List[String]): Translator = appendedPrefixes.distinct.filterNot(_.isEmpty) match {
    case Nil => this
    case ps => getTranslator(id + ps.mkString, appendPrefixes(ps))
  }

  private def appendPrefixes(ps: List[String]) = prefixes.map(parent => ps.map(prefix => parent + prefix + ".")).flatten ::: prefixes

  def translate(key: String, default: => String, args: Any*): String =
    translateOrUseDefault(key, callUnresolved(key, default), args: _*)

  private def callUnresolved(key: String, default: => String) = {
    unresolved(prefixes.head + key, default)
    default
  }

  def unresolved(fullKey: String, default: String): Unit

  def translateOrUseDefault(key: String, default: => String, args: Any*): String =
    format(cache.getOrElseUpdate(key, resolve(prefixes, key) getOrElse default), args: _*)

  private def resolve(prefixes: List[String], key: String): Option[String] =
    prefixes.view.map(prefix => resolve(prefix + key)).collectFirst {case Some(x) => x}

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

  private def format(messageFormatString: String, args: Any*): String =
    try {
      new MessageFormat(messageFormatString, locale).format(args.toArray)
    } catch {
      case e: IllegalArgumentException =>
        Translator.logger.error(s"Invalid format '$messageFormatString'", e)
        throw e
    }

  protected def getTranslator(nid: String, prefixes: => List[String]): Translator

  protected def resolve(fullKey: String): Option[String]
}

trait TranslationSupport {
  private final lazy val implictTranslator: Translator = translator

  def translator: Translator

  implicit def withTranslationFormatter(sc: StringContext) = new {
    def t(args: Any*): String = implictTranslator.translate(sc, args: _*)
  }
}

object Translator extends Loggable {
  implicit def unwrap(companion: Translator.type): Translator = current

  def current: Translator = RequestSettings.current.translator

  implicit def withTranslationFormatter(sc: StringContext)(implicit translator: Translator = current) = new {
    def t(args: Any*): String = translator.translate(sc, args: _*)
  }
}

class TranslatorResolver(val locale: ULocale, configuration: Configuration) {
  val root: Translator = createTranslator("", List(""))

  private val cache = mutable.Map[String, Translator]()

  protected def resolve(fullKey: String): Option[String] = configuration.getString(fullKey)

  protected def unresolved(fullKey: String, default: String): Unit = Translator.logger.info(s"Unresolved $fullKey: $default")

  def createTranslator(id: String, prefixes: List[String]): Translator = new Translator(id, prefixes) {
    def locale = TranslatorResolver.this.locale

    protected def getTranslator(nid: String, prefixes: => List[String]): Translator = cache.getOrElseUpdate(nid, createTranslator(nid, prefixes))

    protected def resolve(fullKey: String): Option[String] = TranslatorResolver.this.resolve(fullKey)

    override def unresolved(fullKey: String, default: String): Unit = TranslatorResolver.this.unresolved(fullKey, default)
  }
}
