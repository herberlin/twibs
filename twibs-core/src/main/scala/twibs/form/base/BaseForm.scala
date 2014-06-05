/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.form.base

import com.google.common.cache.{Cache, CacheBuilder}
import java.io.IOException
import java.util.concurrent.TimeUnit
import scala.Some
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.xml.NodeSeq
import twibs.form.base.Result.AfterFormDisplay
import twibs.form.base.Result.UseResponse
import twibs.util.JavaScript._
import twibs.util.XmlUtils._
import twibs.util._
import twibs.web._

trait Component extends TranslationSupport {
  def itemIsVisible: Boolean = true

  def itemIsRevealed: Boolean = true

  def itemIsEnabled: Boolean = true

  def anchestorIsVisible: Boolean = parent.itemIsVisible && parent.anchestorIsVisible

  def anchestorIsRevealed: Boolean = parent.itemIsRevealed && parent.anchestorIsRevealed

  def anchestorIsEnabled: Boolean = parent.itemIsEnabled && parent.anchestorIsEnabled

  def isVisible: Boolean = itemIsVisible && anchestorIsVisible

  def isRevealed: Boolean = itemIsRevealed && anchestorIsRevealed

  def isEnabled: Boolean = itemIsEnabled && anchestorIsEnabled

  final def itemIsDisabled: Boolean = !itemIsEnabled

  final def itemIsConcealed: Boolean = !itemIsRevealed

  final def itemIsHidden: Boolean = !itemIsVisible

  final def anchestorIsDisabled: Boolean = !anchestorIsEnabled

  final def anchestorIsConcealed: Boolean = !anchestorIsRevealed

  final def anchestorIsHidden: Boolean = !anchestorIsVisible

  final def isDisabled: Boolean = !isEnabled

  final def isConcealed: Boolean = !isRevealed

  final def isHidden: Boolean = !isVisible

  def reset(): Unit = Unit

  def prepare(request: Request): Unit = Unit

  def parse(request: Request): Unit = Unit

  def execute(request: Request): Unit = Unit

  def parent: BaseParentItem

  def form: BaseForm = parent.form

  override def translator: Translator = parent.translator

  parent.registerChild(this)
}

trait ParseOnPrepare extends Component {
  override def prepare(request: Request): Unit = super.parse(request)

  override def parse(request: Request): Unit = Unit
}

trait ComponentWithName extends Component {
  def ilk: String

  def id: IdString = parent.id ~ name

  final val name: String = {
    val names = form.items.collect({ case e: ComponentWithName if e != this => e.name}).toList
    def recursive(n: String, i: Int): String = {
      val ret = n + (if (i == 0) "" else i)
      if (!names.contains(ret)) ret
      else recursive(n, i + 1)
    }
    recursive(parent.prefixForChildNames + ilk, 0)
  }

  override def translator: Translator = super.translator.usage(ilk)

  require(!ilk.isEmpty, "Empty ilk is not allowed")
  require(ilk matches "\\w+[\\w0-9-]*", "Ilk must start with character and contain only characters, numbers and -")
  require(name != BaseForm.PN_ID, s"'${BaseForm.PN_ID}' is reserved")
  require(name != BaseForm.PN_MODAL, s"'${BaseForm.PN_MODAL}' is reserved")
  require(name != ApplicationSettings.PN_NAME, s"'${ApplicationSettings.PN_NAME}' is reserved")
}

trait BaseParentItem extends Component with Validatable with RenderedItem {
  implicit def thisAsParent: BaseParentItem = this

  protected val _children = ListBuffer[Component]()

  def children = _children.toList

  private[base] def registerChild(child: Component): Unit = if( child != this ) _children += child

  def id: IdString

  def prefixForChildNames: String

  override def reset(): Unit = children.foreach(_.reset())

  override def prepare(request: Request): Unit = children.foreach(_.prepare(request))

  override def parse(request: Request): Unit = children.foreach(_.parse(request))

  override def execute(request: Request): Unit = children.foreach(_.execute(request))

  override def html: NodeSeq = children collect { case child: RenderedItem => child.enrichedHtml} flatten

  def items: Iterator[Component] = (children map {
    case withItems: BaseParentItem => withItems.items
    case item => Iterator.single(item)
  }).foldLeft(Iterator.single(this.asInstanceOf[Component]))(_ ++ _)

  def validate(): Boolean = {
    items.foreach {
      case i: Values => i._validated = true
      case i: MinMaxChildren => i._validated = true
      case _ =>
    }
    isValid
  }

  def isValid = children.forall {
    case i: Validatable => i.isValid
    case _ => true
  }
}

trait BaseItemContainer extends BaseParentItem with ComponentWithName {
  implicit override def thisAsParent: BaseItemContainer = this

  override def prefixForChildNames: String = parent.prefixForChildNames
}

class ItemContainer private(val ilk: String, val parent: BaseParentItem, unit: Unit = Unit) extends BaseItemContainer {
  def this(ilk: String)(implicit parent: BaseParentItem) = this(ilk, parent)
}

class Dynamic protected(val ilk: String, val dynamicId: String, val parent: BaseItemContainer, unit: Unit = Unit) extends BaseItemContainer {
  def this(ilk: String, dynamicId: String)(implicit parent: BaseItemContainer) = this(ilk, dynamicId, parent)

  override def prefixForChildNames: String = dynamicId

  override def html: NodeSeq = super.html ++ HiddenInputRenderer(parent.name, dynamicId)
}

