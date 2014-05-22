/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import twibs.util.Message
import twibs.util.XmlUtils
import twibs.web.Session

trait FormUtils extends XmlUtils {
  implicit def wrapMessage(message: Message) = new {
    def showNotificationAfterReload() = Session.addNotificationToSession(message.showNotification.toString + ";")
  }
}

