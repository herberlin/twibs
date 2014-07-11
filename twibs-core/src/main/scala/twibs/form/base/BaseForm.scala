/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import java.io.IOException
import java.util.concurrent.TimeUnit

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.xml.{Elem, NodeSeq}

import twibs.form.base.Result.AfterFormDisplay
import twibs.util.JavaScript._
import twibs.util.XmlUtils._
import twibs.util._
import twibs.web._

import com.google.common.cache.{Cache, CacheBuilder}

trait Component extends TranslationSupport {
  def selfIsVisible: Boolean = true

  def selfIsRevealed: Boolean = true

  def selfIsEnabled: Boolean = true

  def anchestorIsVisible: Boolean = parent.selfIsVisible && parent.anchestorIsVisible

  def anchestorIsRevealed: Boolean = parent.selfIsRevealed && parent.anchestorIsRevealed

  def anchestorIsEnabled: Boolean = parent.selfIsEnabled && parent.anchestorIsEnabled

  def isVisible: Boolean = selfIsVisible && anchestorIsVisible

  def isRevealed: Boolean = selfIsRevealed && anchestorIsRevealed

  def isEnabled: Boolean = selfIsEnabled && anchestorIsEnabled

  final def selfIsDisabled: Boolean = !selfIsEnabled

  final def selfIsConcealed: Boolean = !selfIsRevealed

  final def selfIsHidden: Boolean = !selfIsVisible

  final def anchestorIsDisabled: Boolean = !anchestorIsEnabled

  final def anchestorIsConcealed: Boolean = !anchestorIsRevealed

  final def anchestorIsHidden: Boolean = !anchestorIsVisible

  final def isDisabled: Boolean = !isEnabled

  final def isConcealed: Boolean = !isRevealed

  final def isHidden: Boolean = !isVisible

  def reset(): Unit = Unit

  def initialize(): Unit = Unit

  def prepare(request: Request): Unit = Unit

  def parse(request: Request): Unit = Unit

  def execute(request: Request): Unit = Unit

  def parent: Container

  def form: BaseForm = parent.form

  override def translator: Translator = parent.translator.usage(ilk)

  def ilk: String = "unknown"

  def id: IdString = parent.id ~ name

  final val name: String = computeName

  protected def computeName: String = {
    val names = form.components.map(_.name).toList
    def recursive(n: String, i: Int): String = {
      val ret = n + (if (i == 0) "" else i)
      if (!names.contains(ret)) ret
      else recursive(n, i + 1)
    }
    recursive(parent.prefixForChildNames + ilk, 0)
  }

  def html: NodeSeq

  final def enrichedHtml: NodeSeq = selfIsVisible match {
    case false => NodeSeq.Empty
    case true => isRevealed match {
      case true => html
      case false =>
        html match {
          case s@NodeSeq.Empty => s
          case n: Elem => n.addClass(selfIsConcealed, "concealed")
          case n: NodeSeq => <div>{n}</div>.addClass(selfIsConcealed, "concealed")
        }
    }
  }

  parent.registerChild(this)

  require(!ilk.isEmpty, "Empty ilk is not allowed")
  require(ilk matches "\\w+[\\w0-9-]*", "Ilk must start with character and contain only characters, numbers and -")
  require(!name.endsWith(BaseForm.PN_ID_SUFFIX), s"Suffix '${BaseForm.PN_ID_SUFFIX}' is reserved")
  require(!name.endsWith(BaseForm.PN_MODAL_SUFFIX), s"Suffix '${BaseForm.PN_MODAL_SUFFIX}' is reserved")
  require(name != ApplicationSettings.PN_NAME, s"'${ApplicationSettings.PN_NAME}' is reserved")
}

trait JavascriptComponent extends Component {
  def javascript: JsCmd
}

trait ParseOnPrepare extends InteractiveComponent {
  override def prepare(request: Request): Unit = super.parse(request)

  override def parse(request: Request): Unit = Unit
}

trait Container extends Component with Validatable {
  implicit def thisAsParent: Container = this

  protected val _children = ListBuffer[Component]()

  def children = _children.toList

  private[base] def registerChild(child: Component): Unit = if (child != this) _children += child

  def prefixForChildNames: String = parent.prefixForChildNames

  override def reset(): Unit = children.foreach(_.reset())

  override def initialize(): Unit = children.foreach(_.initialize())

  override def prepare(request: Request): Unit = children.foreach(_.prepare(request))

  override def parse(request: Request): Unit = children.foreach(_.parse(request))

  override def execute(request: Request): Unit = children.foreach(_.execute(request))

  override def html: NodeSeq = children collect { case child: Floating => NodeSeq.Empty case child => child.enrichedHtml} flatten

  def components: Iterator[Component] = (children map {
    case container: Container => container.components
    case component => Iterator.single(component)
  }).foldLeft(Iterator.single(this.asInstanceOf[Component]))(_ ++ _)

  def validate(): Boolean = {
    components.foreach {
      case i: Values => i._validated = true
      case i: MinMaxContainer => i._validated = true
      case _ =>
    }
    isValid
  }

