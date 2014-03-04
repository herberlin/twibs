package twibs.util

import com.ibm.icu.util.ULocale

object LocaleUtils {
  def lookupLocale(locales: Seq[ULocale], locale: ULocale): ULocale = {
    require(!locales.isEmpty, "At least one locale must be given")

    def lookupLocale(locale: ULocale): ULocale =
      if (locales.contains(locale)) locale
      else Option(locale.getFallback).map(lookupLocale) getOrElse locales.head

    lookupLocale(locale)
  }
}
