package twibs.util

object ClassUtils {
  def getCompilationTime(clazz: Class[_]) = clazz.getResource(toPath(clazz) + ".class").toURI.toURL.openConnection.getLastModified

  def toId(clazz: Class[_]): String = StringUtils.convertToComputerLabel(clazz.getName).replace('.', '-').replace('_', '-')

  def toPath(clazz: Class[_]): String = "/" + clazz.getName.replace(".", "/")
}