  def isValid = children.forall {
    case i: Validatable => i.isValid
    case _ => true
  }
}

trait MinMaxContainer extends Container {
  def minimumNumberOfDynamics = 0

  def maximumNumberOfDynamics = Int.MaxValue

  override def html = messageHtml ++ super.html

  override def isValid = super.isValid && (!validated || Range(minimumNumberOfDynamics, maximumNumberOfDynamics).contains(children.size))

  def messageOption: Option[Message] =
    if (validated && children.size < minimumNumberOfDynamics) Some(Message.warning(t"minimum-number-of-children-message: Please provide at least ${format(minimumNumberOfDynamics)} children"))
    else if (validated && children.size > maximumNumberOfDynamics) Some(Message.warning(t"maximum-number-of-chilren-message: Please provide no more than ${format(maximumNumberOfDynamics)} children"))
    else None

  private def format(i: Int) = form.formatters.integerFormat.format(i)

  def messageHtml: NodeSeq = messageOption match {
    case Some(message) => form.renderer.renderMessage(message)
    case _ => NodeSeq.Empty
  }

  override def reset(): Unit = {
    _validated = false
    super.reset()
  }

  def validated = _validated

  private[base] var _validated = false

  override def validate(): Boolean = {
    _validated = true
    super.validate()
  }
}

class StaticContainer private(override val ilk: String, val parent: Container, unit: Unit = Unit) extends Container {
  def this(ilk: String)(implicit parent: Container) = this(ilk, parent)
}

abstract class DynamicContainer[T <: Dynamic] private(override val ilk: String, val parent: Container, tag: ClassTag[T], unit: Unit = Unit) extends MinMaxContainer {
  def this(ilk: String)(implicit parent: Container, tag: ClassTag[T]) = this(ilk, parent, tag)

  override def reset(): Unit = {
    super.reset()
    _children --= dynamics
  }

  override def parse(request: Request): Unit = {
    request.parameters.getStringsOption(name).foreach(_.filterNot(_.isEmpty).foreach(recreate))
    super.parse(request)
  }

  def dynamics: List[T] = children collect { case child if tag.runtimeClass.isInstance(child) => child.asInstanceOf[T]}

  def recreate(dynamicId: String): T = dynamics.collectFirst { case child if child.dynamicId == dynamicId => child} getOrElse create(dynamicId)

  def create(dynamicId: String = IdGenerator.next()): T
}

class Dynamic protected(override val ilk: String, val dynamicId: String, val parent: Container, unit: Unit = Unit) extends Container {
  def this(ilk: String, dynamicId: String)(implicit parent: Container) = this(ilk, dynamicId, parent)

  override def prefixForChildNames: String = dynamicId

  override def html: NodeSeq = super.html ++ form.renderer.hiddenInput(parent.name, dynamicId)
}

object BaseForm {
  val PN_ID_SUFFIX = "-form-id"

  val PN_MODAL_SUFFIX = "-form-modal"

  val deferredResponses: Cache[String, Response] = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build()
}

trait BaseForm extends Container with CurrentRequestSettings {
  def pnId = ilk + BaseForm.PN_ID_SUFFIX

  def pnModal = ilk + BaseForm.PN_MODAL_SUFFIX

  override val id: IdString = Request.parameters.getString(pnId, IdGenerator.next())

  val modal = Request.parameters.getBoolean(pnModal, default = false)

  val formReload = new Executor("form-reload") with StringValues {
    override def execute(): Unit = result = InsteadOfFormDisplay(reloadFormJs)
  }

  override def html: NodeSeq = defaultButtonHtml ++ super.html

  private def defaultButtonHtml = components.collectFirst({ case e: DefaultExecutable => e.renderAsDefault}) getOrElse NodeSeq.Empty

  def modalId = id ~ "modal"

  def contentId: IdString = id ~ "content"

  override def translator: Translator = super.translator.usage("FORM").usage(name)

  def actionLink: String = "/forms" + ClassUtils.toPath(getClassForActionLink(getClass))

  private def getClassForActionLink(classToCheck: Class[_]): Class[_] =
    if (classToCheck.isLocalClass) getClassForActionLink(classToCheck.getSuperclass) else classToCheck

  def actionLinkWithContextPathAndParameters(parameters: (String, String)*): String = actionLinkWithContextPath + queryString(parameters: _*)

  def actionLinkWithContextPath: String = WebContext.path + actionLink

  private def queryString(parameters: (String, String)*) = {
    val keyValues = (pnId -> id.string) :: (pnModal -> modal) :: componentParameters ::: parameters.toList
    val all = if (ApplicationSettings.name != ApplicationSettings.DEFAULT_NAME) (ApplicationSettings.PN_NAME -> ApplicationSettings.name) :: keyValues else keyValues
    "?" + all.distinct.map(e => e._1 + "=" + e._2).mkString("&")
  }

  def componentParameters: List[(String, String)] = components.collect({ case component: BaseField if component.isModified => component.strings.map(string => component.name -> string)}).flatten.toList

  def accessAllowed: Boolean

  override def selfIsEnabled = accessAllowed

  override def selfIsRevealed = accessAllowed

