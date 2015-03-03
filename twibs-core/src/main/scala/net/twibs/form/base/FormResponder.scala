/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form.base

import net.twibs.util.{Request, GetMethod, PostMethod}
import net.twibs.web._

class FormResponder(makeForm: () => BaseForm) extends Responder {
  lazy val actionLink = enhance(makeForm().actionLink)

  def respond(request: Request): Option[Response] =
    if (request.path.string == actionLink) process(request)
    else None

  def process(request: Request): Option[Response] =
    request.method match {
      case GetMethod | PostMethod => Some(enhance(parse(request)))
      case _ => None
    }

  def parse(request: Request) = makeForm().respond(request)

  def enhance[R](f: => R): R = f
}
