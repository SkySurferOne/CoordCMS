package bootstrap

import java.text.SimpleDateFormat
import javax.inject.Inject

import play.api.Logger
import dao.{EventDAO, FieldDAO, PageDAO, SectionDAO}
import models._

import scala.concurrent.{ExecutionContext, Future}

private[bootstrap] class InitialData @Inject()(eventDAO: EventDAO,
                                               pageDAO: PageDAO,
                                               sectionDAO: SectionDAO,
                                               fieldDAO: FieldDAO)
                                              (implicit executionContext: ExecutionContext) {

  def insert(): Unit = {

    // to add more insertion modify for below
    val added = for {
      c <- eventDAO.count()
      if c <= 0
      _ <- eventDAO.insert(InitialData.events)
      _ <- pageDAO.insert(InitialData.pages)
      _ <- sectionDAO.insert(InitialData.sections)
    } yield fieldDAO.insert(InitialData.fields)

    added.onSuccess {
      case _ => Future.sequence(Seq(pageDAO.all(), sectionDAO.all(), fieldDAO.all()))
        .map {
          res => Logger.info(res.map(_.toString).toString())
        }
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

  def pages = Seq(
    Page(Option(1L), events(0).id.get, 1, "Home"),
    Page(Option(2L), events(0).id.get, 2, "Schedule"),
    Page(Option(3L), events(0).id.get, 3, "Application")
  )

  def sections = Seq(
    Section(Option(1L), pages(0).id.get, 1, "Header"),
    Section(Option(2L), pages(0).id.get, 2, "About"),
    Section(Option(3L), pages(0).id.get, 3, "Organizers"),
    Section(Option(4L), pages(0).id.get, 4, "Partners")
  )

  def images = Seq(
    Image(Option(1L), sections(0).id.get, 1, "http://someurl.com/cats.jpg", "Cats doing cats things"),
    Image(Option(2L), sections(1).id.get, 1, "http://someurl.com/cats2.jpg", "Cats doing cats things"),
    Image(Option(3L), sections(1).id.get, 2, "http://someurl.com/cats3.jpg", "Cats doing cats things")
  )

  def paragraphs = Seq(
    Paragraph(Option(1L), sections(1).id.get, 1, "Lorem ipsum1"),
    Paragraph(Option(2L), sections(1).id.get, 2, "Lorem ipsum2"),
    Paragraph(Option(3L), sections(1).id.get, 3, "Lorem ipsum3")
  )

  def headings = Seq(
    Heading(Option(1L), sections(1).id.get, 1, "We are the best!"),
    Heading(Option(2L), sections(1).id.get, 2, "Best event ever!"),
    Heading(Option(3L), sections(1).id.get, 3, "I like doritos!")
  )

  def fields = images ++ paragraphs ++ headings
}
