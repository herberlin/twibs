package twibs.web

import twibs.util.{RequestSettings, LocaleUtils, ApplicationSettings, SettingsFactory}

class ApplicationResponder(delegate: Responder) extends Responder {
  override def respond(request: Request): Option[Response] = {
    def applicationSettingsFromParameterOption = request.parameters.getStringOption(ApplicationSettings.PN_NAME).flatMap(SettingsFactory.applicationSettingsMap.get)

    def applicationSettingsFromPath = SettingsFactory.applicationSettingsForPath(request.path)

    val applicationSettings = applicationSettingsFromParameterOption getOrElse applicationSettingsFromPath

    applicationSettings.use {
      new RequestSettings(applicationSettings) {
        override lazy val locale = LocaleUtils.lookupLocale(ApplicationSettings.locales, request.desiredLocale)
      } use {
        delegate.respond(request)
      }
    }
  }
}
