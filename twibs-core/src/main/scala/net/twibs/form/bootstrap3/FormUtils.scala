/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.form.bootstrap3

import net.twibs.util.{Message, XmlUtils}
import net.twibs.web.Session

trait FormUtils extends XmlUtils {
  implicit def wrapMessage(message: Message) = new {
    def showNotificationAfterReload(session: Session = Session.current) = session.addNotificationToSession(message.showNotification.toString + ";")
  }
}
