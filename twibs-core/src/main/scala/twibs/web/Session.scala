/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

import scala.xml.{Unparsed, NodeSeq}

trait Session extends AttributeContainer {
  def invalidate(): Unit

  import Session._

  def addNotificationToSession(notificationScript: String): Unit =
    setAttribute(NOTIFICATIONS_PARAMETER_NAME, notificationScriptsFromSession + notificationScript)

  private def notificationScriptsFromSession = getAttribute(NOTIFICATIONS_PARAMETER_NAME, "")

  def notifications = {
    val notificationsString = notificationScriptsFromSession
    removeAttribute(NOTIFICATIONS_PARAMETER_NAME)
    if (notificationsString.isEmpty) NodeSeq.Empty
    else <script>{Unparsed("$" + s"(function () {$notificationsString});")}</script>
  }
}

object Session {
  private val NOTIFICATIONS_PARAMETER_NAME: String = "NOTIFICATIONS"

  implicit def unwrap(companion: Session.type): Session = current

  def current = Request.session
}
