package bootstrap

import java.text.SimpleDateFormat
import javax.inject.Inject
import dao.EventDAO
import models.{Event, EventCategory}
import scala.concurrent.{ExecutionContext}

private[bootstrap] class InitialData @Inject()(eventDAO: EventDAO)
                                              (implicit executionContext: ExecutionContext) {

  def insert(): Unit = {
    eventDAO.count().onSuccess {
      case count if count <= 0 => for (e <- InitialData.events) eventDAO.insert(e)
    }
  }

  insert()
}

private[bootstrap] object InitialData {
  private val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm")

  def events = Seq(
    Event(Option(1L), "SSA4", "Nice softskills wokshop", EventCategory.Workshops, sdf.parse("2006-01-10T10:00"), sdf.parse("2006-01-12T10:00")),
    Event(Option(2L), "Web Platform", "For HP lovers", EventCategory.Workshops, sdf.parse("2006-01-10T10:00"), sdf.parse("2006-01-12T10:00")),
    Event(Option(3L), "TPA", "Nice softskills wokshop", EventCategory.Workshops, sdf.parse("2006-01-10T10:00"), sdf.parse("2006-01-12T10:00")),
    Event(Option(4L), "Hacknarok", "Best hackathon in the world!", EventCategory.Workshops, sdf.parse("2006-01-10T10:00"), sdf.parse("2006-01-12T10:00")),
    Event(Option(5L), "Android Master", "Android workshops", EventCategory.Workshops, sdf.parse("2006-01-10T10:00"), sdf.parse("2006-01-12T10:00"))
  )
}
