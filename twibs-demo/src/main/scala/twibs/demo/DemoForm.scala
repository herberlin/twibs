package twibs.demo

import twibs.form.base.ComponentState.ComponentState
import twibs.form.base._
import twibs.form.bootstrap3._
import twibs.util.{Message, DefaultDisplayType}

class DemoForm extends Form("demo") {
  override def accessAllowed: Boolean = true

  new DisplayHtml(<div class="alert alert-warning"><p>Warning Message.</p></div>)

  new DisplayHtml(false, <div class="alter alert-error"><p>This is never displayed.</p></div>)

  trait TwoValues extends StringValues {
    override def defaultValues: Seq[ValueType] = "a" :: "b" :: Nil
  }

  trait BaseField extends Field with StringValues {
    override def minimumLength: Int = 2

    override def defaultValues: Seq[ValueType] = "a" :: Nil
  }

  trait BaseFieldDisabled extends BaseField {
    override def state: ComponentState = super.state.disabled
  }

  trait BaseFieldHidden extends BaseField {
    override def state: ComponentState = super.state.hidden
  }

  trait BaseFieldIgnored extends BaseField {
    override def state: ComponentState = super.state.ignored
  }

  trait SingleLineContainer extends StaticContainer {
    new Field("f1") with BaseField with SingleLineField

    new Field("f2") with BaseFieldDisabled with SingleLineField

    new Field("f3") with BaseFieldHidden with SingleLineField

    new Field("f4") with BaseFieldIgnored with SingleLineField
  }

  new StaticContainer("simple") with SingleLineContainer

  new StaticContainer("with-info") with Panel with SingleLineContainer

  new StaticContainer("two-values") with Panel {
    new Field("f1") with BaseField with SingleLineField with TwoValues

    new Field("f2") with BaseFieldDisabled with SingleLineField with TwoValues

    new Field("f3") with BaseFieldHidden with SingleLineField with TwoValues

    new Field("f4") with BaseFieldIgnored with SingleLineField with TwoValues
  }

  new Button("ok") with StringValues with DefaultDisplayType {
    override def executeValidated(): Unit = result = AfterFormDisplay(Message.success("Validated").showNotification)
  }

