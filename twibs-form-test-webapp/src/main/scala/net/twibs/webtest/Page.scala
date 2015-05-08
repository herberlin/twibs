/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.webtest

import net.twibs.util.Formatters._
import net.twibs.util.Predef._
import net.twibs.util.{ClassUtils, GetMethod, Request}
import net.twibs.web._
import org.threeten.bp.Instant

import scala.xml.Unparsed

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

  //          {new TestForm().inlineHtml}

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
        <link rel="stylesheet" href="/clientlibs/twibs.css"/>
        <link rel="stylesheet" href="/clientlibs/twibs-form.css"/>
        <link rel="shortcut icon" href="/favicon.ico"/>
      </head>
      <body>
        <div class="container">
          {val f = new RadioTestForm()
          f.singleValue.validate()
          f.inlineHtml}
          <div>
            <p>
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
              Lots of text<br />
            </p>

            <div class="twibs-notify">
              <div class="alert alert-danger">This is an error message</div>
              <div class="alert alert-info">The info text</div>
              <div class="alert alert-warning">Some more errors</div>
            </div>
          </div>
        </div>
      </body>
        {Unparsed( """
          <!--[if lt IE 9]>
              <script src="http://code.jquery.com/jquery-1.11.2.min.js"></script>
              <script src="https://cdnjs.cloudflare.com/ajax/libs/respond.js/1.4.2/respond.min.js"></script>
              <script src="https://cdnjs.cloudflare.com/ajax/libs/html5shiv/3.7.2/html5shiv-printshiv.min.js"></script>
          <![endif]-->
          <!--[if (gte IE 9) | (!IE)]><!-->
              <script src="/clientlibs/jquery.js"></script>
          <!--<![endif]-->
                   """)}
        <script src="/clientlibs/twibs.js"></script>
        <script src="/clientlibs/twibs-form.js"></script>
    </html>
}
