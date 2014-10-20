/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util._
import scala.concurrent.duration._

class ApplicationResponder(delegate: Responder) extends Responder {
  val defaultUserSettings: UserSettings = new UserSettings {
    val name = "anonymous"

    val locale = SystemSettings.current.locale
  }

  val systemSettingsCached = LazyCache(if (RunMode.isDevelopment) 15 second else 1 day) {
    val ret = SystemSettings.computeDefault()
    ret.activate()
    ret
  }

  override def respond(request: Request): Option[Response] = {
    val systemSettings = systemSettingsCached.value

    def applicationSettingsFromParameterOption = request.parameters.getStringOption(ApplicationSettings.PN_NAME).flatMap(systemSettings.applicationSettings.get)

    def applicationSettingsForPath = systemSettings.applicationSettingsForPath(request.path)

    val applicationSettings = applicationSettingsFromParameterOption getOrElse applicationSettingsForPath

    applicationSettings.use {
      new RequestSettings(applicationSettings) {
        override lazy val locale = LocaleUtils.lookupLocale(ApplicationSettings.locales, request.desiredLocale)
      } use {
        delegate.respond(request)
      }
    }
  }
}
