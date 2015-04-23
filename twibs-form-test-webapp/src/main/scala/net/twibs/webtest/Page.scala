/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.webtest

import net.twibs.util.{Request, GetMethod, ClassUtils}
import net.twibs.util.Formatters._
import net.twibs.util.Predef._
import net.twibs.web._
import org.threeten.bp.Instant

class Page extends Responder {
  def respond(request: Request): Option[Response] =
    if (request.path.string == "/index.html" && request.method == GetMethod) Some(indexResponse)
    else None

  def indexResponse = new StringResponse with VolatileResponse with HtmlMimeType {
    val asString = plainString
  }

  def plainString = doctype + html.toString

  def doctype = "<!DOCTYPE html>"

  val modified = Instant.ofEpochMilli(ClassUtils.getCompilationTime(getClass)).toLocalDateTime

  def yearString = modified.getYear.toString

  def dateString = modified.formatAsIso

  def html =
    <html lang="de" class="no-js">
      <head>
        <title>Twibs Webtest</title>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="copyright" content={s"$yearString Twibs"}/>
        <meta name="author" content="Twibs Demo"/>
        <meta name="date" content={dateString}/>
        <meta name="description" content="Twibs Webtest"/>
        <meta name="keywords" content="Java, Scala, CMS, Software"/>
        <link rel="stylesheet" href="/inc/foreign.css"/>
        <link rel="stylesheet" href="/inc/default.css"/>
        <link rel="shortcut icon" href="/favicon.ico"/>
        <script src="/inc/foreign.js"></script>
        <script src="/inc/default.js"></script>
      </head>
      <body>
        <div class="container">
          {new RadioTestForm().inlineHtml}
          {new TestForm().inlineHtml}
        </div>
      </body>
    </html>
}