/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import com.google.common.cache.{CacheBuilder, Cache}
import com.ibm.icu.text.NumberFormat
import java.text.ParseException
import java.util.concurrent.TimeUnit
import org.apache.commons.io.FilenameUtils
import org.threeten.bp.format.{DateTimeParseException, DateTimeFormatter}
import org.threeten.bp.{LocalDate, LocalDateTime}
import twibs.util.XmlUtils._
import twibs.util._
import twibs.web.Upload

trait Values extends TranslationSupport {
  type ValueType

  sealed trait ValidationResult[I, O]

  case class Success[I, O](output: O) extends ValidationResult[I, O]

  case class SuccessAndTerminate[I, O](output: O) extends ValidationResult[I, O]

  case class Failure[I, O](input: I, message: String) extends ValidationResult[I, O]

  trait Input {
    def string: String

    def title: String
  }

  case class ValidInput(string: String, title: String, valueOption: Option[ValueType]) extends Input

  case class InvalidInput(string: String, title: String, message: String, valueOption: Option[ValueType]) extends Input

  type StringProcessor = (String) => ValidationResult[String, String]
  type StringToValueConverter = (String) => ValidationResult[String, ValueType]
  type StringToValueOption = (String) => Option[ValueType]
  type ValueProcessor = ((ValueType) => ValidationResult[ValueType, ValueType])
  type ValueToString = (ValueType) => String

  private var bufferedStrings: Option[Seq[String]] = None

  private var bufferedValues: Option[Seq[ValueType]] = None

  private val cachedInputs = LazyCache {
    bufferedStrings match {
      case Some(strings) =>
        bufferedStrings = None
        strings.map(validateString)
      case None => bufferedValues match {
        case Some(values) =>
          bufferedValues = None
          values.map(validateValue)
        case None =>
          val ret = defaultValues.map(validateValue)
          ret.toList ::: (for (i <- ret.size until minimumNumberOfInputs) yield validateString("")).toList
      }
    }
  }

  def strings_=(strings: Seq[String]) = {
    bufferedStrings = Some(strings)
    bufferedValues = None
    cachedInputs.reset()
    _modified = true
  }

  def strings = inputs.map(_.string)

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

  def values = inputs.collect {case ValidInput(_, _, Some(value)) => value}

  def defaultValues: Seq[ValueType] = Nil

  def inputs: Seq[Input] = cachedInputs.value

  def inputsMessageOption =
    if (validated && areInputsValid) {
      if (inputs.size < minimumNumberOfInputs) Some(Message.warning(t"minimum-number-of-inputs-message: Please enter at least ${format(minimumNumberOfInputs)} values"))
      else if (inputs.size > maximumNumberOfInputs) Some(Message.warning(t"maximum-number-of-inputs-message: Please enter no more than ${format(maximumNumberOfInputs)} values"))
      else None
    } else None

  private def format(i: Int) = Formatters.integerFormat.format(i)

  def messageDisplayTypeOption = if (validated) inputsMessageOption.map(_.displayTypeString) orElse inputs.collectFirst({case x: InvalidInput => "warning"}) else None

  def computeIsValid = areInputsValid && isNumberOfInputsValid

  private def areInputsValid = inputs.forall(_.isInstanceOf[ValidInput])

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

  def minimumLengthProcessor: StringProcessor = (string: String) => if (string.length >= minimumLength) Success(string) else Failure(string, t"minimum-length-message: Please enter at least $minimumLength characters.")

  def maximumLengthProcessor: StringProcessor = (string: String) => if (string.length <= maximumLength) Success(string) else Failure(string, t"maxiumum-length-message: Please enter no more than $maximumLength characters.")

  def requiredProcessor: StringProcessor = (string: String) => if (string.isEmpty) if (required) Failure(string, t"required-message: This field is required.") else SuccessAndTerminate(string) else Success(string)

  def trimProcessor: StringProcessor = (string: String) => Success(if (trim) string.trim else string)

  def regexProcessor: StringProcessor = (string: String) => if (regex.isEmpty || string.matches(regex)) Success(string) else Failure(string, t"regex-message: Please enter a string that matches ''$regex''.")

  def regex = ""

  def trim = true

  def minimumLength = 0

  def maximumLength = Int.MaxValue

  def required = false

  private def processString(string: String) = recursive(stringProcessors, string)

  private def processValue(value: ValueType) = recursive(valueProcessors, value)

