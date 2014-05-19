/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.db

//TODO: Replace PostgresDriver with generic type once someone knows how ;)
import scala.slick.driver.PostgresDriver.simple.{Column => SlickColumn}

trait NamedColumns {
  def allColumns: Product

  def findColumnByName(name: String) = allColumns.productIterator.collectFirst {case c: SlickColumn[_] if c.toString().endsWith(name) => c}.get
}
