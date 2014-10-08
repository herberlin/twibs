/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.demo

import java.awt.Desktop
import java.io.File
import java.net.URI

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import twibs.util.{RunMode, WebContext}

object Demo {
  def main(args: Array[String]): Unit = {
    val port = 9905
    System.setProperty("run.mode", RunMode.DEVELOPMENT.name)
    val server = new Server(port)
    server.setStopAtShutdown(true)
    WebContext.activate(new WebContext(""))
    val webAppContext = new WebAppContext()
    webAppContext.setClassLoader(Thread.currentThread.getContextClassLoader)
    webAppContext.setContextPath("")
    webAppContext.setResourceBase(new File("src/main/webapp").getAbsolutePath)
    webAppContext.addFilter(classOf[DemoFilter], "/*", null)
    server.setHandler(webAppContext)
    server.start()
    Desktop.getDesktop.browse(new URI(s"http://localhost:$port"))
  }
}
