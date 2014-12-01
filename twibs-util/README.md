# Overview

This module is part of [twibs](https://github.com/hombre/twibs). See there for licence and build information.

# Binary Releases

Can be found on Maven Central (Java 7 and Scala 2.10).

    <dependency>
        <groupId>net.twibs</groupId>
        <artifactId>twibs-util</artifactId>
        <version>0.12</version>
    </dependency>

# What twibs-util can do for you

## Run modes

Specified with `java -Drun.mode=(development|test|staging|production) ...` at startup (default: `production`).

Use eg `RunMode.isProduction` to choose between different behaviours in code.

`test` is used if Stacktrace contains `org.scalatest.tools.Runner` and no run mode defined.

Switch RunMode using eg `System.copy(runMode = RunMode.STAGING).use { yourFunction }`. Mainly used in unit test.

## Logging

Logging is configured to be routed to [SLF4j](http://www.slf4j.org/) for logging frameworks jcl, jul, log4j on use of first logger.

[Logback](http://logback.qos.ch/) is used as default logger (must be excluded if other logging mechanism should be used).

`Logger` is a thin wrapper around SLF4j that used called by name parameters, so calling eg `logger.isDebugEnabled` can be ommited before calling `logger.debug(...)`

Use `Logger.getLogger(class|object|name)` to get a logger.

Use `trait Loggable` to get a `logger` for the implementing class.

Use `StartupTimeBasedTriggeringPolicy` to roll a log file on initialisation.

    <appender name="..." class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <timeBasedFileNamingAndTriggeringPolicy class="net.twibs.util.StartupTimeBasedTriggeringPolicy"/>
            ...
        </rollingPolicy>
        ...
    </appender>

# Other software

* Configuration is done with [Typesafe Config](https://github.com/typesafehub/config)
