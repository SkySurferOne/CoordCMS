package models

object EventCategory extends Enumeration {
  type EventCategory = Value
  val InternationalEvent = Value("International Event")
  val LocalEvent = Value("Local Event")
  val Workshops = Value("Workshops")
}
import java.util.Date

import EventCategory._

case class Event(id: Option[Long],
                 name: String,
                 description: String,
                 category: EventCategory,
                 startDateTime: Date,
                 endDateTime: Date)
