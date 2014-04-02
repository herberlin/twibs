/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

class MemoryDatabase extends Database {
  def username = "sa"

  def password = ""

  def url = "jdbc:h2:mem:db1"

  def driver = "org.h2.Driver"
}
