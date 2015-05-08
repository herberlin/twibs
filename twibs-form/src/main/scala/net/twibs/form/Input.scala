/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form

import net.twibs.util.Parsers._
import net.twibs.util._
import org.owasp.html.PolicyFactory

trait Input extends TranslationSupport {
  type ValueType

  case class Entry(string: String, valueOption: Option[ValueType], title: String, validationMessageOption: Option[Message], continue: Boolean = true, index: Int = 0) {
    def valid = validationMessageOption.isEmpty

    def withIndex(newIndex: Int) = copy(index = newIndex)

    def invalid(message: Message) = copy(validationMessageOption = Some(message), continue = false)

    def validate(valid: => Boolean, message: => Message) = if (valid) this else invalid(message)
  }

  private[this] var _entries: Option[Seq[Entry]] = None

  private[this] var _validationMessageOption: Option[Message] = None

  private[this] final val cachedDefaultEntries = Memo(computeDefaultEntries)

  final def values_=(values: Seq[ValueType]): Unit = {
    setEntries(values.map(valueToEntry))
    _validationMessageOption = None
  }

  final def strings_=(strings: Seq[String]): Unit = {
    setEntries(strings.map(stringToEntry))
    _validationMessageOption =
      if (_entries.get.size < minimumNumberOfEntries)
        Some(danger"minimum-number-of-entries-message: Please enter at least {$minimumNumberOfEntries, plural, =1{one value}other{# values}}")
      else if (_entries.get.size > maximumNumberOfEntries)
        Some(danger"maximum-number-of-entries-message: Please enter no more than {$maximumNumberOfEntries, plural, =1{one value}other{# values}}")
      else None
  }

  protected def setEntries(es: Seq[Entry]): Unit = _entries = Some(reindex(es))

  protected def stringToEntry(string: String) =
    stringProcessors(Entry(string, None, string, None)) match {
      case e if e.continue => convertToValue(e.string) match {
        case None => e.invalid(danger"format-message: Invalid format for string ''${e.string}''")
        case valueOption => valueProcessors(valueToEntry(valueOption.get))
      }
      case e => e
    }

  def stringProcessors = processTrimmed andThen processRequired andThen minimumLengthProcessor andThen maximumLengthProcessor

  def valueProcessors = (entry: Entry) => entry

  private def processRequired = (entry: Entry) =>
    if (entry.continue && entry.string.isEmpty)
      if (required) entry.invalid(danger"required-message: Please enter a value")
      else entry.copy(continue = false)
    else entry

  private def processTrimmed = (entry: Entry) =>
    if (trim)
      entry.copy(string = trim(entry.string))
    else entry

  private def minimumLengthProcessor = (entry: Entry) => entry.validate(entry.string.length >= minimumLength, danger"minimum-length-message: Please enter at least $minimumLength characters")

  private def maximumLengthProcessor = (entry: Entry) => entry.validate(entry.string.length <= maximumLength, danger"maxiumum-length-message: Please enter no more than $maximumLength characters")

  protected def trim(string: String) = string.trim

  protected def valueToEntry(value: ValueType) = {
    val string = convertToString(value)
    val title = titleFor(string)
    Entry(string, Some(value), title, None)
  }

  protected def titleFor(string: String) = string

  /* Implement */

  def defaults: Seq[ValueType] = Nil

  def convertToString(value: ValueType): String

  def convertToValue(string: String): Option[ValueType]

  /* Overridable */

  def required = true

  def trim = true

  def minimumNumberOfEntries = 1

  def maximumNumberOfEntries = 1

  def minimumLength = 0

  def maximumLength = Int.MaxValue

  def computeDefaultEntries = {
    val ret = defaults.map(valueToEntry)
    val pad = minimumNumberOfEntries - ret.size
    reindex(if (pad > 0) ret ++ List.fill(pad)(stringToEntry("")) else ret)
  }

  def reindex(es: Seq[Entry]) = es.zipWithIndex.map { case (e, index) => e.copy(index = index) }

  // Accessors
  final def defaultEntries = cachedDefaultEntries()

  final def isModified = _entries.isDefined

  final def valid = validationMessageOption.isEmpty && !firstInvalidEntryOption.isDefined

  final def firstInvalidEntryOption = entries.find(!_.valid)

  final def validationMessageOption = _validationMessageOption

  final def entries: Seq[Entry] = _entries getOrElse defaultEntries

  final def values: Seq[ValueType] = entries.flatMap(_.valueOption)

  final def strings: Seq[String] = entries.map(_.string)

  // Convenience methods
  final def isChanged = isModified && values != defaults

  final def entry = entries.head

  final def string = strings.head

  final def stringOrEmpty = strings.headOption getOrElse ""

