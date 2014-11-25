/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.testutil

import java.io.File

import net.twibs.util.{Request, RunMode}
import net.twibs.web.Filter
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

trait TwibsJettySeleniumTest extends TwibsSeleniumTest {
  private var server: Server = null

  def baseUrl = s"http://localhost:$port"

  def filterClass: Class[_ <: Filter]

  def contextPath: String = ""

  def resourceBase: String

  def port = 9905

  override protected def beforeAll(): Unit = {
    startServer()
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    stopServer()
  }

  def startServer(): Unit = {
    System.setProperty(RunMode.SYSTEM_PROPERTY_NAME, RunMode.TEST.name)
    server = new Server(port)
    server.setStopAtShutdown(true)
    Request.activate(Request.copy(contextPath = contextPath))
    val webAppContext = new WebAppContext()
    webAppContext.setClassLoader(Thread.currentThread.getContextClassLoader)
    webAppContext.setContextPath(Request.contextPath)
    webAppContext.setResourceBase(new File(resourceBase).getAbsolutePath)
    webAppContext.addFilter(filterClass, "/*", null)
    server.setHandler(webAppContext)
    server.start()
  }

  def stopServer(): Unit = {
    server.stop()
    server.join()
    Request.deactivate(Request)
  }
}
