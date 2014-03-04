package twibs.web

sealed trait RequestMethod

case object GetMethod extends RequestMethod

case object PostMethod extends RequestMethod

case object PutMethod extends RequestMethod

case object DeleteMethod extends RequestMethod

case object UnknownMethod extends RequestMethod