  //  case class Translation(key: String, locale: ULocale, text: String)
  //
  //  //  val emails = new ItemContainer("emails") with DbTable[PositionedResult] with H2Table {
  //  //    val columns =
  //  //      StringColumn("subject", "Betreff", _.nextString()) ::
  //  //        StringColumn("body", "Nachricht", _.nextString()) ::
  //  //        StringColumn("from_email_address", "From", _.nextString()) ::
  //  //        StringColumn("tos", "To", _.nextString()) ::
  //  //        StringColumn("ccs", "CC", _.nextString()) ::
  //  //        StringColumn("bccs", "BCC", _.nextString()) :: Nil
  //  //
  //  //    def tableBody: NodeSeq =
  //  //      query.as(GetResult(r => <tr>{visibleColumns.map(column => <td>{column.value(r)}</td>)}</tr>)).list()
  //  //
  //  //    def fromSql: String = "email_table_view"
  //  //  }
  //  //
  //
  //  val userid = new HiddenField("userid") with StringValues
  //
  //
  //  val simpleButton = new Button("simple") with StringValues with SuccessDisplayType with EnabledForm {
  //    override def execute(): Unit = result = InsteadOfFormDisplay(Message.success("Pressed").showNotification)
  //  }
  //
  //  new DisplayHtml(<div class="row"><div class="col-sm-offset-3 col-sm-9"><p>Next is an input with minimal ckeditor configuration.</p></div></div>)
  //
  //  val ckEditor = new Field("ckeditor") with StringValues with MultiLineField with JavascriptComponent {
  //    override def javascript: JsCmd =
  //      jQuery(id).call("ckeditor", Map(
  //        "skin" -> "bootstrapck",
  //        "smiley_path" -> "/smileys/",
  //        "smiley_images" -> Array("smiley.png", "smiley-angel.png", "smiley-confuse.png", "smiley-cool.png", "smiley-cry.png", "smiley-draw.png", "smiley-eek.png", "smiley-eek-blue.png", "smiley-evil.png", "smiley-fat.png", "smiley-grin.png", "smiley-kiss.png", "smiley-kitty.png", "smiley-lol.png", "smiley-mad.png", "smiley-medium.png", "smiley-money.png", "smiley-mr-green.png", "smiley-nerd.png", "smiley-neutral.png", "smiley-razz.png", "smiley-red.png", "smiley-roll.png", "smiley-roll-blue.png", "smiley-roll-sweat.png", "smiley-sad.png", "smiley-sad-blue.png", "smiley-sleep.png", "smiley-slim.png", "smiley-surprise.png", "smiley-sweat.png", "smiley-twist.png", "smiley-wink.png", "smiley-yell.png", "smiley-zipper.png"),
  //        "smiley_descriptions" -> Array("smiley", "angel", "confuse", "cool", "cry", "draw", "eek", "eek-blue", "evil", "fat", "grin", "kiss", "kitty", "lol", "mad", "medium", "money", "mr-green", "nerd", "neutral", "razz", "red", "roll", "roll-blue", "roll-sweat", "sad", "sad-blue", "sleep", "slim", "surprise", "sweat", "twist", "wink", "yell", "zipper"),
  //        "resize_enabled" -> false,
  //        "removePlugins" -> "elementspath",
  //        "toolbar" -> Array(Array("Bold", "Italic", "-", "Smiley"))))
  //  }
  //
  //  val screenname = new Field("screenname") with StringValues with SingleLineField with DisabledField with Untrimmed {
  //    override def regex = "[a-z]+"
  //  }
  //
  //  val reload = new Field("reload") with BooleanCheckBoxField
  //
  //  val salutation = new Field("salutation") with StringValues with SingleSelectField with Required with Chosen {
  //    override def computeOptions = toOptions("Mr" :: "Mrs" :: Nil)
  //  }
  //
  //  val user = new StaticContainer("user") {
  //    val username = new Field("username") with StringValues with SingleLineField with Required
  //
  //    val password = new Field("password") with StringValues with PasswordField
  //  }
  //
  //  val comment = new Field("comment") with StringValues with MultiLineField {
  //    override def minimumLength: Int = 2
  //
  //    override def maximumLength: Int = 4
  //
  //    override def defaultValues = "Text\nwith break" :: Nil
  //  }
  //
  //  val age = new Field("age") with LongValues with SingleLineField {
  //    override def minimum = -1L
  //
  //    override def maximum = 12L
  //
  //    override def defaultValues = 18L :: Nil
  //  }
  //
  //  val price = new Field("price") with DoubleValues with SingleLineField {
  //    override def minimum = -1.5D
  //
  //    override def maximum = 12.2D
  //
  //    override def suffix = Text("€")
  //  }
  //
  //  val next = new Field("next") with DateTimeField {
  //    override def minimum = LocalDateTime.of(2013, 6, 12, 13, 14)
  //
  //    override def maximum = LocalDateTime.of(2013, 6, 13, 14, 15)
  //  }
  //
  //  val birthday = new Field("birthday") with DateField {
  //    override def minimum = LocalDate.of(2013, 6, 12)
  //
  //    override def maximum = LocalDate.of(2013, 6, 13)
  //  }
  //
  //  val when = new Field("when") with LongValues with CheckBoxField with Required {
  //    override def computeOptions = toOptions(1L :: 2L :: -1L :: Nil)
  //
  //    override def computeTitleForValue(value: ValueType): String = value match {
  //      case 1L => "One"
  //      case 2L => "Two"
  //      case -1L => "more"
  //      case _ => super.computeTitleForValue(value)
  //    }
  //  }
  //
  //  val weekday = new Field("weekday") with DoubleValues with SingleSelectField with Required {
  //    override def computeOptions = toOptions(1.0D :: 2.0D :: 3.0D :: Nil)
  //
  //    override def computeTitleForValue(value: ValueType): String = value match {
  //      case 1.0D => "Monday"
  //      case 2.0D => "Tuesday"
  //      case 3.0D => "Wednesday"
  //      case _ => super.computeTitleForValue(value)
  //    }
  //
  //    override def defaultValues = -1D :: optionValues(1) :: Nil
  //  }
  //
  //  val uploadButton = new Button("upload") with UploadButton {
  //    override def uploaded(uploads: List[Upload]): Unit = uploads.foreach(addUpload)
  //  }
  //
  //  val uploadContainer = new DynamicContainer[UploadWithComment]("portrait") {
  //    def create(dynamicId: String) = new Dynamic("upload", dynamicId) with UploadWithComment {
  //      override def html: NodeSeq = <div>{super.html}</div>
  //    }
  //
  //    override def minimumNumberOfDynamics: Int = 3
  //  }
  //
  //  val u1 = new Upload() {
  //    override val id = "idfile1"
  //
  //    def name: String = "file_one.jpg"
  //
  //    def size: Long = 123451
  //
  //    def stream = new ByteArrayInputStream(Array[Byte](0))
  //  }
  //
  //  val u2 = new Upload() {
  //    override val id = "idfile2"
  //
  //    def name: String = "file_two.jpg"
  //
  //    def size: Long = 874643
  //
  //    def stream = new ByteArrayInputStream(Array[Byte](0))
  //  }
  //
  //  val uploadDynamic1 = addUpload(u1)
  //
  //  val uploadDynamic2 = addUpload(u2)
  //
  //  addUpload(u2)
  //
  //  def addUpload(upload: Upload): UploadWithComment = {
  //    Uploads.register(upload)
  //    val ret = uploadContainer.recreate(upload.id)
  //    ret.upload.values = upload :: Nil
  //    ret
  //  }
  //
  //  val messages = new Messages()
  //
  //  messages append Message.info("Please supply information")
  //
  //  val uploadButtonAutoSubmit = new Button("uploadsubmit2") with UploadButton {
  //    override def uploaded(uploads: List[Upload]): Unit = uploads.foreach(addUpload)
  //
  //    override def submitOnChange: Boolean = true
  //  }
  //
  //  val buttons = new ButtonFormGroup {
  //    val login = new Button("login") with StringValues with PrimaryDisplayType with DefaultExecutable {
  //      override def executeValidated() = {
  //        messages append Message.success("Login successful")
  //      }
  //
  //      override def html: NodeSeq = buttonAsHtml
  //    }
  //  }
  //
  //  val login2 = new Button("login2") with StringValues with PrimaryDisplayType {
  //    override def executeValidated() = {
  //      messages append Message.success("Login2 successful")
  //    }
  //  }
  //
  //  //  val table = new ItemContainer("table") with ElementDbTable[Translation] with H2Table {
  //  //    val columns = StringColumn("key", "Schlüssel", _.key) ::
  //  //      StringColumn("locale", "Locale", _.locale.toString) ::
  //  //      StringColumn("text", "Text", _.text) :: Nil
  //  //
  //  //    def fromSql: String = "translation"
  //  //
  //  //    def toResult = GetResult(r => Translation(r.<<, new ULocale(r.<<), r.<<))
  //  //  }

  //  new DisplayHtml(!valid, validationResults.map(v => <div>{v}</div>))
}
