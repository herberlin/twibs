/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form

import net.twibs.util.{Request, PostMethod, GetMethod}
import net.twibs.web.{Responder, Response}

class FormResponder(makeForm: () => Form) extends Responder {
  lazy val pnId = enhance(makeForm().pnId)

  def respond(request: Request): Option[Response] =
    request.parameters.getStringOption(pnId).flatMap(x => process(request))

  def process(request: Request): Option[Response] =
    request.method match {
      case GetMethod | PostMethod => Some(enhance(parse(request)))
      case _ => None
    }

  def parse(request: Request) = makeForm().process(request.parameters)

  def enhance[R](f: => R): R = f
}
