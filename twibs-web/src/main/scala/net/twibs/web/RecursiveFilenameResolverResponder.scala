/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

abstract class RecursiveFilenameResolverResponder(contentResponder: Responder, filename: String) extends Responder {
  def respondWithFilename(request: Request) = respondRecursive(request.relative(filename))

  private def respondRecursive(request: Request): Option[Response] = {
    contentResponder.respond(request) match {
      case None =>
        if (request.path == "/" + filename) None
        else respondRecursive(request.relative("../" + filename))
      case responseOption => responseOption
    }
  }
}
