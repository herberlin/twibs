package twibs.web

import twibs.util.{Configuration, Environment, Parameters}

class StaticRequest(val path: String,
                    val domain: String = "localhost",
                    val parameters: Parameters = Parameters(Map()),
                    val useCache: Boolean = true) extends Request with StaticAttributeContainer {
  val timestamp = Request.now()

  def method: RequestMethod = GetMethod

  def accept: List[String] = Nil

  def referrerOption: Option[String] = None

  def userAgentOption: Option[String] = None

  def remoteUserOption: Option[String] = None

  def doesClientSupportGzipEncoding: Boolean = false

  def remoteAddress: String = "::1"

  def uri: String = path

  def uploads = Map[String, Seq[Upload]]()

  val session = new StaticSession

  val locale = Environment.locale

  val config = Configuration.current
}
