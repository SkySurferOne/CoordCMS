package models

object EventCategory extends Enumeration {
  type EventCategory = Value
  val InternationalEvent = Value("International Event")
  val LocalEvent = Value("Local Event")
  val Workshops = Value("Workshops")

  def options(): Seq[(String, String)] = {
    (for ( v <- EventCategory.values.toSeq)
          yield (v.toString, v.toString))(collection.breakOut)
  }
}

import java.util.Date

import EventCategory._

case class Event(id: Option[Long],
                 name: String,
                 description: String,
                 category: EventCategory,
                 startDateTime: Date,
                 endDateTime: Date)