trait MinMaxChildren extends BaseItemContainer {
  def minimumNumberOfDynamics = 0

  def maximumNumberOfDynamics = Int.MaxValue

  override def html = messageHtml ++ super.html

  override def isValid: Boolean = super.isValid && (!validated || Range(minimumNumberOfDynamics, maximumNumberOfDynamics).contains(children.size))

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

abstract class DynamicContainer[T <: Dynamic] private(val ilk: String, val parent: BaseParentItem, tag: ClassTag[T], unit: Unit = Unit) extends BaseItemContainer with MinMaxChildren {
  def this(ilk: String)(implicit parent: BaseParentItem, tag: ClassTag[T]) = this(ilk, parent, tag)

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

object BaseForm {
  val PN_ID = "form-id"

  val PN_MODAL = "form-modal"

  val deferredResponses: Cache[String, Response] = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build()
}

trait BaseForm extends BaseParentItem with CurrentRequestSettings {
  def name: String

  val id: IdString = Request.parameters.getString(BaseForm.PN_ID, IdGenerator.next())

  val modal = Request.parameters.getBoolean(BaseForm.PN_MODAL, default = false)

  def modalId = id ~ "modal"

  def contentId: IdString = id ~ "content"

  override def translator: Translator = super.translator.usage("FORM").usage(name)

  def actionLink: String = "/forms" + ClassUtils.toPath(getClassForActionLink(getClass))

  private def getClassForActionLink(classToCheck: Class[_]): Class[_] =
    if (classToCheck.isLocalClass) getClassForActionLink(classToCheck.getSuperclass) else classToCheck

  def actionLinkWithContextPathAndParameters: String = actionLinkWithContextPath + queryString

  def actionLinkWithContextPath: String = WebContext.path + actionLink

  private def queryString = {
    val keyValues = items.collect({ case item: BaseField if item.isModified => item.strings.map(string => item.name -> string)}).flatten.toList
    val all = if (ApplicationSettings.name != ApplicationSettings.DEFAULT_NAME) (ApplicationSettings.PN_NAME -> ApplicationSettings.name) :: keyValues else keyValues
    if (all.isEmpty) "" else "?" + all.map(e => e._1 + "=" + e._2).mkString("&")
  }

  def accessAllowed: Boolean

  def isPrepareAllowed = true

  def isParseAllowed = accessAllowed

  def isExecuteAllowed = accessAllowed

  def isDisplayAllowed = accessAllowed

  def displayJs: JsCmd

  val renderer: Renderer = new Renderer {
    override def renderMessage(message: Message): NodeSeq = message.text
  }

  def deferred(response: Response): Result.Value = {
    val id = IdGenerator.next()
    BaseForm.deferredResponses.put(id, response)
    AfterFormDisplay(JsCmd(s"location.href = '${deferredAction.executionLink(id)}'"))
  }

  private val deferredAction = new Executor("deferred-download") with Result {
    override def execute(strings: Seq[String]): Unit =
      result = strings.headOption.flatMap(id => Option(BaseForm.deferredResponses.getIfPresent(id)).map(response => UseResponse(response))) getOrElse (throw new IOException("File does not exists"))
  }

  def respond(request: Request): Response = {
    val result: List[Result.Value] = {
      if (isPrepareAllowed) prepare(request)
      if (isParseAllowed) parse(request)
      if (isExecuteAllowed) execute(request)
      items.collect { case r: Result if r.result != Result.Ignored => r.result}.toList
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

  // Form is the root item
  override def form = this

  override def parent = this

  override def prefixForChildNames: String = ""

  override def anchestorIsEnabled: Boolean = true

  override def anchestorIsRevealed: Boolean = true

  override def anchestorIsVisible: Boolean = true

}

trait Executable extends ComponentWithName {
  override def execute(request: Request): Unit = request.parameters.getStringsOption(name).foreach(execute)

  def execute(strings: Seq[String]): Unit

  def executionLink(string: String) = form.actionLinkWithContextPath + "?" + name + "=" + string
}

trait ExecuteValidated extends Executable {
  def execute(parameters: Seq[String]): Unit = if (callValidation()) executeValidated()

  def callValidation() = form.validate()

  def executeValidated(): Unit
}

trait SubmitOnChange extends BaseField {
  override def submitOnChange = true
}

abstract class Executor(val ilk: String)(implicit val parent: BaseParentItem) extends Executable {

}

trait BaseField extends ComponentWithName with Values {
  def submitOnChange = false

  override def parse(request: Request): Unit = request.parameters.getStringsOption(name).foreach(parse)

  def parse(parameters: Seq[String]): Unit = strings = parameters

  /* Convenience methods */
  def input = inputs.head

  def string = strings.head

  def string_=(string: String) = strings = string :: Nil

  def value = values.head

  def value_=(value: ValueType) = values = value :: Nil

  def valueOption = values.headOption

  def valueOption_=(valueOption: Option[ValueType]) = valueOption.map(v => values = v :: Nil)

  override def reset(): Unit = resetInputs()
}

class LazyCacheItem[T](calculate: => T)(implicit val parent: BaseParentItem) extends LazyCache[T] with Component {
  private val lazyCache = LazyCache(calculate)

  def valueOption: Option[T] = lazyCache.valueOption

  def value: T = lazyCache.value

  override def reset(): Unit = {
    super.reset()
    lazyCache.reset()
  }
}