  private def recursive[O](processors: Seq[(O) => ValidationResult[O, O]], in: O): ValidationResult[O, O] = {
    if (processors.isEmpty) Success(in)
    else processors.head.apply(in) match {
      case v: SuccessAndTerminate[O, O] => v
      case v: Failure[O, O] => v
      case v: Success[O, O] => recursive(processors.tail, v.output)
    }
  }

  def stringProcessors: List[StringProcessor] = trimProcessor :: requiredProcessor :: minimumLengthProcessor :: maximumLengthProcessor :: regexProcessor :: Nil

  def stringToValueConverter: StringToValueConverter = (string) => stringToValueOption(string) match {
    case Some(value) => Success(value)
    case None => Failure(string, t"format-message: Invalid string '$string'.")
  }

  def stringToValueOption: StringToValueOption

  def valueToString: ValueToString

  def valueProcessors: List[ValueProcessor] = Nil

  def validateString(string: String): Input =
    processString(string) match {
      case r: Failure[String, String] => InvalidInput(r.input, string, r.message, None)
      case r: SuccessAndTerminate[String, String] => ValidInput(r.output, string, None)
      case r: Success[String, String] => stringToValueConverter(r.output) match {
        case r2: Failure[String, ValueType] => InvalidInput(r2.input, r2.input, r2.message, None)
        case r2: SuccessAndTerminate[String, ValueType] => validateValue(r2.output)
        case r2: Success[String, ValueType] => validateValue(r2.output)
      }
    }

  def validateValue(value: ValueType): Input =
    processValue(value) match {
      case r: Failure[ValueType, ValueType] => InvalidInput(valueToString(r.input), titleForValue(r.input), r.message, Some(r.input))
      case r: SuccessAndTerminate[ValueType, ValueType] => ValidInput(valueToString(r.output), titleForValue(r.output), Some(r.output))
      case r: Success[ValueType, ValueType] => ValidInput(valueToString(r.output), titleForValue(r.output), Some(r.output))
    }

  def titleForValue(value: ValueType): String = valueToString(value)

  def toInput(value: ValueType): Input = ValidInput(valueToString(value), titleForValue(value), Some(value))

  def toInput(value: ValueType, title: String): Input = ValidInput(valueToString(value), title, Some(value))

  /* Convenience methods */
  def input = inputs.head

  def string = strings.head

  def stringOrEmpty = strings.headOption getOrElse ""

  def string_=(string: String) = strings = string :: Nil

  def value = values.head

  def value_=(value: ValueType) = values = value :: Nil

  def valueOption = values.headOption

  def valueOption_=(valueOption: Option[ValueType]) = valueOption.map(v => values = v :: Nil)

  def withValue[R](valueArg : ValueType)(f: => R) : R = withValues(valueArg:: Nil)(f)

  def withValues[R](valuesArg : Seq[ValueType])(f: => R) : R = {
    val was = values
    values = valuesArg
    try {
      f
    } finally {
      values = was
    }
  }
}

trait StringValues extends Values {
  type ValueType = String

  override def valueToString: ValueToString = identity

  override def stringToValueOption: StringToValueOption = (string) => Some(string)

  override def stringToValueConverter: StringToValueConverter = s => Success(s)

  abstract override def translator: Translator = super.translator.kind("STRING")
}

trait EmailAddressValues extends StringValues {
  def emailAddressProcessor: ValueProcessor = (value: ValueType) => if (EmailUtils.isValidEmailAddress(value)) Success(value) else Failure(value, t"format-message: Please enter a valid email address.")

  override def valueProcessors = super.valueProcessors :+ emailAddressProcessor

  abstract override def translator: Translator = super.translator.kind("EMAIL-ADDRESS")
}

trait WebAddressValues extends StringValues {
  def webAddressProcessor: ValueProcessor = (value: ValueType) => if (UrlUtils.isValidWebAddress(value)) Success(value) else Failure(value, t"format-message: Please enter a valid web address.")

  override def valueProcessors = super.valueProcessors :+ webAddressProcessor

  abstract override def translator: Translator = super.translator.kind("WEB-ADDRESS")
}

trait BooleanValues extends Values {
  type ValueType = Boolean

  override def valueToString: ValueToString = value => value.toString

  override def stringToValueOption: StringToValueOption = {
    case "true" => Some(true)
    case "false" => Some(false)
    case string => None
  }

  abstract override def translator: Translator = super.translator.kind("BOOLEAN")
}

trait MinMaxValues extends Values {
  def checkMinimum: ValueProcessor = (value: ValueType) => if (isGreaterOrEqualMinimum(value)) Success(value) else Failure(value, t"minimum-message: Must be greater or equal ${titleForValue(minimum)}.")

