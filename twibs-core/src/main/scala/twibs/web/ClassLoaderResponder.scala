package twibs.web

import java.net.URL

class ClassLoaderResponder(classLoader: ClassLoader, prefixPath: String) extends ResourceResponder {
  def getResourceOption(request: Request): Option[URL] = Option(classLoader.getResource(withPrefixPath(request.path)))

  private def withPrefixPath(path: String) = removeTrailingSlash(prefixPath + path)

  private def removeTrailingSlash(path: String) = if (path.startsWith("/")) path.substring(1) else path
}
