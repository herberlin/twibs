/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.form.base

import java.io.File
import java.text.ParseException
import java.util.concurrent.TimeUnit

import net.twibs.util.XmlUtils._
import net.twibs.util._
import net.twibs.web.Upload

import com.google.common.cache.{Cache, CacheBuilder}
import com.ibm.icu.text.NumberFormat
import com.ibm.icu.util.ULocale
import org.apache.commons.io.FilenameUtils
import org.owasp.html.PolicyFactory
import org.threeten.bp.format.{DateTimeFormatter, DateTimeParseException}
import org.threeten.bp.{LocalDate, LocalDateTime}

trait Values extends TranslationSupport {
  type ValueType

  case class Input(string: String, title: String, valueOption: Option[ValueType] = None, messageOption: Option[Message] = None, continue: Boolean = true, index: Int = 0) {
    def validate(valid: => Boolean, message: => String) = if (valid) this else failure(message)

    def failure(message: String) = copy(messageOption = Some(Message.warning(message)))

    def success() = copy(messageOption = None)

    def terminate() = copy(continue = false)

    def value = valueOption.get

    def isValid = messageOption.isEmpty

    def withIndex(newIndex: Int) = copy(index = newIndex)
  }

  type StringProcessor = Input => Input

  private var bufferedStrings: Option[Seq[String]] = None

  private var bufferedValues: Option[Seq[ValueType]] = None

  private val cachedInputs = LazyCache {
    {
      (bufferedStrings match {
        case Some(strings) =>
          bufferedStrings = None
          if (isStringProcessingEnabled)
            strings.map(validateString)
          else
            defaultInputs
        case None => bufferedValues match {
          case Some(values) =>
            bufferedValues = None
            values.map(validateValue)
          case None => defaultInputs
        }
      }).zipWithIndex.map { case (i, index) => i.withIndex(index)}
    }
  }

  def defaultInputs = {
    val ret = defaultValues.map(validateValue)
    ret.toList ::: (for (i <- ret.size until minimumNumberOfInputs) yield stringToInput("")).toList
  }

  def isStringProcessingEnabled: Boolean = true

  def strings_=(strings: Seq[String]) = {
    bufferedStrings = Some(strings)
    bufferedValues = None
    cachedInputs.reset()
    _modified = true
  }

  def values_=(values: Seq[ValueType]) = {
    bufferedValues = Some(values)
    bufferedStrings = None
    cachedInputs.reset()
    _modified = true
  }

  def resetInputs(): Unit = {
    bufferedStrings = None
    bufferedValues = None
    cachedInputs.reset()
    _validated = false
    _modified = false
  }

  private[base] var _validated = false

  private var _modified = false

  def defaultValues: Seq[ValueType] = Nil

  def inputs: Seq[Input] = cachedInputs.value

  def inputsMessageOption =
    if (validated && areInputsValid) {
      if (inputs.size < minimumNumberOfInputs) Some(Message.warning(t"minimum-number-of-inputs-message: Please enter at least ${format(minimumNumberOfInputs)} values"))
      else if (inputs.size > maximumNumberOfInputs) Some(Message.warning(t"maximum-number-of-inputs-message: Please enter no more than ${format(maximumNumberOfInputs)} values"))
      else None
    } else None

  private def format(i: Int) = Formatters.integerFormat.format(i)

  def messageDisplayTypeOption = if (validated) inputsMessageOption.map(_.displayTypeString) orElse inputs.collectFirst({ case Input(_, _, _, Some(message), _, _) => message.displayTypeString}) else None

  def computeIsValid = areInputsValid && isNumberOfInputsValid

  private def areInputsValid = inputs.forall(_.isValid)

  private def isNumberOfInputsValid = inputs.size >= minimumNumberOfInputs && inputs.size <= maximumNumberOfInputs

  def validated = _validated

  def isValid = !validated || computeIsValid

  def validate(): Boolean = {
    _validated = true
    computeIsValid
  }

  def isModified = _modified

  def minimumNumberOfInputs = 1

  def maximumNumberOfInputs = 1

  def minimumLengthProcessor(input: Input) = input.validate(input.string.length >= minimumLength, t"minimum-length-message: Please enter at least $minimumLength characters.")

  def maximumLengthProcessor(input: Input) = input.validate(input.string.length <= maximumLength, t"maxiumum-length-message: Please enter no more than $maximumLength characters.")

  def requiredProcessor(input: Input) = if (input.string.isEmpty) if (required) input.failure(t"required-message: This field is required.") else input.terminate() else input

  def trimProcessor(input: Input) = if (trim) input.copy(string = input.string.trim()) else input

