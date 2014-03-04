package twibs.web

trait Responder {
  def respond(request: Request): Option[Response]

  def useAndRespond(request: Request): Option[Response] = request.use {respond(request)}
}

class ResponderChain(list: List[Responder]) extends Responder {
  def respond(request: Request): Option[Response] = list.view.flatMap(_.respond(request)).headOption
}

object Responder {
  implicit def apply(list: List[Responder]) = new ResponderChain(list)
}
