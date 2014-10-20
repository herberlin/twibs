/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.form

import net.twibs.util.Parameters
import net.twibs.web.{PostMethod, GetMethod, Response, Request, Responder}

class FormResponder(makeForm: (Parameters) => Form) extends Responder {
  lazy val actionLink = enhance(makeForm(new Parameters()).actionLink)

  def respond(request: Request): Option[Response] =
    if (request.path == actionLink) process(request)
    else None

  def process(request: Request): Option[Response] =
    request.method match {
      case GetMethod | PostMethod => Some(enhance(parse(request)))
      case _ => None
    }

  def parse(request: Request) = makeForm(request.parameters).process(request.parameters)

  def enhance[R](f: => R): R = f
}
