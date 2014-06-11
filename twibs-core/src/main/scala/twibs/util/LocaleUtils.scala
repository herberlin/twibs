/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import com.ibm.icu.util.ULocale

object LocaleUtils {
  def lookupLocale(locales: Seq[ULocale], locale: ULocale): ULocale = {
    require(locales.nonEmpty, "At least one locale must be given")

    def lookupLocale(locale: ULocale): ULocale =
      if (locales.contains(locale)) locale
      else Option(locale.getFallback).fold(locales.head)(lookupLocale)

    lookupLocale(locale)
  }
}
