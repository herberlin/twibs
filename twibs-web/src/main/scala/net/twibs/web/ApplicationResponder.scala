/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util._

class ApplicationResponder(delegate: Responder) extends Responder {
  val defaultUserSettings: UserSettings = new UserSettings {
    val name = "anonymous"

    val locale = SystemSettings.locale
  }

  override def respond(request: Request): Option[Response] = {
    val systemSettings = SystemSettings.default

    def applicationSettingsFromParameterOption = request.parameters.getStringOption(ApplicationSettings.PN_NAME).flatMap(systemSettings.applicationSettings.get)

    def applicationSettingsForPath = systemSettings.applicationSettingsForPath(request.path)

    val applicationSettings = applicationSettingsFromParameterOption getOrElse applicationSettingsForPath

    new RequestSettings(applicationSettings) {
      override lazy val locale = LocaleUtils.lookupLocale(ApplicationSettings.locales, request.desiredLocale)
    } use {
      delegate.respond(request)
    }
  }
}
