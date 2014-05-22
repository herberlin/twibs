/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.db

class MemoryDatabase extends Database {
  def username = "sa"

  def password = ""

  def url = "jdbc:h2:mem:db1"

  def driver = "org.h2.Driver"

  override def migrationLocations: List[String] = "memory-db/migration" :: Nil
}
