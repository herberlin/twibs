/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import com.ibm.icu.text.{DecimalFormat, NumberFormat}
import com.ibm.icu.util.Currency
import com.ibm.icu.util.ULocale
import org.threeten.bp.format.{DateTimeFormatterBuilder, DateTimeFormatter}
import org.threeten.bp._

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

  implicit def doubleToFormattable(value: Double) = new {
    def formatAsInteger = integerFormat.format(value)

    def formatAsPercent = percentFormat.format(value)

    def formatAsCurrencyWithSymbol = currencyFormatWithSymbol.format(value)

    def formatAsCurrencyWithoutSymbol = currencyFormatWithoutSymbol.format(value)

    def formatAsCurrencyWithCode = currencyFormatWithCode.format(value)
  }

  implicit def intToFormattable(value: Int) = new {
    def formatAsInteger = integerFormat.format(value)

    def formatAsPercent = percentFormat.format(value)

    def formatAsCurrencyWithSymbol = currencyFormatWithSymbol.format(value)

    def formatAsCurrencyWithCode = currencyFormatWithCode.format(value)
  }

  implicit def dateTimeToFormattable(dateTime: LocalDateTime) = new {
    def formatAsMediumDateTime = mediumDateTimeFormatter.format(dateTime)

    def formatAsIso = DateTimeFormatter.ISO_DATE_TIME.format(dateTime)

    def formatAsIsoWithOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(withZoneId)

    def withZoneId = ZonedDateTime.of(dateTime, zoneId)
  }

  implicit def dateToFormattable(date: LocalDate) = new {
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

object Formatters {
  val systemZoneOffset = OffsetDateTime.now().getOffset

  implicit def unwrap(companion: Formatters.type): Formatters = current

  def current = Request.current.formatters

  implicit def doubleToFormattable(value: Double) = current.doubleToFormattable(value)

  implicit def intToFormattable(value: Int) = current.intToFormattable(value)

  implicit def dateTimeToFormattable(dateTime: LocalDateTime) = current.dateTimeToFormattable(dateTime)

  implicit def dateToFormattable(date: LocalDate) = current.dateToFormattable(date)
}