  def checkMaximum: ValueProcessor = (value: ValueType) => if (isLessOrEqualMaximum(value)) Success(value) else Failure(value, t"maximum-message: Must be less or equal ${titleForValue(maximum)}.")

  def minimum: ValueType

  def maximum: ValueType

  protected def isGreaterOrEqualMinimum(value: ValueType): Boolean

  protected def isLessOrEqualMaximum(value: ValueType): Boolean

  override def valueProcessors = super.valueProcessors ::: checkMinimum :: checkMaximum :: Nil
}

trait NumberValues extends MinMaxValues {
  def editNumberFormat: NumberFormat

  def displayNumberFormat: NumberFormat = editNumberFormat

  override def valueToString: ValueToString = value => editNumberFormat.format(value)

  override def stringToValueOption: StringToValueOption = string => try {
    Some(parseString(string))
  } catch {
    case e: ParseException => None
  }

  protected def parseString(string: String): ValueType

  override def titleForValue(value: ValueType): String = displayNumberFormat.format(value)

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

  override def stringToValueOption: StringToValueOption = string => Uploads.get(string)

  override def valueToString: ValueToString = value => value.id

  override def titleForValue(value: ValueType): String = value.name + " (" + value.sizeAsHumanReadableString + ")"

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

  override def stringToValueOption: StringToValueOption = string => try {
    Some(LocalDateTime.parse(string, editDateTimeFormat))
  } catch {
    case e: DateTimeParseException => None
  }

  abstract override def translator: Translator = super.translator.kind("DATE-TIME")

  override def valueToString: ValueToString = value => editDateTimeFormat.format(value)
}

trait DateValues extends MinMaxValues {
  type ValueType = LocalDate

  def editDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern(translator.translate("date-format", "dd.MM.yyyy"), translator.locale.toLocale)

  def displayDateFormat = editDateFormat

  def minimum = LocalDate.MIN

  def maximum = LocalDate.MAX

  override protected def isLessOrEqualMaximum(value: ValueType): Boolean = !value.isAfter(maximum)

  override protected def isGreaterOrEqualMinimum(value: ValueType): Boolean = !value.isBefore(minimum)

  override def stringToValueOption: StringToValueOption = string => try {
    Some(LocalDate.parse(string, editDateFormat))
  } catch {
    case e: DateTimeParseException => None
  }

  abstract override def translator: Translator = super.translator.kind("DATE")

  override def valueToString: ValueToString = value => editDateFormat.format(value)
}

trait EnumerationValues[E <: Enumeration] extends Options {
  type ValueType = E#Value

  def enumeration: E

  override def stringToValueOption: StringToValueOption = string => try {
    Some(enumeration.apply(string.toInt))
  } catch {
    case _: NoSuchElementException | _: NumberFormatException => None
  }

  override def valueToString: ValueToString = value => value.id.toString

  def computeOptions = enumeration.values.toList
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
  case class OptionI(string: String, title: String, value: ValueType, enabled: Boolean = true)

  implicit def toOptions[V <: ValueType](values: List[ValueType]): List[OptionI] = values.map(toOption)

  def toOption(value: ValueType) = OptionI(valueToString(value), titleForOption(value), value)

  def titleForOption(value: ValueType) = titleForValue(value)

  def toOption(value: ValueType, title: String, enabled: Boolean = true) = OptionI(valueToString(value), title, value, enabled)

  final def options: List[OptionI] = _options.value

  final def optionValues: List[ValueType] = options.map(_.value)

  final def optionStrings: List[String] = options.map(_.string)

  private val _options = LazyCache(computeOptions)

  def computeOptions: List[OptionI]

  def resetOptions(): Unit = _options.reset()

  protected def checkIsInOptionValues: ValueProcessor = (value: ValueType) => if (optionValues.contains(value)) Success(value) else Failure(value, t"is-no-option-message: Please choose a valid value.")

  override def valueProcessors = super.valueProcessors :+ checkIsInOptionValues

  def useEmptyOption(string: String) = !required || options.isEmpty || !optionStrings.contains(string)

  override def stringToValueOption: StringToValueOption = string => options.collectFirst{ case o if o.string == string => o.value }
}

trait TranslatedOptions extends Options {
  override def titleForOption(value: ValueType): String = translator.translate("option-title." + valueToString(value), titleForValue(value))
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

trait FileEntryValues extends Values {
  type ValueType = FileEntry

  override def valueToString: ValueToString = value => value.path

  override def stringToValueOption: StringToValueOption = string => defaultValues.find(_.path == string)

  override def titleForValue(value: ValueType): String = value.title
}
