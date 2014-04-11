/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import java.util.regex.Pattern._

object EmailUtils {
  // final val EMAIL_REGEXP_STRING: String = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"
  // Also used twibs.view.Validators: Copy there if modified (because of GWT)
  val emailAddressRegex = "^[a-z0-9\\-_%](\\.?[a-z0-9_%\\-+])*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z]{2,4}$"

  val emailAddressRegexPattern = compile(emailAddressRegex, CASE_INSENSITIVE)

  def isValidEmailAddress(emailAddress: String) = emailAddress != null && emailAddressRegexPattern.matcher(emailAddress).matches()

  //  def isEmailAddressWithName (emailAddress: String): Boolean = {
  //    if (emailAddress == null || StringUtils.isBlank (emailAddress) ) {
  //      return true
  //    }
  //    try {
  //      new InternetAddress (emailAddress, true)
  //    }
  //    catch {
  //      case ex: AddressException => {
  //        return false
  //      }
  //    }
  //    return EMAIL_REGEXP.matcher (emailAddress).matches
  //
  //}
}


