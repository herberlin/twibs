/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import com.ibm.icu.util.ULocale
import net.twibs.testutil.TwibsTest

class LocaleUtilsTest extends TwibsTest {
  test("Locale lookup") {
    val locales = ULocale.GERMAN :: ULocale.US :: ULocale.UK :: Nil
    LocaleUtils.lookupLocale(locales, ULocale.GERMAN) should be(ULocale.GERMAN)
    LocaleUtils.lookupLocale(locales, ULocale.GERMANY) should be(ULocale.GERMAN)
    LocaleUtils.lookupLocale(locales, ULocale.US) should be(ULocale.US)
    LocaleUtils.lookupLocale(locales, ULocale.UK) should be(ULocale.UK)
    LocaleUtils.lookupLocale(locales, ULocale.ENGLISH) should be(ULocale.GERMAN)
  }
}