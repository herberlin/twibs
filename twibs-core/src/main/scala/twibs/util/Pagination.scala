package twibs.util

object Pagination {

  private object Placeholder extends Page(-1, "..", disabled = true)

  case class Page(firstElementNumber: Long, title: String, active: Boolean = false, disabled: Boolean = false)

}

class Pagination(firstElementNumberArg: Long, displayedElementCountArg: Long, totalElementCountArg: Long, pageSizeArg: Int) {

  import Pagination._

  val pageSize = Math.max(pageSizeArg, 1)
  val totalElementCount = Math.max(0, totalElementCountArg)
  val displayedElementCount = Math.min(Math.max(0, displayedElementCountArg), totalElementCount)
  val currentPageNumber = Math.max(Math.min(firstElementNumberArg, displayedElementCount - 1), 0) / pageSize
  val firstElementNumber = currentPageNumber * pageSize
  val lastElementNumber = Math.min(firstElementNumber + pageSize - 1, displayedElementCount - 1)
  val pageCount = Math.max(1, (displayedElementCount + pageSize - 1) / pageSize)
  val lastPageNumber = pageCount - 1
  val pages: List[Page] = {
    if (pageCount == 0) Nil
    else {
      val seq = if (pageCount <= 5) for (run <- 0 to lastPageNumber.toInt) yield create(run, active = run == currentPageNumber)
      else if (currentPageNumber < 3) (for (run <- 0 to 2) yield create(run, active = run == currentPageNumber)) :+ Placeholder :+ create(lastPageNumber)
      else if (currentPageNumber >= lastPageNumber - 2) create(0) :: Placeholder :: (for (r <- lastPageNumber - 2 to lastPageNumber) yield create(r, active = r == currentPageNumber)).toList
      else create(0) :: Placeholder :: create(currentPageNumber, active = true) :: Placeholder :: create(lastPageNumber) :: Nil

      val prev = Page(Math.max(currentPageNumber - 1, 0) * pageSize, "«", disabled = currentPageNumber == 0)
      val next = Page(Math.min(currentPageNumber + 1, lastPageNumber) * pageSize, "»", disabled = currentPageNumber == lastPageNumber)
      prev :: seq.toList ::: next :: Nil
    }
  }

  private def create(pageNumber: Long, active: Boolean = false, disabled: Boolean = false) =
    Page(pageNumber * pageSize, Formatters.integerFormat.format(pageNumber + 1), active, disabled)
}