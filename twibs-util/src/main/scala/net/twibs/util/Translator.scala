/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import com.ibm.icu.text.MessageFormat
import com.ibm.icu.util.ULocale

import scala.collection.concurrent.TrieMap
import scala.collection.convert.decorateAsJava._
import scala.collection.mutable.ArrayBuffer
import scala.xml.Unparsed

abstract class Translator(id: String, usages: List[String], kinds: List[String]) {
  private val cache = TrieMap[String, String]()

  def locale: ULocale

  def prefixes = usages ::: kinds

  def kind(kind: String) =
    if (kind.isEmpty) this
    else getTranslator(id + kind, usages, (kind + ".") :: kinds)

  def usage(appendedPrefixes: String*): Translator = usage(appendedPrefixes.toList)

  def usage(appendedPrefixes: List[String]): Translator = appendedPrefixes.distinct.filterNot(_.isEmpty) match {
    case Nil => this
    case ps => getTranslator(id + ps.mkString, appendPrefixes(usages, ps), appendPrefixes(kinds, ps))
  }

  private def appendPrefixes(from: List[String], ps: List[String]) = from.flatMap(parent => ps.map(prefix => parent + prefix + ".")) ::: from

  def translate(key: String, default: => String, args: Any*): String =
    translateOrUseDefault(key, callUnresolved(key, default), args: _*)

  private def callUnresolved(key: String, default: => String) = {
    unresolved(prefixes.head + key, default)
    default
  }

  def unresolved(fullKey: String, default: String): Unit

  def translateOrUseDefault(key: String, default: => String, args: Any*): String = {
    assert(!key.contains("."), s"Keys must not contain any dot ('.'): $key")
    format(cache.getOrElseUpdate(key, resolve(prefixes, key) getOrElse default), args: _*)
  }

  private def resolve(prefixes: List[String], key: String): Option[String] =
    prefixes.view.map(prefix => resolve(prefix + key)).collectFirst { case Some(x) => x }

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
    val l = sa(0).split("\\.").toList.reverse
    val (t, key) = l match {
      case List(k) => (this, k)
      case k :: tail => (tail.foldRight(this)((u, t) => t.usage(u)), k)
      case _ => throw new IllegalStateException("Nil")
    }
    t.translate(key, sb.toString(), remainingArgs.toArray: _*)
  }

  private def format(messageFormatString: String, args: Any*): String =
    try {
      val mf = new MessageFormat(messageFormatString.replaceAll( """\{\{([0-9]+)\}\s*,""", "{$1,"), locale)
      args match {
        case Seq(m: Map[_, _]) => mf.format(m.asJava)
        case _ => mf.format(args.toArray)
      }
    } catch {
      case e: IllegalArgumentException =>
        TranslatorLogger.logger.error(s"Invalid format '$messageFormatString'", e)
        throw e
    }

  protected def getTranslator(nid: String, usages: => List[String], kinds: => List[String]): Translator

  protected def resolve(fullKey: String): Option[String]
}

trait TranslationSupport {
  def translator: Translator

  private lazy val cachedTranslator = translator

  implicit def withTranslationFormatter(sc: StringContext) = new {
    def t(args: Any*): String = cachedTranslator.translate(sc, args: _*)

    def warn(args: Any*): Message = Message.warning(Unparsed(cachedTranslator.translate(sc, args: _*)))

    def info(args: Any*): Message = Message.info(Unparsed(cachedTranslator.translate(sc, args: _*)))

    def danger(args: Any*): Message = Message.danger(Unparsed(cachedTranslator.translate(sc, args: _*)))

    def success(args: Any*): Message = Message.success(Unparsed(cachedTranslator.translate(sc, args: _*)))
  }
}

object Translator extends UnwrapCurrent[Translator] {
  def current: Translator = Request.current.translator

  implicit def withTranslationFormatter(sc: StringContext)(implicit translator: Translator = current) = new {
    def t(args: Any*): String = translator.translate(sc, args: _*)
  }
}

private object TranslatorLogger {
  lazy val logger = Logger(Translator.getClass)
}

abstract class BaseResolver(val locale: ULocale) {
  val root: Translator = createTranslator("", List(""), List())

  private val cache = TrieMap[String, Translator]()

  protected def resolve(fullKey: String): Option[String]

  protected def unresolved(fullKey: String, default: String): Unit

  def createTranslator(id: String, usages: List[String], kinds: List[String]): Translator = new Translator(id, usages, kinds) {
    def locale = BaseResolver.this.locale

    protected def getTranslator(nid: String, usages: => List[String], kinds: => List[String]): Translator = cache.getOrElseUpdate(nid, createTranslator(nid, usages, kinds))

    protected def resolve(fullKey: String): Option[String] = BaseResolver.this.resolve(fullKey)

    override def unresolved(fullKey: String, default: String): Unit = BaseResolver.this.unresolved(fullKey, default)
  }
}

class TranslatorResolver(locale: ULocale, configuration: Configuration) extends BaseResolver(locale) {
  protected def resolve(fullKey: String): Option[String] = configuration.getString(fullKey)

  protected def unresolved(fullKey: String, default: String): Unit = TranslatorLogger.logger.info(s"Unresolved $fullKey: $default")
}
