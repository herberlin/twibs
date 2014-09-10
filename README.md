Utility classes to ease the development of internet applications with scala.

# Overview

* AJAX Forms defined in Scala
* pure server side form validation
* runtime server side JS/CSS Compression and LESS processing
* complex inheritable translation mechanism

# License

The license is Apache 2.0, see LICENSE-2.0.txt.

# Binary Releases

Can be found on Maven Central (Java 7 and Scala 2.10).

    <dependency>
        <groupId>net.twibs</groupId>
        <artifactId>twibs-core</artifactId>
        <version>0.10</version>
    </dependency>

# API docs

Are published on Maven Central.

# Build

Is done with plain maven.

# Java and Scala version

Currently the library is maintained against Java 7 and Scala 2.11.

# Other software

* Configuration is done with [Typesafe Config](https://github.com/typesafehub/config)

# Run modes

Specified with `java -Drun.mode=(development|test|staging|production) ...` at startup (default: `production`).

`test` is used if Stacktrace contains `org.scalatest.tools.Runner` and no run mode defined.

## Date picker component

Two formats need to be defined in configuration:

1. "data-time-format": Java Format described [here](http://download.java.net/jdk8/docs/api/java/time/format/DateTimeFormatter.html). Example "dd.MM.yyy HH:mm"

2. "date-time-format-browser": JavaScript Format described [here](http://www.malot.fr/bootstrap-datetimepicker). Example "yyyy-mm-ddThh:ii:ssZ"

# Field states

- Enabled:         shown,     validated,     submitted
- Disabled:       shown, not validated,     submitted (encrypted)
- Hidden:     not shown, not validated,     submitted (encrypted)
- Unrendered: not shown, not validated, not submitted
