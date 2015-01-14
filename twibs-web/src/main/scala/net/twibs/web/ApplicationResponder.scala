/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util._

class ApplicationResponder(delegate: Responder) extends Responder {
  override def respond(request: Request): Option[Response] =
    ApplicationResponder.modify(request).useIt(delegate.respond)
}

object ApplicationResponder {
  def modify(request: Request): Request = {
    val systemSettings = SystemSettings.default

    def applicationSettingsFromParameterOption = request.parameters.getStringOption(ApplicationSettings.PN_NAME).flatMap(systemSettings.applicationSettings.get)

    def applicationSettingsForPath = systemSettings.applicationSettingsForPath(request.path)

    val applicationSettings = applicationSettingsFromParameterOption getOrElse applicationSettingsForPath

    request.copy(applicationSettings)
  }
}
