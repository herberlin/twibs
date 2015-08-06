/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import com.ibm.icu.util.ULocale

object LocaleUtils {
  def lookupLocaleOption(locales: Seq[ULocale], locale: ULocale): Option[ULocale] = {
    def lookup(locale: ULocale): Option[ULocale] =
      if (locales.contains(locale)) Some(locale)
      else Option(locale.getFallback).flatMap(lookup)

    lookup(locale)
  }

  def lookupLocale(locales: Seq[ULocale], locale: ULocale): ULocale = {
    require(locales.nonEmpty, "At least one locale must be given")
    lookupLocaleOption(locales, locale) getOrElse locales.head
  }
}
