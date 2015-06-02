/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import com.ibm.icu.text.{DecimalFormat, NumberFormat}
import com.ibm.icu.util.{Currency, ULocale}
import org.threeten.bp._
import org.threeten.bp.chrono.IsoChronology
import org.threeten.bp.format.{DateTimeFormatter, DateTimeFormatterBuilder, ResolverStyle}
import org.threeten.bp.temporal.ChronoField._

class Formatters(translator: Translator, locale: ULocale, currency: Currency, zoneId: ZoneId) {
  val decimalFormat = {
    val ret = NumberFormat.getNumberInstance(locale)
    ret.setMinimumFractionDigits(2)
    ret.setMaximumFractionDigits(2)
    ret
  }
  val integerFormat = NumberFormat.getIntegerInstance(locale)
  val percentFormat = NumberFormat.getPercentInstance(locale)
  val percentFormatWithoutSuffix = {
    val ret = NumberFormat.getPercentInstance(locale).asInstanceOf[DecimalFormat]
    ret.setPositiveSuffix("")
    ret.setNegativeSuffix("")
    ret
  }

  //  val currency = Currency.getInstance(currencyCode)
  val currencyFormatWithSymbol = {
    val ret = NumberFormat.getCurrencyInstance(locale)
    ret.setCurrency(currency)
    ret
  }
  val currencyFormatWithoutSymbol = decimalFormat
  val currencyFormatWithCode = {
    val ret = NumberFormat.getInstance(locale, NumberFormat.ISOCURRENCYSTYLE)
    ret.setCurrency(currency)
    ret
  }

  def getDisplayCountry(isoCountryCode: String) = ULocale.getDisplayCountry("de_" + isoCountryCode, locale)

  val mediumDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendPattern(translator.translate("date-time-format", "dd.MM.yyyy HH:mm:ss")).toFormatter(locale.toLocale)

  val mediumDateShortTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendPattern(translator.translate("date-time-short-format", "dd.MM.yyyy HH:mm")).toFormatter(locale.toLocale)

  val mediumDateFormatter = new DateTimeFormatterBuilder().parseLenient().appendPattern(translator.translate("date-format", "dd.MM.yyyy")).toFormatter(locale.toLocale)

  val shortDateFormatter = new DateTimeFormatterBuilder().parseLenient().appendPattern(translator.translate("date-short-format", "dd.MM.yy")).toFormatter(locale.toLocale)

  def doubleToFormattable(value: Double) = new DoubleFormattable(value)

  class DoubleFormattable(value: Double) {
    def formatAsInteger = integerFormat.format(value)

    def formatAsPercent = percentFormat.format(value)

    def formatAsCurrencyWithSymbol = currencyFormatWithSymbol.format(value)

    def formatAsCurrencyWithoutSymbol = currencyFormatWithoutSymbol.format(value)

    def formatAsCurrencyWithCode = currencyFormatWithCode.format(value)
  }

  def intToFormattable(value: Int) = new IntFormattable(value)

  class IntFormattable(value: Int) {
    def formatAsInteger = integerFormat.format(value)

    def formatAsPercent = percentFormat.format(value)

    def formatAsCurrencyWithSymbol = currencyFormatWithSymbol.format(value)

    def formatAsCurrencyWithCode = currencyFormatWithCode.format(value)
  }

  def localDateTimeToFormattable(value: LocalDateTime) = new LocalDateTimeFormattable(value)

  class LocalDateTimeFormattable(dateTime: LocalDateTime) {
    def formatAsMediumDateTime = mediumDateTimeFormatter.format(dateTime)

    def formatAsIso = DateTimeFormatter.ISO_DATE_TIME.format(dateTime)

    def formatAsIsoWithOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(withZoneId)

    def withZoneId = ZonedDateTime.of(dateTime, zoneId)
  }

  def zonedDateTimeToFormattable(value: ZonedDateTime) = new ZonedDateTimeFormattable(value)

  class ZonedDateTimeFormattable(dateTime: ZonedDateTime) {
    def formatAsMediumDateTime = mediumDateTimeFormatter.format(dateTime)

    def formatAsIso = DateTimeFormatter.ISO_DATE_TIME.format(dateTime)

    def formatAsIsoWithOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime)

    def formatAsFixedIsoLocalDateTime = Formatters.FIXED_ISO_LOCAL_DATE_TIME.format(dateTime)
  }

  def localDateToFormattable(value: LocalDate) = new LocalDateFormattable(value)

  class LocalDateFormattable(date: LocalDate) {
    def formatAsMediumDate = mediumDateFormatter.format(date)

    def formatAsShortDate = shortDateFormatter.format(date)
  }

  def getHumanReadableByteCountSi(bytes: Long): String = innerGetHumanReadableByteCount(bytes, 1000, "kMGTPE", "")

  def getHumanReadableByteCount(bytes: Long): String = innerGetHumanReadableByteCount(bytes, 1024, "KMGTPE", "i")

  private def innerGetHumanReadableByteCount(bytes: Long, unit: Int, units: String, add: String): String = {
    if (bytes < unit) {
      bytes + " B"
    } else {
      def exp: Int = (Math.log(bytes) / Math.log(unit)).asInstanceOf[Int]
      def pre: String = units.charAt(exp - 1) + add
      decimalFormat.format(bytes / Math.pow(unit, exp)) + " " + pre + "B"
    }
  }
}

object Formatters extends UnwrapCurrent[Formatters] {
  val systemZoneOffset = OffsetDateTime.now().getOffset

  def current = Request.current.formatters

  implicit def doubleFormattable(value: Double): Formatters#DoubleFormattable = current.doubleToFormattable(value)

  implicit def intFormattable(value: Int): Formatters#IntFormattable = current.intToFormattable(value)

  implicit def localDateTimeFormattable(dateTime: LocalDateTime): Formatters#LocalDateTimeFormattable = current.localDateTimeToFormattable(dateTime)

  implicit def zonedDateTimeFormattable(dateTime: ZonedDateTime): Formatters#ZonedDateTimeFormattable = current.zonedDateTimeToFormattable(dateTime)

  implicit def localDateFormattable(date: LocalDate): Formatters#LocalDateFormattable = current.localDateToFormattable(date)

  /**
   * Nearly the same as DateTimeFormatter.ISO_LOCAL_DATE_TIME but with always
   * 3 digits for nano seconds.
   *
   * For better layout in log files and
   * used for querying JCR (which needs fixed nano seconds with 3 digits)
   */
  val FIXED_ISO_LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
    .parseCaseInsensitive.append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral('T')
    .appendValue(HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(MINUTE_OF_HOUR, 2)
    .optionalStart.appendLiteral(':').appendValue(SECOND_OF_MINUTE, 2)
    .optionalStart.appendFraction(NANO_OF_SECOND, 3, 3, true)
    .toFormatter.withResolverStyle(ResolverStyle.STRICT).withChronology(IsoChronology.INSTANCE)
}
