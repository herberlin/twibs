package twibs.form.base

import twibs.util.IdGenerator
import twibs.util.JavaScript.JsCmd
import twibs.web._

class FormResponder(makeForm: () => BaseForm) extends Responder {
  lazy val actionLink = enhance(makeForm().actionLink)

  def respond(request: Request): Option[Response] =
    if (request.path == actionLink) process(request)
    else None

  def process(request: Request): Option[Response] =
    request.method match {
      case GetMethod | PostMethod => Some(enhance(parse(request)))
      case _ => None
    }

  def parse(request: Request) = {
    BaseForm.use(request.parameters.getString(BaseForm.PN_ID, IdGenerator.next()), request.parameters.getBoolean(BaseForm.PN_MODAL, default = false)) {
      val form = makeForm()

      val result: List[Result.Value] =
        if (form.accessAllowed) {
          form.prepare(request)
          form.parse(request)
          form.execute(request)
          form.items.collect {case r: Result if r.result != Result.Ignored => r.result}.toList
        } else Nil

      result.collectFirst {case Result.UseResponse(response) => response} match {
        case Some(response) => response
        case None =>

          val beforeDisplayJs = result.collect {case Result.BeforeFormDisplay(js) => js}

          val displayJs = result.collect {case Result.InsteadOfFormDisplay(js) => js} match {
            case Nil => form.displayJs :: Nil
            case l => l
          }

          val afterDisplayJs = result.collect {case Result.AfterFormDisplay(js) => js}

          val javascript: JsCmd = beforeDisplayJs ::: displayJs ::: afterDisplayJs

          new StringResponse with VolatileResponse with TextMimeType {
            val asString = javascript.toString
          }
      }
    }
  }

  def enhance[R](f: => R): R = f
}