  def regexProcessor(input: Input) = input.validate(regex.isEmpty || input.string.matches(regex), t"regex-message: Please enter a string that matches ''$regex''.")

  def regex = ""

  def trim = true

  def minimumLength = 0

  def maximumLength = Int.MaxValue

  def required = false

  def valueToString(value: ValueType): String

  def stringToValueOption(string: String): Option[ValueType]

  def valueToInput(value: ValueType) = Input(valueToString(value), computeTitleForValue(value), Some(value), None)

  def stringToInput(string: String) = Input(string, string, None, None)

  def titleForValue(value: ValueType): String = valueToInput(value).title

  protected def computeTitleForValue(value: ValueType): String = valueToString(value)

  def stringProcessors: List[StringProcessor] = trimProcessor _ :: requiredProcessor _ :: minimumLengthProcessor _ :: maximumLengthProcessor _ :: regexProcessor _ :: Nil

  def valueProcessors: List[StringProcessor] = Nil

  def validateValue(value: ValueType): Input = recursive(valueProcessors, valueToInput(value))

  def validateString(string: String): Input = recursive(stringProcessors ::: stringToValueConverter _ :: valueProcessors, stringToInput(string))

  def stringToValueConverter(input: Input) =
    if (input.valueOption.isDefined) input
    else stringToValueOption(input.string) match {
      case Some(value) => valueToInput(value)
      case None => input.failure(t"format-message: Invalid string ''${input.string}''.")
    }

  private def recursive[O](processors: Seq[StringProcessor], input: Input): Input = {
    if (processors.isEmpty) input
    else processors.head.apply(input) match {
      case i@Input(_, _, _, Some(m), _, _) => i
      case i@Input(_, _, _, _, false, _) => i
      case i => recursive(processors.tail, i)
    }
  }

  /* Convenience methods */
  final def isChanged = values != defaultValues

  final def values = inputs.collect { case Input(_, _, Some(value), _, _, _) => value}

  final def input = inputs.head

  final def strings = inputs.map(_.string)

  final def string = strings.head

  final def stringOrEmpty = strings.headOption getOrElse ""

  final def string_=(string: String) = strings = string :: Nil

  final def validValues = inputs.collect { case Input(_, _, Some(value), None, _, _) => value}

  final def validValue = validValues.head

  final def validValueOption = validValues.headOption

  final def validValueOrDefault = validValueOption getOrElse defaultValue

  final def value = values.head

  final def value_=(value: ValueType) = values = value :: Nil

  final def valueOption = values.headOption

  final def valueOption_=(valueOption: Option[ValueType]) = valueOption match {
    case Some(v) => values = v :: Nil
    case None => values = Nil
  }

  final def defaultValue = defaultValues.head

  final def defaultValueOption = defaultValues.headOption

  final def valueOrDefault = valueOption getOrElse defaultValue

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

trait StringValues extends Values {
  type ValueType = String

  override def valueToString(value: ValueType) = value

  override def stringToValueOption(string: String) = Some(string)

  abstract override def translator: Translator = super.translator.kind("STRING")
}

trait TranslatedValueTitles extends Values {
  override def computeTitleForValue(value: ValueType): String = translator.usage("value-title").translate(valueToString(value), super.computeTitleForValue(value))
}

trait EmailAddressValues extends StringValues {
  def emailAddressProcessor(input: Input) = input.validate(EmailUtils.isValidEmailAddress(input.value), t"format-message: Please enter a valid email address.")

  override def valueProcessors = super.valueProcessors :+ emailAddressProcessor _

  abstract override def translator: Translator = super.translator.kind("EMAIL-ADDRESS")
}

trait WebAddressValues extends StringValues {
  def webAddressProcessor(input: Input) = input.validate(UrlUtils.isValidWebAddress(input.value), t"format-message: Please enter a valid web address.")

  override def valueProcessors = super.valueProcessors :+ webAddressProcessor _

  abstract override def translator: Translator = super.translator.kind("WEB-ADDRESS")
}

trait HtmlValues extends StringValues {
  def policyFactory: PolicyFactory

  def cleanupProcessor(input: Input) = input.copy(string = policyFactory.sanitize(string))

  override def stringProcessors: List[StringProcessor] = cleanupProcessor _ :: super.stringProcessors
}

trait BooleanValues extends Values {
  type ValueType = Boolean

  override def valueToString(value: ValueType) = value.toString

  override def stringToValueOption(string: String) = string match {
    case "true" => Some(true)
    case "false" => Some(false)
    case x => None
  }

  abstract override def translator: Translator = super.translator.kind("BOOLEAN")
}

trait MinMaxValues extends Values {
  def checkMinimum(input: Input) = input.validate(isGreaterOrEqualMinimum(input.value), t"minimum-message: Must be greater or equal ${computeTitleForValue(minimum)}.")

