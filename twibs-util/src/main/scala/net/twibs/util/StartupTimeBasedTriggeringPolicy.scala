/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.util

import ch.qos.logback.core.joran.spi.NoAutoStart
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

@NoAutoStart
class StartupTimeBasedTriggeringPolicy[E] extends SizeAndTimeBasedFNATP[E] {
  val first = new AtomicBoolean()

  override def isTriggeringEvent(activeFile: File, event: E): Boolean =
    if (first.compareAndSet(false, true)) {
      setMaxFileSize("1")
      super.isTriggeringEvent(activeFile, event)
      true
    } else false
}
