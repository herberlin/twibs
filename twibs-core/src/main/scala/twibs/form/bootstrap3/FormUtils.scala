/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.bootstrap3

import twibs.util.Message
import twibs.util.XmlUtils
import twibs.web.{Request, Session}

trait FormUtils extends XmlUtils {
  implicit def wrapMessage(message: Message) = new {
    def showNotificationAfterReload(session: Session = Session.current) = session.addNotificationToSession(message.showNotification.toString + ";")
  }
}
