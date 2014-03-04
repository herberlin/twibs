package twibs.util

trait DisplayType {
  def displayTypeString: String
}

trait DefaultDisplayType extends DisplayType {
  override def displayTypeString = "default"
}

trait PrimaryDisplayType extends DisplayType {
  override def displayTypeString = "primary"
}

trait SuccessDisplayType extends DisplayType {
  override def displayTypeString = "success"
}

trait InfoDisplayType extends DisplayType {
  override def displayTypeString = "info"
}

trait WarningDisplayType extends DisplayType {
  override def displayTypeString = "warning"
}

trait DangerDisplayType extends DisplayType {
  override def displayTypeString = "danger"
}