  def checkMaximum(input: Input) = input.validate(isLessOrEqualMaximum(input.value), t"maximum-message: Must be less or equal ${computeTitleForValue(maximum)}.")

  def minimum: ValueType

  def maximum: ValueType

  protected def isGreaterOrEqualMinimum(value: ValueType): Boolean

  protected def isLessOrEqualMaximum(value: ValueType): Boolean

  override def valueProcessors = super.valueProcessors ::: checkMinimum _ :: checkMaximum _ :: Nil
}

trait NumberValues extends MinMaxValues {
  def editNumberFormat: NumberFormat

  def displayNumberFormat: NumberFormat = editNumberFormat

  override def valueToString(value: ValueType) = editNumberFormat.format(value)

  override def stringToValueOption(string: String) = try {
    Some(parseString(string))
  } catch {
    case e: ParseException => None
  }

  protected def parseString(string: String): ValueType

  override def computeTitleForValue(value: ValueType): String = displayNumberFormat.format(value)

  abstract override def translator: Translator = super.translator.kind("NUMBER")
}

trait IntValues extends NumberValues {
  type ValueType = Int

  def editNumberFormat = Formatters.integerFormat

  protected def parseString(string: String): ValueType = editNumberFormat.parse(string).intValue

  protected def isGreaterOrEqualMinimum(value: ValueType) = value >= minimum

  protected def isLessOrEqualMaximum(value: ValueType) = value <= maximum

  override def minimum: ValueType = Int.MinValue

  override def maximum: ValueType = Int.MaxValue

  abstract override def translator: Translator = super.translator.kind("INT")
}

trait LongValues extends NumberValues {
  type ValueType = Long

  def editNumberFormat = Formatters.integerFormat

  protected def parseString(string: String): ValueType = editNumberFormat.parse(string).longValue

  protected def isGreaterOrEqualMinimum(value: ValueType) = value >= minimum

  protected def isLessOrEqualMaximum(value: ValueType) = value <= maximum

  override def minimum: ValueType = Long.MinValue

  override def maximum: ValueType = Long.MaxValue

  abstract override def translator: Translator = super.translator.kind("LONG")
}

trait DoubleValues extends NumberValues {
  type ValueType = Double

  def editNumberFormat = Formatters.decimalFormat

  protected def parseString(string: String): ValueType = editNumberFormat.parse(string).doubleValue

  protected def isGreaterOrEqualMinimum(value: ValueType) = value >= minimum

  protected def isLessOrEqualMaximum(value: ValueType) = value <= maximum

  override def minimum: ValueType = Double.MinValue

  override def maximum: ValueType = Double.MaxValue

  abstract override def translator: Translator = super.translator.kind("DOUBLE")
}

trait PercentValues extends NumberValues {
  type ValueType = Double

  def editNumberFormat = Formatters.percentFormatWithoutSuffix

  override def displayNumberFormat = Formatters.percentFormat

  protected def parseString(string: String): ValueType = editNumberFormat.parse(string).doubleValue

  protected def isGreaterOrEqualMinimum(value: ValueType) = value >= minimum

  protected def isLessOrEqualMaximum(value: ValueType) = value <= maximum

  override def minimum: ValueType = 0D

  override def maximum: ValueType = 100D

  abstract override def translator: Translator = super.translator.kind("PERCENT")
}

trait UploadValues extends Values {
  type ValueType = Upload

  override def stringToValueOption(string: String) = Uploads.get(string)

  override def valueToString(value: ValueType) = value.id

  override def computeTitleForValue(value: ValueType): String = value.name + " (" + value.sizeAsHumanReadableString + ")"

  abstract override def translator: Translator = super.translator.kind("UPLOAD")
}

trait DateTimeValues extends MinMaxValues {
  type ValueType = LocalDateTime

  def editDateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern(translator.translate("date-time-format", "dd.MM.yyyy HH:mm"), translator.locale.toLocale)

  def displayDateTimeFormat = editDateTimeFormat

  def minimum = LocalDateTime.MIN

  def maximum = LocalDateTime.MAX

  override protected def isLessOrEqualMaximum(value: ValueType): Boolean = !value.isAfter(maximum)

  override protected def isGreaterOrEqualMinimum(value: ValueType): Boolean = !value.isBefore(minimum)

  override def valueToString(value: ValueType) = editDateTimeFormat.format(value)

  override def stringToValueOption(string: String) = try {
    Some(LocalDateTime.parse(string, editDateTimeFormat))
  } catch {
    case e: DateTimeParseException => None
  }

