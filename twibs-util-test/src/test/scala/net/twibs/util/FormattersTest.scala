/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import com.ibm.icu.util.{Currency, ULocale}
import org.scalatest.{FunSuite, Matchers}
import org.threeten.bp._

class FormattersTest extends FunSuite with Matchers {
  val ukFormatter = new Formatters(ApplicationSettings.translators(ULocale.ENGLISH), ULocale.ENGLISH, Currency.getInstance("USD"), ZoneId.of("America/New_York"))
  val deFormatter = new Formatters(ApplicationSettings.translators(ULocale.GERMAN), ULocale.GERMAN, Currency.getInstance("EUR"), ZoneId.of("Europe/Berlin"))

  test("Display country") {
    deFormatter.getDisplayCountry("DE") should equal("Deutschland")
    ukFormatter.getDisplayCountry("DE") should equal("Germany")
  }

  test("Currency") {
    deFormatter.currencyFormatWithSymbol.format(1111.555) should equal("1.111,56 €")
    ukFormatter.currencyFormatWithSymbol.format(1111.555) should equal("$1,111.56")
  }

  test("Currency without symbol") {
    deFormatter.currencyFormatWithoutSymbol.format(1111.555) should equal("1.111,56")
    ukFormatter.currencyFormatWithoutSymbol.format(1111.555) should equal("1,111.56")
  }

  test("Currency with code instead of symbol") {
    deFormatter.currencyFormatWithCode.format(1111.555) should equal("1.111,56 EUR")
    ukFormatter.currencyFormatWithCode.format(1111.555) should equal("USD1,111.56")
  }

  test("Decimal Format") {
    deFormatter.decimalFormat.format(1111.555) should equal("1.111,56")
    ukFormatter.decimalFormat.format(1111.555) should equal("1,111.56")
  }

  test("Percent") {
    deFormatter.percentFormat.format(0.015) should equal("2 %")
    deFormatter.percentFormat.format(-0.015) should equal("-2 %")
    deFormatter.percentFormatWithoutSuffix.format(0.015) should equal("2")
    deFormatter.percentFormatWithoutSuffix.format(-0.015) should equal("-2")
  }

  import Formatters._

  test("Format as iso") {
    LocalDateTime.of(2012, 4, 16, 12, 13, 14, 15000000).formatAsIso should equal("2012-04-16T12:13:14.015")
  }

  test("Format zoned") {
    SystemSettings.copy(zoneId = ZoneId.of("Europe/Berlin")).use {
      LocalDateTime.of(2012, 4, 16, 12, 13, 14, 15000000).formatAsIsoWithOffset should equal("2012-04-16T12:13:14.015+02:00")
      LocalDateTime.of(2012, 12, 16, 12, 13, 14, 15000000).formatAsIsoWithOffset should equal("2012-12-16T12:13:14.015+01:00")
    }
    SystemSettings.copy(zoneId = ZoneId.of("America/New_York")).use {
      LocalDateTime.of(2012, 4, 16, 12, 13, 14, 15000000).formatAsIsoWithOffset should equal("2012-04-16T12:13:14.015-04:00")
      LocalDateTime.of(2012, 12, 16, 12, 13, 14, 15000000).formatAsIsoWithOffset should equal("2012-12-16T12:13:14.015-05:00")
    }

    ZonedDateTime.of(LocalDateTime.of(2012, 12, 16, 12, 13, 14, 15000000), ZoneId.of("Europe/Berlin")).formatAsIsoWithOffset should equal("2012-12-16T12:13:14.015+01:00")
  }

  test("Implicit percent") {
    0.1.formatAsPercent should equal("10 %")
    0.014.formatAsPercent should equal("1 %")
    0.016.formatAsPercent should equal("2 %")
  }

  test("Implicit integer") {
    1.1.formatAsInteger should equal("1")
    1.6.formatAsInteger should equal("2")
    12345.formatAsInteger should equal("12.345")
  }

  test("Implicit currency") {
    1.1.formatAsCurrencyWithSymbol should equal("1,10 €")
    1.116.formatAsCurrencyWithSymbol should equal("1,12 €")
    12345.formatAsCurrencyWithSymbol should equal("12.345,00 €")
  }

  test("Implicit MediumDateFormat") {
    LocalDateTime.of(2001, 2, 3, 11, 17, 32).formatAsMediumDateTime should equal("03.02.2001 11:17:32")
  }
}
