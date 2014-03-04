package twibs.util

class GwbiDatabase extends Database {
  def password: String = ""

  def username: String = "gwbi"

  def url: String = "jdbc:postgresql://localhost/gwbi_prod"

  def driver: String = "org.postgresql.Driver"
}
