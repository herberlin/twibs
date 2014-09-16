/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import java.io.IOException
import java.util.concurrent.TimeUnit

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.xml.NodeSeq

import twibs.form.base.ComponentState.ComponentState
import twibs.form.base.Result.AfterFormDisplay
import twibs.util.JavaScript._
import twibs.util.XmlUtils._
import twibs.util._
import twibs.web._

import com.google.common.cache.{Cache, CacheBuilder}

trait Component extends TranslationSupport {
  def state: ComponentState = parent.state

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

  def disabledName = name + "-disabled"

  protected def computeName: String = {
    val names = form.components.map(_.name).toList
    def recursive(n: String, i: Int): String = {
      val ret = n + (if (i == 0) "" else i)
      if (!names.contains(ret)) ret
      else recursive(n, i + 1)
    }
    recursive(parent.prefixForChildNames + ilk, 0)
  }

  def asHtml: NodeSeq = NodeSeq.Empty

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

  override def asHtml: NodeSeq = {
    import ComponentState._
    state match {
      case Ignored => NodeSeq.Empty
      case Hidden =>
        <div class="concealed">{containerAsHtml}</div>
      case _ => containerAsDecoratedHtml
    }
  }

  def containerAsDecoratedHtml: NodeSeq = containerAsHtml

  protected def containerAsHtml: NodeSeq = children collect { case child: Floating => NodeSeq.Empty case child => child.asHtml} flatten

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

  protected override def containerAsHtml = messageHtml ++ super.containerAsHtml

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

  override protected def containerAsHtml = super.containerAsHtml ++ form.renderer.hiddenInput(parent.name, dynamicId)
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

  override protected def containerAsHtml = defaultButtonHtml ++ super.containerAsHtml

  def defaultButtonHtml = defaultExecutableOption.fold(NodeSeq.Empty)(form.renderer.renderAsDefaultExecutable)

  def defaultExecutableOption = components.collectFirst { case e: DefaultExecutable if e.state.isEnabled => e}

  def modalId = id ~ "modal"

  def contentId = id ~ "content"

  override def translator = super.translator.usage("FORM").usage(name)

  def actionLink = "/forms" + ClassUtils.toPath(getClassForActionLink(getClass))

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

  def reloadFormJs = displayJs

  def displayJs = Request.method match {
    case GetMethod => openModalJs
    case PostMethod => refreshJs
    case _ => JsEmpty
  }

  def openModalJs = jQuery("body").call("append", modalHtml) ~ jQuery(modalId).call("twibsModal") ~ javascript

  def modalHtml: NodeSeq

  def refreshJs = replaceContentJs ~ javascript ~ focusJs

  def javascript: JsCmd = if (!state.isIgnored) components.collect({ case component: JavascriptComponent => component.javascript}) else JsEmpty

  def focusJs = components.collectFirst({ case field: BaseField if field.needsFocus => field.focusJs}) getOrElse JsEmpty

  def replaceContentJs = jQuery(contentId).call("html", asHtml)

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

  override def state = ComponentState.Enabled.hideIf(!accessAllowed)
}

trait Executable extends InteractiveComponent {
  override def execute(request: Request): Unit = request.parameters.getStringsOption(name).foreach(parameters => if (state.isEnabled) execute())

  def execute(): Unit = if (callValidation()) executeValidated()

  def callValidation() = form.validate()

  def executeValidated(): Unit = Unit

  def executionLink(value: ValueType) = form.actionLinkWithContextPath + "?" + name + "=" + valueToString(value)

  def commitLink(value: ValueType) = form.actionLinkWithContextPathAndParameters(name -> valueToString(value))
}

abstract class Executor(override val ilk: String)(implicit val parent: Container) extends Executable with Result with Floating

trait DefaultExecutable extends Executable

trait InteractiveComponent extends Component with Values {
  override def parse(request: Request): Unit = {
    import ComponentState._
    state match {
      case Enabled => request.parameters.getStringsOption(name).foreach(parse)
      case Disabled | Hidden => request.parameters.getStringsOption(disabledName).foreach(parse)
      case Ignored =>
    }
  }

  def parse(parameters: Seq[String]): Unit = strings = parameters

  override def reset(): Unit = resetInputs()

  def link(value: ValueType) = form.actionLinkWithContextPathAndParameters(name -> valueToString(value))

  def clearLink = form.actionLinkWithContextPathAndParameters(name -> "")

  override def validated: Boolean = state.isEnabled && super.validated
}

trait BaseField extends InteractiveComponent with Validatable {
  def submitOnChange = false

  def needsFocus = state.isEnabled && !isValid

  def focusJs = jQuery(id).call("focus")
}

trait RequiredIfEnabled extends BaseField {
  private def checkEnabled(input: Input) = if (state.isEnabled) input else input.terminate()

  override def stringProcessors: List[StringProcessor] = checkEnabled _ :: super.stringProcessors

  override def required = true
}

trait SubmitOnChange extends BaseField {
  override def submitOnChange = true

  def isSubmittedOnChange = Request.parameters.getString("form-change", "") == name
}

trait UseLastParameterOnly extends BaseField {
  override def parse(parameters: Seq[String]): Unit = super.parse(parameters.lastOption.map(_ :: Nil) getOrElse Nil)
}

class LazyCacheComponent[T](calculate: => T)(implicit val parent: Container) extends LazyCache[T] with Floating {
  private val lazyCache = LazyCache(calculate)

  def valueOption: Option[T] = lazyCache.valueOption

  def value: T = lazyCache.value

  override def reset(): Unit = {
    super.reset()
    lazyCache.reset()
  }
}
