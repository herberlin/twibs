/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import twibs.db.Database

class GwbiDatabase extends Database {
  def password: String = ""

  def username: String = "gwbi"

  def url: String = "jdbc:postgresql://localhost/gwbi_prod"

  def driver: String = "org.postgresql.Driver"
}
