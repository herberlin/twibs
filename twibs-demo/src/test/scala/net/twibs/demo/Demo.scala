package net.twibs.demo

import java.awt.Desktop
import java.io.File
import java.net.URI

import net.twibs.util.RunMode
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

object Demo {
  def main(args: Array[String]): Unit = {
    val port = 9905
    System.setProperty(RunMode.SYSTEM_PROPERTY_NAME, RunMode.DEVELOPMENT.name)
    val server = new Server(port)
    server.setStopAtShutdown(true)
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
