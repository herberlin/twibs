/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

object ClassUtils {
  def getCompilationTime(clazz: Class[_]) = clazz.getResource(toPath(clazz) + ".class").toURI.toURL.openConnection.getLastModified

  def toId(clazz: Class[_]): String = StringUtils.convertToComputerLabel(clazz.getName).replace('.', '-').replace('_', '-')

  def toPath(clazz: Class[_]): String = "/" + clazz.getName.replace(".", "/")
}
