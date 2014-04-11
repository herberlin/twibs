/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import twibs.TwibsTest

class GermanIbanTest extends TwibsTest {
  test("Test german IBAN") {
    GermanIban("A").valid should beFalse
    GermanIban("DE00123456780123456789").valid should beFalse
    GermanIban("0dE9").checksumString should be("013149")

    GermanIban.blzToBicAndName("50050201")._1 should be("HELADEF1822")
    GermanIban.blzToBicAndName("50050201")._2 should be("Frankfurter Sparkasse")

    val iban = GermanIban("DE16 7002 0270 0005 7131 53")
    iban.valid should beTrue
    iban.blz should be("70020270")
    iban.kto should be("0005713153")
    iban.bankName should be("UniCredit Bank - HypoVereinsbank")
    iban.bic should be("HYVEDEMMXXX")
  }
}
