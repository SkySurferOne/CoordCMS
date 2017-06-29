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
    Event(Option(5L), "Android Master", "Android workshops", EventCategory.Workshops, sdf.parse("2006-01-10T10:00"), sdf.parse("2006-01-12T10:00")),
    Event(Option(6L), "Web Development for Cats", "With this course your cat would't be just useless bastard " +
      "who only dumps things from a table, licks his fur and split furballs. " +
      "With this workshop your cat gain very particullar set of skills about web development." +
      "Skills we have acquired over a very long career. Skills that make me a savor for people like you." +
      "If you enroll your cat for this course you not only make him useful you will make him understand what is HTTP protocol, how to write " +
      "a websites in HTML5 and CSS and how to deploy it on the server. Don't hesitate and enroll you cat now!",
      EventCategory.Workshops, sdf.parse("2017-07-03T09:00"), sdf.parse("2017-07-06T16:00"))
  )

  def pages = Seq(
    Page(Option(1L), events(0).id.get, 1, "Home"),
    Page(Option(2L), events(0).id.get, 2, "Schedule"),
    Page(Option(3L), events(0).id.get, 3, "Application"),
    Page(Option(4L), events(5).id.get, 1, "Home")
  )

  def sections = Seq(
    Section(Option(1L), pages(0).id.get, 1, "Header"),
    Section(Option(2L), pages(0).id.get, 2, "About"),
    Section(Option(3L), pages(0).id.get, 3, "Organizers"),
    Section(Option(4L), pages(0).id.get, 4, "Partners"),
    Section(Option(5L), pages(3).id.get, 1, "Header"),
    Section(Option(6L), pages(3).id.get, 2, "About a workshop"),
    Section(Option(7L), pages(3).id.get, 3, "Price"),
    Section(Option(8L), pages(3).id.get, 4, "Contact")
  )

  def images = Seq(
    Image(Option(1L), sections(0).id.get, 1, "http://someurl.com/cats.jpg", "Cats doing cats things"),
    Image(Option(2L), sections(1).id.get, 1, "http://someurl.com/cats2.jpg", "Cats doing cats things"),
    Image(Option(3L), sections(1).id.get, 2, "http://someurl.com/cats3.jpg", "Cats doing cats things"),
    Image(Option(3L), sections(4).id.get, 3, "http://lorempixel.com/1140/300/cats/", "Cat"),
    Image(Option(3L), sections(5).id.get, 3, "http://lorempixel.com/300/300/cats/", "Cat"),
    Image(Option(3L), sections(5).id.get, 4, "http://lorempixel.com/300/300/cats/", "Cat"),
    Image(Option(3L), sections(6).id.get, 3, "http://lorempixel.com/300/300/cats/", "Cat"),
    Image(Option(3L), sections(7).id.get, 3, "http://lorempixel.com/300/300/cats/", "Cat")
  )

  def paragraphs = Seq(
    Paragraph(Option(1L), sections(1).id.get, 1, "Lorem ipsum1"),
    Paragraph(Option(2L), sections(1).id.get, 2, "Lorem ipsum2"),
    Paragraph(Option(3L), sections(1).id.get, 3, "Lorem ipsum3"),
    Paragraph(Option(4L), sections(4).id.get, 2, "Teaching cats with passion"),
    Paragraph(Option(5L), sections(5).id.get, 2, "With this course your cat would't be just useless bastard " +
      "who only dumps things from a table, licks his fur and split furballs. " +
      "With this workshop your cat gain very particullar set of skills about web development." +
      "Skills we have acquired over a very long career. Skills that make me a savor for people like you." +
      "If you enroll your cat for this course you not only make him useful you will make him understand what is HTTP protocol, how to write " +
      "a websites in HTML5 and CSS and how to deploy it on the server. Don't hesitate and enroll you cat now!"),
    Paragraph(Option(6L), sections(6).id.get, 2, "Only $20 per day!"),
    Paragraph(Option(7L), sections(7).id.get, 2, "You have to call: 911-888-777-MEOW")
  )

  def headings = Seq(
    Heading(Option(1L), sections(1).id.get, 1, "We are the best!"),
    Heading(Option(2L), sections(1).id.get, 2, "Best event ever!"),
    Heading(Option(3L), sections(1).id.get, 3, "I like doritos!"),
    Heading(Option(4L), sections(4).id.get, 1, "Web Development for cats"),
    Heading(Option(5L), sections(5).id.get, 1, "About a workshop"),
    Heading(Option(6L), sections(6).id.get, 1, "Price"),
    Heading(Option(7L), sections(7).id.get, 1, "Contact")
  )

  def fields = images ++ paragraphs ++ headings
}
