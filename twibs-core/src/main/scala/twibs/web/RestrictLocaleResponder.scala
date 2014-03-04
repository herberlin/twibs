package twibs.web

import com.ibm.icu.util.ULocale
import twibs.util.{Environment, LocaleUtils}

class RestrictLocaleResponder(locales: List[ULocale], delegatee: Responder) extends Responder {
  override def respond(request: Request) = {
    val env = Environment.current
    env.withLocale(LocaleUtils.lookupLocale(locales, env.locale)) use delegatee.respond(request)
  }
}
