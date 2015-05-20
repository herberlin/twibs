/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.form

import net.twibs.util.JavaScript._
import net.twibs.web._

import scala.languageFeature.dynamics

trait Result

case object Ignored extends Result

case class AfterFormDisplay(js: JsCmd) extends Result

case class BeforeFormDisplay(js: JsCmd) extends Result

case class InsteadOfFormDisplay(js: JsCmd) extends Result

case class UseResponse(response: Response) extends Result
