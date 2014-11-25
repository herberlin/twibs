/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.joran.spi.JoranException
import ch.qos.logback.core.util.StatusPrinter
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler
import com.google.common.base.Stopwatch

object Logger extends Loggable {
  private def removeDollarFromString(string: String) = string.replace("$", "")

  def apply(name: String): Logger = new DefaultLogger(LoggerFactory getLogger name)

  def apply(loggerClass: Class[_]): Logger = apply(removeDollarFromString(loggerClass.getName))

  def apply(loggerObject: AnyRef): Logger = apply(loggerObject.getClass)

  private def context = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]

  SLF4JBridgeHandler.removeHandlersForRootLogger()
  SLF4JBridgeHandler.install()

  def init(): Unit = ()

  def loadConfiguration(configuration: String): Unit =
    if (configuration != "test" && configuration != "") {
      val propertiesName = s"/logback-$configuration.xml"
      Option(getClass.getResource(propertiesName)) match {
        case Some(properties) => try {
          val configurator = new JoranConfigurator()
          configurator.setContext(context)
          context.reset()
          configurator.doConfigure(properties)
          logger.info(s"Loaded logging configuration '$propertiesName'")
        } catch {
          case e: JoranException => // IGNORED
        }
          StatusPrinter.printInCaseOfErrorsOrWarnings(context)
        case None => logger.info(s"Logging configuration '$propertiesName' not found")
      }
    }
}

trait Logger {
  lazy val name = internalLogger.getName

  protected def internalLogger: org.slf4j.Logger

  def isErrorEnabled = internalLogger.isErrorEnabled

  def isWarnEnabled = internalLogger.isWarnEnabled

  def isInfoEnabled = internalLogger.isInfoEnabled

  def isDebugEnabled = internalLogger.isDebugEnabled

  def error(msg: => String): Unit =
    if (internalLogger.isErrorEnabled) internalLogger.error(msg)

  def error(msg: => String, t: Throwable): Unit =
    if (internalLogger.isErrorEnabled) internalLogger.error(msg, t)

  def warn(msg: => String): Unit =
    if (internalLogger.isWarnEnabled) internalLogger.warn(msg)

  def warn(msg: => String, t: Throwable): Unit =
    if (internalLogger.isWarnEnabled) internalLogger.warn(msg, t)

  def info(msg: => String): Unit =
    if (internalLogger.isInfoEnabled) internalLogger.info(msg)

  def info(msg: => String, t: Throwable): Unit =
    if (internalLogger.isInfoEnabled) internalLogger.info(msg, t)

  def debug(msg: => String): Unit =
    if (internalLogger.isDebugEnabled) internalLogger.debug(msg)

  def debug(msg: => String, t: Throwable): Unit =
    if (internalLogger.isDebugEnabled) internalLogger.debug(msg, t)

  def trace(msg: => String): Unit =
    if (internalLogger.isTraceEnabled) internalLogger.trace(msg)

  def trace(msg: => String, t: Throwable): Unit =
    if (internalLogger.isTraceEnabled) internalLogger.trace(msg, t)

  def debugWithTiming[A](msg: => String)(alwaysExecuted: => A) = {
    if (isDebugEnabled) {
      debug(s"Start: $msg")
      val sw = Stopwatch.createStarted()
      val result = alwaysExecuted
      debug(s"End: $msg (in $sw)")
      result
    } else alwaysExecuted
  }

  def infoWithTiming[A](msg: => String)(alwaysExecuted: => A) = {
    if (isInfoEnabled) {
      info(s"Start: $msg")
      val sw = Stopwatch.createStarted()
      val result = alwaysExecuted
      info(s"End: $msg (in $sw)")
      result
    } else alwaysExecuted
  }
}

private[util] final class DefaultLogger(override protected val internalLogger: org.slf4j.Logger) extends Logger

trait Loggable {
  lazy val logger = Logger(this.getClass)
}
