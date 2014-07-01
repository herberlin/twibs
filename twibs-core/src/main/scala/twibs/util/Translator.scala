/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import scala.collection.concurrent.TrieMap
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._

import com.ibm.icu.text.MessageFormat
import com.ibm.icu.util.ULocale

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

  private def appendPrefixes(from: List[String], ps: List[String]) = from.map(parent => ps.map(prefix => parent + prefix + ".")).flatten ::: from

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
    prefixes.view.map(prefix => resolve(prefix + key)).collectFirst { case Some(x) => x}

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
      val mf = new MessageFormat(messageFormatString, locale)
      args match {
        case Seq(m: Map[_, _]) => mf.format(m.asJava)
        case _ => mf.format(args.toArray)
      }
    } catch {
      case e: IllegalArgumentException =>
        Translator.logger.error(s"Invalid format '$messageFormatString'", e)
        throw e
    }

  protected def getTranslator(nid: String, usages: => List[String], kinds: => List[String]): Translator

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
  val root: Translator = createTranslator("", List(""), List())

  private val cache = TrieMap[String, Translator]()

  protected def resolve(fullKey: String): Option[String] = configuration.getString(fullKey)

  protected def unresolved(fullKey: String, default: String): Unit = Translator.logger.info(s"Unresolved $fullKey: $default")

  def createTranslator(id: String, usages: List[String], kinds: List[String]): Translator = new Translator(id, usages, kinds) {
    def locale = TranslatorResolver.this.locale

    protected def getTranslator(nid: String, usages: => List[String], kinds: => List[String]): Translator = cache.getOrElseUpdate(nid, createTranslator(nid, usages, kinds))

    protected def resolve(fullKey: String): Option[String] = TranslatorResolver.this.resolve(fullKey)

    override def unresolved(fullKey: String, default: String): Unit = TranslatorResolver.this.unresolved(fullKey, default)
  }
}
