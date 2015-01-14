/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import java.io.File

import net.twibs.util.{Request, GetMethod}

class FileResponder(baseDir: File) extends Responder {
  def respond(request: Request): Option[Response] = {
    if (request.method != GetMethod) None
    else {
      val fileArg = new File(baseDir, request.path)

      if (fileArg.exists() && fileArg.isFile && fileArg.canRead)
        Some(new FileResponse() with CacheableResponse {
          def file = fileArg
        })
      else None
    }
  }
}
