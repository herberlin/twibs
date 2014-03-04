package twibs.form.bootstrap3

import twibs.util.Message
import twibs.util.XmlUtils
import twibs.web.Session

trait FormUtils extends XmlUtils {
  implicit def wrapMessage(message: Message) = new {
    def showNotificationAfterReload() = Session.addNotificationToSession(message.showNotification.toString + ";")
  }
}

object SortOrder extends Enumeration {
  type SortOrder = Value
  val NotSortable = Value
  val Unsorted = Value
  val Ascending = Value
  val Descending = Value
}
