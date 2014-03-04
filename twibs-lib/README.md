# Twibs

This is the Documentation for Twibs Version 5 (V5).

## Date picker component

Two formats need to be defined:

1. "data-time-format": Java Format described [here](http://download.java.net/jdk8/docs/api/java/time/format/DateTimeFormatter.html). Example "dd.MM.yyy HH:mm"

2. "date-time-format-browser": JavaScript Format described [here](http://www.malot.fr/bootstrap-datetimepicker). Example "yyyy-mm-ddThh:ii:ssZ"

```scala
class Example(name: String) {
  val field: Option[Int] = None
}
```
## Deprecations

The use of *joda-time* is deprecated. Use *threeten (backport)* instead. Will be removed in V6.
