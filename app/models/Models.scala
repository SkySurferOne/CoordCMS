package models

abstract class BasicEnum extends Enumeration {
  def options(): Seq[(String, String)] = {
    (for (v <- this.values.toSeq)
      yield (v.toString, v.toString)) (collection.breakOut)
  }
}

object EventCategory extends BasicEnum {
  type EventCategory = Value
  val InternationalEvent = Value("International Event")
  val LocalEvent = Value("Local Event")
  val Workshops = Value("Workshops")
}

object FieldType extends BasicEnum {
  type FieldType = Value
  val Heading = Value("Heading")
  val Paragraph = Value("Paragraph")
  val Image = Value("Image")
}

import java.util.Date

import EventCategory._

sealed trait Entity

case class Event(id: Option[Long],
                 name: String,
                 description: String,
                 category: EventCategory,
                 startDateTime: Date,
                 endDateTime: Date) extends Entity

case class Page(id: Option[Long],
                eventId: Long,
                ordinal: Int,
                title: String) extends Entity

case class Section(id: Option[Long],
                   pageId: Long,
                   ordinal: Int,
                   title: String) extends Entity


sealed trait Field extends Entity

case class Heading(id: Option[Long],
                   sectionId: Long,
                   ordinal: Int,
                   content: String) extends Field

case class Paragraph(id: Option[Long],
                     sectionId: Long,
                     ordinal: Int,
                     content: String) extends Field

case class Image(id: Option[Long],
                 sectionId: Long,
                 ordinal: Int,
                 url: String,
                 description: String) extends Field