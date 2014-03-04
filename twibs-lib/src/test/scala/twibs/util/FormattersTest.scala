package twibs.util

import com.ibm.icu.util.ULocale
import org.scalatest.{FunSuite, Matchers}
import org.threeten.bp.{ZoneId, LocalDateTime}
import scala.concurrent.duration._

class FormattersTest extends FunSuite with Matchers {
  val ukFormatter = new Formatters(Environment.configuration.translators(ULocale.ENGLISH), ULocale.ENGLISH, "USD")
  val deFormatter = new Formatters(Environment.configuration.translators(ULocale.GERMAN), ULocale.GERMAN, "EUR")

  test("Display country") {
    deFormatter.getDisplayCountry("DE") should equal("Deutschland")
    ukFormatter.getDisplayCountry("DE") should equal("Germany")
  }

  test("Currency") {
    deFormatter.currencyFormat.format(1111.555) should equal("1.111,56 €")
    ukFormatter.currencyFormat.format(1111.555) should equal("$1,111.56")
  }

  test("Currency without symbol") {
    deFormatter.currencyFormatWithoutSymbol.format(1111.555) should equal("1.111,56")
    ukFormatter.currencyFormatWithoutSymbol.format(1111.555) should equal("1,111.56")
  }

  test("Currency with code instead of symbol") {
    deFormatter.currencyFormatWithCodeInsteadOfSymbol.format(1111.555) should equal("1.111,56 EUR")
    ukFormatter.currencyFormatWithCodeInsteadOfSymbol.format(1111.555) should equal("USD1,111.56")
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

  test("Format sitemap date time") {
    val timeOffset = (2 hours).toMillis.toInt
    Formatters.formatSitemapDateTime(LocalDateTime.of(2012, 4, 16, 12, 13, 14, 15000000).atZone(ZoneId.of("UTC+02:00"))) should equal("2012-04-16T12:13:14.015+02:00")
    Formatters.formatSitemapDateTime(LocalDateTime.of(2012, 4, 16, 15, 13, 14, 15000000).atZone(ZoneId.of("UTC"))) should equal("2012-04-16T15:13:14.015Z")
  }

  import Formatters._

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
    1.1.formatAsCurrency should equal("1,10 €")
    1.116.formatAsCurrency should equal("1,12 €")
    12345.formatAsCurrency should equal("12.345,00 €")
  }

  test("Implicit MediumDateFormat") {
    LocalDateTime.of(2001, 2, 3, 11, 17, 32).formatAsMediumDateTime should equal("03.02.2001 11:17:32")
  }
}