  def reloadFormJs = displayJs

  def displayJs = Request.method match {
    case GetMethod => openModalJs
    case PostMethod => refreshJs
    case _ => JsEmpty
  }

  def openModalJs = jQuery("body").call("append", modalHtml) ~ jQuery(modalId).call("twibsModal") ~ javascript

  def modalHtml: NodeSeq

  def refreshJs = replaceContentJs ~ javascript ~ focusJs

  def javascript: JsCmd = if (isVisible) components.collect({ case component: JavascriptComponent => component.javascript}) else JsEmpty

  def focusJs = components.collectFirst({ case field: BaseField if field.needsFocus => field.focusJs}) getOrElse JsEmpty

  def replaceContentJs = jQuery(contentId).call("html", enrichedHtml)

  def hideModalJs = jQuery(modalId).call("modal", "hide")

  def renderer: Renderer

  def deferred(response: Response): Result.Value = {
    val id = IdGenerator.next()
    BaseForm.deferredResponses.put(id, response)
    AfterFormDisplay(JsCmd(s"location.href = '${deferredAction.executionLink(id)}'"))
  }

  private val deferredAction = new Executor("deferred-download") with StringValues {
    override def execute(): Unit =
      result = values.headOption.flatMap(id => Option(BaseForm.deferredResponses.getIfPresent(id)).map(response => UseResponse(response))) getOrElse (throw new IOException("File does not exists"))
  }

  def respond(request: Request): Response = {
    val result: List[Result.Value] = {
      prepare(request)
      parse(request)
      execute(request)
      components.collect { case r: Result if r.result != Result.Ignored => r.result}.toList
    }

    result.collectFirst { case Result.UseResponse(response) => response} match {
      case Some(response) => response
      case None =>

        val beforeDisplayJs = result.collect { case Result.BeforeFormDisplay(js) => js}

        val insteadOfFormDisplayJs = result.collect { case Result.InsteadOfFormDisplay(js) => js} match {
          case Nil => displayJs :: Nil
          case l => l
        }

        val afterDisplayJs = result.collect { case Result.AfterFormDisplay(js) => js}

        val javascript: JsCmd = beforeDisplayJs ::: insteadOfFormDisplayJs ::: afterDisplayJs

        new StringResponse with VolatileResponse with TextMimeType {
          val asString = javascript.toString
        }
    }
  }

  // Form is the root component
  override def form = this

  override def parent = this

  override def prefixForChildNames: String = ""

  override def anchestorIsEnabled: Boolean = true

  override def anchestorIsRevealed: Boolean = true

  override def anchestorIsVisible: Boolean = true
}

trait Executable extends InteractiveComponent {
  override def execute(request: Request): Unit = request.parameters.getStringsOption(name).foreach(parameters => if (isEnabled) execute())

  def execute(): Unit = if (callValidation()) executeValidated()

  def callValidation() = form.validate()

  def executeValidated(): Unit = Unit

  def executionLink(value: ValueType) = form.actionLinkWithContextPath + "?" + name + "=" + valueToString(value)

  def commitLink(value: ValueType) = form.actionLinkWithContextPathAndParameters(name -> valueToString(value))
}

abstract class Executor(override val ilk: String)(implicit val parent: Container) extends Executable with Result with Floating {
  override def html: NodeSeq = NodeSeq.Empty
}

trait DefaultExecutable extends Executable {
  def renderAsDefault = <input type="submit" class="concealed" tabindex="-1" name={name} value="" />
}

trait InteractiveComponent extends Component with Values {
  override def parse(request: Request): Unit = request.parameters.getStringsOption(name).foreach(parse)

  def parse(parameters: Seq[String]): Unit = strings = parameters

  override def reset(): Unit = resetInputs()

  def link(value: ValueType) = form.actionLinkWithContextPathAndParameters(name -> valueToString(value))

  def clearLink = form.actionLinkWithContextPathAndParameters(name -> "")
}

trait BaseField extends InteractiveComponent with Validatable {
  def submitOnChange = false

  def needsFocus = !isDisabled && !isValid

  def focusJs = jQuery(id).call("focus")
}

trait RequiredIfRevealed extends BaseField {
  private def checkRevealed(input: Input) = if (isRevealed) input else input.terminate()

  override def stringProcessors: List[StringProcessor] = checkRevealed _ :: super.stringProcessors

  override def required = true
}

trait SubmitOnChange extends BaseField {
  override def submitOnChange = true

  def isSubmittedOnChange = Request.parameters.getString("form-change", "") == name
}

trait UseLastParameterOnly extends BaseField {
  override def parse(parameters: Seq[String]): Unit = super.parse(parameters.lastOption.map(_ :: Nil) getOrElse Nil)
}

class LazyCacheComponent[T](calculate: => T)(implicit val parent: Container) extends LazyCache[T] with Component with Floating {
  private val lazyCache = LazyCache(calculate)

  def valueOption: Option[T] = lazyCache.valueOption

  def value: T = lazyCache.value

  override def reset(): Unit = {
    super.reset()
    lazyCache.reset()
  }

  override def html: NodeSeq = NodeSeq.Empty
}
