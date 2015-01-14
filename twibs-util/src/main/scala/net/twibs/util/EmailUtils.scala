/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import java.util.regex.Pattern._
import javax.mail.internet.{AddressException, InternetAddress}

object EmailUtils {
  // final val EMAIL_REGEXP_STRING: String = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"
  val emailAddressRegex = "^[a-z0-9\\-_%](\\.?[a-z0-9_%\\-+])*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z]{2,4}$"

  val emailAddressRegexPattern = compile(emailAddressRegex, CASE_INSENSITIVE)

  def isRfc822InternetAddress(emailAddress: String) = toInternetAddressOption(emailAddress).isDefined

  def isValidEmailAddress(emailAddress: String) = emailAddress != null && emailAddressRegexPattern.matcher(emailAddress).matches()

  def toInternetAddressOption(emailAddress: String) =
    if (emailAddress == null) None
    else
      try {
        InternetAddress.parse(emailAddress, true) match {
          case Array(ia) => ia.validate(); Some(ia)
          case _ => None
        }
      } catch {
        case e: AddressException => None
      }

  def cleanupEmailAddress(emailAddress: String) = emailAddress.trim().stripSuffix(".").replaceAll("[\\.\\s]+\\@", "\\@").toLowerCase
}
