/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.form

import net.twibs.util.{Request, PostMethod, GetMethod}
import net.twibs.web.{Responder, Response}

class FormResponder(makeForm: () => Form) extends Responder {
  lazy val actionLink = enhance(makeForm().actionLink)

  def respond(request: Request): Option[Response] =
    if (request.path == actionLink) process(request)
    else None

  def process(request: Request): Option[Response] =
    request.method match {
      case GetMethod | PostMethod => Some(enhance(parse(request)))
      case _ => None
    }

  def parse(request: Request) = makeForm().process(request.parameters)

  def enhance[R](f: => R): R = f
}
