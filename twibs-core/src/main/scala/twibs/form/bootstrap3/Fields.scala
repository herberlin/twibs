package twibs.form.bootstrap3

import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.{LocalDate, LocalDateTime}
import twibs.form.base._
import twibs.util.JavaScript._
import twibs.util.{InfoDisplayType, DangerDisplayType, Message, Translator}
import twibs.web.Upload
import xml._

trait ReadOnlyField extends Field {
  override def inputAsEnrichedHtml(input: Input, index: Int) =
    HiddenInputRenderer(name, input.string) ++ <p class="form-control-static">{input.title}</p>

  override def inputAsElem(input: Input) = <span></span>
}

trait Emptiable extends Field {
  override def inputAsEnrichedHtml(input: Input, index: Int): NodeSeq =
    <span class="emptiable">{super.inputAsEnrichedHtml(input, index)}<span class="input-clear glyphicon glyphicon-remove"></span></span>
}

trait FieldWithSuffixes extends Field {
  def suffixes: List[NodeSeq] = suffix :: Nil

  def suffix: NodeSeq = NodeSeq.Empty

  def surroundWithInputGroup(input: Input, nodeSeq: NodeSeq) = <div class="input-group">{nodeSeq}</div>

  override def inputAsEnrichedHtml(input: Input, index: Int): NodeSeq = {
    (suffixes.filterNot(_.isEmpty).map(s => <span class="input-group-addon">{ s }</span>), infoHtml) match {
      case (Nil, NodeSeq.Empty) => super.inputAsEnrichedHtml(input, index)
      case (suffixes, infoHtml) => surroundWithInputGroup(input, super.inputAsEnrichedHtml(input, index) ++ suffixes ++ infoHtml)
    }
  }

  protected override def infoHtml: NodeSeq = super.infoHtml match {
    case NodeSeq.Empty => NodeSeq.Empty
    case x => <span class="input-group-btn field-info">{x}</span>
  }
}

trait TextField extends FieldWithSuffixes {
  def placeholder = t"placeholder: Please enter text"
}

trait SingleLineField extends TextField {
  override def inputAsElem(input: Input) = <input type="text" placeholder={placeholder} value={input.string} />
}

trait AbstractDateTimeField extends SingleLineField with JavascriptItem {
  def datePickerOptions = Map("autoclose" -> autoClose, "pickerPosition" -> "bottom-left", "language" -> "de")

  def autoClose = true

  override def javascript = if (submitOnChange) initDateTimeJs.call("on", "changeDate", JsCmd("$(this).reloadForm()")) else initDateTimeJs

  private def initDateTimeJs = jQuery(inputGroupId).call("datetimepicker", datePickerOptions)

  private def inputGroupId = id + "_input_group"

  override def suffixes: List[NodeSeq] =
    if (isEnabled) <span class="glyphicon glyphicon-calendar"></span> :: super.suffixes
    else super.suffixes


  override def surroundWithInputGroup(input: Input, nodeSeq: NodeSeq): Elem =
    <div class="input-group date" id={inputGroupId} data-date={input.string} data-link-field={id} data-link-format={formatPatternForBrowser} data-date-format={formatPatternForBrowser} data-date-today-btn={todayButton} data-date-today-highlight={"" + todayHighlight}>{nodeSeq}</div>
      .add(!minimumFormattedForBrowser.isEmpty, "data-date-startdate", minimumFormattedForBrowser)
      .add(!maximumFormattedForBrowser.isEmpty, "data-date-enddate", maximumFormattedForBrowser)

  override def inputAsEnrichedHtml(input: Input, index: Int) =
    if (isDisabled)
      super.inputAsEnrichedHtml(input, index)
    else
      super.inputAsEnrichedHtml(input, index) ++ clearButton

  override def inputAsEnrichedElem(input: Input, index: Int): Elem = super.inputAsEnrichedElem(input, index).removeClass("submit-on-change")

  def withClearButton = false

  def clearButton = if (withClearButton) <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span> else NodeSeq.Empty

  def formatPatternForBrowser: String

  def minimumFormattedForBrowser: String

  def maximumFormattedForBrowser: String

  def todayButton = "false"

  def todayHighlight = false
}

trait DateTimeField extends AbstractDateTimeField with DateTimeValues {
  override lazy val formatPatternForBrowser = translator.translate("date-time-format-browser", "")

  lazy val dateTimeFormatterForBrowser = DateTimeFormatter.ofPattern(formatPatternForBrowser)

  def minimumFormattedForBrowser: String = if (minimum == LocalDateTime.MIN) "" else editDateTimeFormat.format(minimum)

  def maximumFormattedForBrowser: String = if (maximum == LocalDateTime.MAX) "" else editDateTimeFormat.format(maximum)
}

trait DateField extends AbstractDateTimeField with DateValues {
  override def datePickerOptions = super.datePickerOptions ++ Map("minView" -> 2, "startView" -> 2)

  override lazy val formatPatternForBrowser = translator.translate("date-format-browser", "")