  abstract override def translator: Translator = super.translator.kind("DATE-TIME")
}

trait DateValues extends MinMaxValues {
  type ValueType = LocalDate

  def editDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern(translator.translate("date-format", "dd.MM.yyyy"), translator.locale.toLocale)

  def displayDateFormat = editDateFormat

  def minimum = LocalDate.MIN

  def maximum = LocalDate.MAX

  override protected def isLessOrEqualMaximum(value: ValueType): Boolean = !value.isAfter(maximum)

  override protected def isGreaterOrEqualMinimum(value: ValueType): Boolean = !value.isBefore(minimum)

  override def valueToString(value: ValueType) = editDateFormat.format(value)

  override def stringToValueOption(string: String) = try {
    Some(LocalDate.parse(string, editDateFormat))
  } catch {
    case e: DateTimeParseException => None
  }

  abstract override def translator: Translator = super.translator.kind("DATE")
}

trait LocaleValues extends Values {
  type ValueType = ULocale

  override def valueToString(value: ValueType) = value.toLanguageTag

  override def stringToValueOption(string: String) = Some(ULocale.forLanguageTag(string))

  override def computeTitleForValue(value: ValueType): String = value.getDisplayName(RequestSettings.locale)

  abstract override def translator: Translator = super.translator.kind("LOCALE")
}

trait EnumerationValues[E <: Enumeration] extends Options with TranslatedValueTitles {
  type ValueType = E#Value

  def enumeration: E

  override def valueToString(value: ValueType) = value.id.toString

  override def stringToValueOption(string: String) = try {
    Some(enumeration.apply(string.toInt))
  } catch {
    case _: NoSuchElementException | _: NumberFormatException => None
  }

  def computeOptions = enumeration.values.toList

  override def computeTitleForValue(value: ValueType): String = translator.usage("value-title").translate(value.toString, value.toString)
}

trait Required extends Values {
  override def required = true
}

trait Untrimmed extends Values {
  override def trim = false
}

object Uploads {
  val cache: Cache[String, Upload] = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build()

  def register(upload: Upload): Unit = cache.put(upload.id, upload)

  def deregister(upload: Upload): Unit = cache.invalidate(upload.id)

  def get(id: String) = Option(cache.getIfPresent(id))
}

trait Options extends Values {

  // Use OptionI to avoid conflicts with scala.util.Option
  case class OptionI(string: String, title: String, value: ValueType, enabled: Boolean = true, index: Int = 0) {
    def input = Input(string, title, Some(value), index = index)

    def withIndex(newIndex: Int) = copy(index = newIndex)
  }

  implicit def toOptions[V <: ValueType](values: List[ValueType]): List[OptionI] = values.map(toOption)

  def toOption(value: ValueType) = OptionI(valueToString(value), computeTitleForValue(value), value)

  def toOption(value: ValueType, title: String, enabled: Boolean = true) = OptionI(valueToString(value), title, value, enabled)

  final def options: List[OptionI] = _options.value

  final def optionValues: List[ValueType] = options.map(_.value)

  final def optionStrings: List[String] = options.map(_.string)

  private val _options = LazyCache(computeOptions.zipWithIndex.map { case (o, index) => o.withIndex(index)})

  def computeOptions: List[OptionI]

  def resetOptions(): Unit = _options.reset()

  protected def checkIsInOptionValues(input: Input) = input.validate(optionValues.contains(input.value), t"is-no-option-message: Please choose a valid value.")

  override def valueProcessors = super.valueProcessors :+ checkIsInOptionValues _

  def useEmptyOption(string: String) = !required || options.isEmpty || !optionStrings.contains(string)

  override def stringToInput(string: String): Input = options.find(_.string == string).map(_.input) getOrElse super.stringToInput(string)

  override def valueToInput(value: ValueType): Input = options.find(_.value == value).map(_.input) getOrElse super.valueToInput(value)
}

trait FileEntry {
  def title = s"$displayName ($sizeAsHumanReadableString)"

  def displayName = FilenameUtils.getName(path)

  def path: String

  def size: Long

  def sizeAsHumanReadableString = Formatters.getHumanReadableByteCountSi(size)

  override def equals(that: Any): Boolean = that match {
    case fe: FileEntry => fe.path == path
    case _ => false
  }

  override def hashCode(): Int = 47 + path.hashCode
}

class FileFileEntry(val file: File) extends FileEntry {
  override def path: String = file.getPath

  override def size: Long = file.length()
}

trait FileEntryValues extends Values {
  type ValueType = FileEntry

  override def valueToString(value: ValueType) = value.path

  override def stringToValueOption(string: String) = defaultValues.find(_.path == string)

  override def computeTitleForValue(value: ValueType): String = value.title
}
