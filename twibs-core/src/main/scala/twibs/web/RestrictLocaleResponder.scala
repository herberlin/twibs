package twibs.web

import com.ibm.icu.util.ULocale
import twibs.util.{RequestSettings, LocaleUtils}

class RestrictLocaleResponder(locales: List[ULocale], delegatee: Responder) extends Responder {
  override def respond(request: Request) = {
    val requestSettings = RequestSettings.current
    requestSettings.withLocale(LocaleUtils.lookupLocale(locales, requestSettings.locale)) use delegatee.respond(request)
  }
}