  lazy val dateFormatterForBrowser = DateTimeFormatter.ofPattern(formatPatternForBrowser)

  def minimumFormattedForBrowser: String = if (minimum == LocalDate.MIN) "" else editDateFormat.format(minimum)

  def maximumFormattedForBrowser: String = if (maximum == LocalDate.MAX) "" else editDateFormat.format(maximum)
}

trait PasswordField extends TextField {
  override def inputAsElem(input: Input) = <input placeholder={placeholder} type="password" autocomplete="off" />

  override def translator: Translator = super.translator.kind("PASSWORD")
}

trait MultiLineField extends TextField {
  override def inputAsElem(input: Input) = <textarea rows={rows.toString} placeholder={placeholder}>{input.string}</textarea>

  def rows = 6
}

trait WebAddress extends TextField with WebAddressValues

trait EmailAddress extends TextField with EmailAddressValues

trait SearchField extends TextField with Emptiable {
  override def inputAsElem(input: Input) = <input type="search" placeholder={placeholder} value={input.string} />

  override def translator: Translator = super.translator.kind("SEARCH")
}

trait SelectField extends FieldWithSuffixes with FieldWithOptions {
  override def translator: Translator = super.translator.kind("SELECT")
}

trait Chosen extends SelectField {
  override def inputCssClasses = "chosen" :: super.inputCssClasses
}

trait SingleSelectField extends SelectField {
  override def inputAsElem(input: Input) =
    <select data-placeholder={t"placeholder: Please select a value"}>{ optionsAsElems(input) }</select>

  private def optionsAsElems(input: Input) =
    if (useEmptyOption(input.string))
      optionAsElem("", "", "").add(required, "disabled", "disabled") :: elems(input)
    else elems(input)

  private def elems(input: Input) = options.map(option => optionAsElem(option.string, option.title, input.string))

  private def optionAsElem(string: String, title: String, inputString: String) =
    <option value={ string }>{ title }</option>.set(inputString == string, "selected")

  override def translator: Translator = super.translator.kind("SINGLE-SELECT")
}

trait MultiSelectField extends SelectField {
  override def inputsAsHtml: NodeSeq = inputs.headOption.map(input => inputAsEnrichedHtml(input, 0)) getOrElse NodeSeq.Empty

  override def inputAsElem(input: Input) =
    <select data-placeholder={t"placeholder: Please select some values"} multiple="multiple">{ optionsAsElems(input) }</select>

  private def optionsAsElems(input: Input) = options.map(option => optionAsElem(option.string, option.title))

  private def optionAsElem(string: String, title: String) = <option value={ string }>{ title }</option>.set(strings.contains(string), "selected")

  override def translator: Translator = super.translator.kind("MULTI-SELECT")
}

trait FloatingInfo extends Field {
  protected override def infoHtml: NodeSeq = super.infoHtml match {
    case NodeSeq.Empty => NodeSeq.Empty
    case x => <span class="pull-right field-info">{x}</span>
  }
}

trait CheckOrRadioField extends FieldWithOptions with FloatingInfo {
  override def inputsAsHtml: NodeSeq = infoHtml ++ options.zipWithIndex.map({
    case (option, index) =>
      if (inlineField) {
      <label class={checkOrRadioType + "-inline"}>{enrichInputElem(optionAsElem(option), index)}{option.title}</label>
    } else {
      <div class={checkOrRadioType}><label>{enrichInputElem(optionAsElem(option), index)}{option.title}</label></div>
    }
  })

  def optionAsElem(option: OptionI) = <input type={checkOrRadioType} value={option.string} />.set(strings.contains(option.string), "checked")

  override def inputCssClasses: List[String] = super.inputCssClasses.filterNot(_ == "form-control")

  def checkOrRadioType: String

  def inlineField = false

  override def inputAsElem(input: Input): Elem = <span></span>
}

trait CheckBoxField extends CheckOrRadioField {
  def checkOrRadioType = "checkbox"

  override def messageHtml: NodeSeq = (super.messageHtml ++ inputs.map(messageHtmlFor)).flatten

  override def minimumNumberOfInputs = if (required) 1 else 0

  override def maximumNumberOfInputs = options.size
}

trait RadioField extends CheckOrRadioField {
  def checkOrRadioType = "radio"
}

trait BooleanCheckBoxField extends Field with BooleanValues with FloatingInfo {
  override def inputsAsHtml: NodeSeq = infoHtml ++ super.inputsAsHtml

  override def formGroupTitle: String = ""

  override def inputAsEnrichedHtml(value: Input, index: Int) =
      <div class="checkbox"><label>{super.inputAsEnrichedHtml(value, index)}{super.formGroupTitle}</label></div>

  override def inputAsElem(input: Input) =
      <input type="checkbox" value="true" />.set(input.string == "true", "checked")

  override def inputCssClasses: List[String] = super.inputCssClasses.filterNot(_ == "form-control")

  override def defaultValues: Seq[ValueType] = false :: Nil
}

