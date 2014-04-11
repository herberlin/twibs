/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

trait Responder {
  def respond(request: Request): Option[Response]
}

class ResponderChain(list: List[Responder]) extends Responder {
  def respond(request: Request): Option[Response] = list.view.flatMap(_.respond(request)).headOption
}

object Responder {
  implicit def apply(list: List[Responder]) = new ResponderChain(list)
}
