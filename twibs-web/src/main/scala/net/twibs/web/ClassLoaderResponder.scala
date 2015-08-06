/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import java.net.URL

import net.twibs.util.{Path, Request}

class ClassLoaderResponder(classLoader: ClassLoader, prefixPath: Path) extends ResourceResponder {
  def getResourceOption(request: Request): Option[URL] = Option(classLoader.getResource(withPrefixPath(request.path).string))

  private def withPrefixPath(path: Path) = Path(prefixPath.parts ++ path.parts, path.suffix, absolute = false)
}