trait FileEntryField extends Field with FileEntryValues with Result {
  val deleteButton = new SpecialButton("delete")(parent) with PopoverButtonRenderer with DangerDisplayType with Executable {
    override def execute(parameters: Seq[String]) = processDeleteParameters(parameters)

    override def popoverPlacement: String = "auto right"

    override def popoverContainer: String = parent.form.contentId.toCssId
  }

  override def inputAsElem(input: Input): Elem = <span>{input.title}</span>

  override def inputAsEnrichedHtml(input: Input, index: Int): NodeSeq = {
    val link = input match {
      case ValidInput(_, _, Some(fileEntry)) => fileEntry.path
      case _ => "#"
    }
    <p class="form-control-static clearfix"><div class="pull-right">{deleteButton.buttonAsHtml(input.string)}</div><a href={link} target="_blank">{input.title}</a></p>
  }

  override def minimumNumberOfInputs: Int = 0

  override def maximumNumberOfInputs: Int = Int.MaxValue

  override def itemIsVisible: Boolean = values.length > 0

  private def processDeleteParameters(parameters: Seq[String]): Unit = {
    val ret = parameters.map(stringToValueConverter).collect {
      case SuccessAndTerminate(value) =>
        val message = Message.info(t"deleted-message: File ${value.title} was deleted")
        deleteFileEntry(value)
        message.showNotification
    }
    reset()
    result = AfterFormDisplay(ret)
  }

  def deleteFileEntry(fileEntry: FileEntry): Unit
}

trait UploadWithOverwrite extends BaseItemContainer {
  def defaultFileEntries: Seq[FileEntry]

  def deleteFileEntry(fileEntry: FileEntry): Unit

  def submitOnChange: Boolean = false

  def registerUpload(upload: Upload): Unit

  def exists(upload: Upload): Boolean

  val files = new Field("files") with FileEntryField {
    def deleteFileEntry(fileEntry: FileEntry): Unit = UploadWithOverwrite.this.deleteFileEntry(fileEntry)

    // Ignore parameters from Request
    override def strings_=(strings: Seq[String]): Unit = Unit

    override def defaultValues: Seq[ValueType] = defaultFileEntries
  }

  val uploadsField = new Field("overwriting-uploads") with UploadValues with ReadOnlyField with Executable with Result {
    override def minimumNumberOfInputs: Int = 0

    override def maximumNumberOfInputs: Int = 0

    override def itemIsVisible: Boolean = values.length > 0

    override def execute(parameters: Seq[String]): Unit = {
      val (ex, no) = values.partition(exists)
      values = ex
      if (!no.isEmpty)
        result = AfterFormDisplay(no.map {
          upload =>
            registerUpload(upload)
            Uploads.deregister(upload)
            Message.info(t"moved-message: Upload ${upload.name} moved to files.").showNotification
        })
    }

    override def inputAsEnrichedHtml(input: Input, index: Int): NodeSeq =
      HiddenInputRenderer(name, input.string) ++ <p class="form-control-static clearfix">{actionButtonsHtml(input.string)}<a href="#">{input.title}</a></p>

    private val deleteButton = new SpecialButton("delete") with PopoverButtonRenderer with DangerDisplayType with Executable {
      override def execute(parameters: Seq[String]) = processDeleteParameters(parameters)

      override def popoverPlacement: String = "auto right"

      override def popoverContainer: String = form.contentId.toCssId
    }

    private val overwriteButton = new SpecialButton("overwrite") with PopoverButtonRenderer with InfoDisplayType with Executable {
      override def execute(parameters: Seq[String]) = processOverwriteParameters(parameters)

      override def popoverPlacement: String = "auto right"

      override def popoverContainer: String = form.contentId.toCssId
    }

    private def actionButtonsHtml(string: String): NodeSeq = {
      if (isDisabled) NodeSeq.Empty
      else
        <div class="pull-right btn-group">
          {deleteButton.buttonAsHtml(string)}
          {overwriteButton.buttonAsHtml(string)}
        </div>
    }

    private def processOverwriteParameters(strings: Seq[String]) = {
      result = AfterFormDisplay(JsEmpty +: strings.map(stringToValueConverter).collect {
        case Success(upload) =>
          registerUpload(upload)
          Uploads.deregister(upload)
          values = values.filterNot(_ == upload)
          Message.info(t"overwritten-message: Upload ${upload.name} overwritten.").showNotification
      })
    }

    private def processDeleteParameters(parameters: Seq[String]) = {
      val ret = parameters.map(stringToValueConverter).collect {
        case Success(value) =>
          values = values.filterNot(_ == value)
          Message.info(t"deleted-message: File ${value.name} was deleted").showNotification
      }
      result = AfterFormDisplay(ret)
    }
  }

  new Button("upload") with UploadButton {
    override def uploaded(uploads: List[Upload]): Unit = {
      uploads foreach {
        upload =>
          if (exists(upload)) {
            Uploads.register(upload)
            uploadsField.values = uploadsField.values :+ upload
          }
          else
            registerUpload(upload)
      }
    }

    override def submitOnChange: Boolean = UploadWithOverwrite.this.submitOnChange
  }
}