  final def string_=(string: String) = strings = string :: Nil

  final def value = values.head

  final def value_=(value: ValueType) = values = value :: Nil

  final def valueOption = values.headOption

  final def valueOption_=(valueOption: Option[ValueType]) = valueOption match {
    case Some(v) => values = v :: Nil
    case None => values = Nil
  }

  final def default = defaults.head

  final def defaultOption = defaults.headOption

  final def valueOrDefault = valueOption getOrElse default

  final def withValue[R](valueArg: ValueType)(f: this.type => R): R = withValues(valueArg :: Nil)(f)

  final def withValues[R](valuesArg: Seq[ValueType])(f: this.type => R): R = {
    val was = values
    values = valuesArg
    try {
      f(this)
    } finally {
      values = was
    }
  }

}

trait Untrimmed extends Input {
  override def trim = false
}

trait Optional extends Input {
  override def required = false
}

trait TranslatedValueTitles extends Input {
  override def titleFor(string: String): String = translator.usage("values").usage(string).translate("title", super.titleFor(string))
}

trait StringInput extends Input {
  type ValueType = String

  override def convertToString(value: ValueType): String = value

  override def convertToValue(string: String): Option[ValueType] = Some(string)

  override def stringProcessors: (Entry) => Entry = super.stringProcessors andThen regexProcessor

  private def regexProcessor = (entry: Entry) => entry.validate(regex.isEmpty || entry.string.matches(regex), danger"regex-message: Please enter a string that matches ''$regex''")

  // Overideable
  def regex = ""
}

trait SingleLineInput extends StringInput {
  override def stringProcessors: (Entry) => Entry = super.stringProcessors andThen checkLineBreaks

  override protected def trim(string: String): String = super.trim(super.trim(string).stripLineEnd)

  private def checkLineBreaks = (entry: Entry) =>
    if (entry.continue && (entry.string.contains("\n") || entry.string.contains("\r"))) entry.invalid(danger"line-breaks-message: Value must not contain line breaks")
    else entry
}

trait EmailAddressInput extends SingleLineInput {
  override def stringProcessors = super.stringProcessors andThen emailAddressProcessor

  private def emailAddressProcessor = (entry: Entry) =>
    if (entry.continue) entry.validate(EmailUtils.isValidEmailAddress(entry.string), danger"format-message: ''${entry.string}'' is not a valid email address")
    else entry
}

trait WebAddressInput extends SingleLineInput {
  override def stringProcessors = super.stringProcessors andThen webAddressProcessor

  private def webAddressProcessor = (entry: Entry) => entry.validate(UrlUtils.isValidWebAddress(entry.string), danger"format-message: ''${entry.string}'' is not a valid web address")
}

trait HtmlInput extends StringInput {
  def policyFactory: PolicyFactory

  override def stringProcessors = super.stringProcessors andThen cleanupHtml

  private def cleanupHtml = (entry: Entry) => entry.copy(string = policyFactory.sanitize(entry.string))
}

trait BooleanInput extends Input {
  override type ValueType = Boolean

  override def convertToString(b: Boolean) = b.toString

  override def convertToValue(string: String) = Some("true" == string)
}

trait LongInput extends Input {
  override type ValueType = Long

  override def convertToString(l: Long) = l.toString

  override def convertToValue(string: String) = string.toLongOption
}

trait IntInput extends Input {
  override type ValueType = Int

  override def convertToString(i: Int) = i.toString

  override def convertToValue(string: String) = string.toIntOption
}

trait Options extends Input {
  def options: Seq[ValueType]

  def optionEntries: Seq[Entry] = reindex(options.map(super.valueToEntry))

  override protected def valueToEntry(value: ValueType): Entry =
    optionEntries.find(_.valueOption == Some(value)) getOrElse invalidate(super.valueToEntry(value))

  override protected def stringToEntry(string: String): Entry =
    optionEntries.find(_.string == string) getOrElse invalidate(super.stringToEntry(string))

  private def invalidate(entry: Entry) =
    if (entry.valid) entry.copy(validationMessageOption = Some(danger"not-an-option-message: ''${entry.string}'' is not an option"))
    else entry
}

trait EnumerationInput[E <: Enumeration] extends Input with Options {
  type ValueType = E#Value

  def enumeration: E

  override def convertToString(value: ValueType): String = value.id.toString

  override def convertToValue(string: String): Option[ValueType] = try {
    Some(enumeration.apply(string.toInt))
  } catch {
    case _: NoSuchElementException | _: NumberFormatException => None
  }

  override def options: Seq[ValueType] = enumeration.values.toList

  override protected def titleFor(string: String): String = translator.usage("value-title").translate(string, string)
}

class FormException(message: String) extends RuntimeException(message)
