package dao

import java.util.Date

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject

import models.Event
import models.EventCategory
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

class EventDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                        (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val events = TableQuery[EventTable]

  // find event by id
  def findById(id: Long): Future[Option[Event]] =
    db.run(events.filter(_.id === id).result.headOption)

  // delete event
  def delete(id: Long): Future[Int] = {
    db.run(events.filter(_.id === id).delete)
  }

  // get all events
  def all(): Future[Seq[Event]] =
    db.run(events.result)

  // insert one event
  def insert(event: Event): Future[Long] = {
    db.run(this.events returning events.map(_.id) += event)
  }

  // insert couple events in one batch
  def insert(events: Seq[Event]): Future[String] = {
    db.run(this.events ++= events)
      .map { _ => "Events are successfully added" }
      .recover {
        case ex: Exception => ex.getCause.getMessage
      }
  }

  // return number of events
  def count(): Future[Int] = {
    db.run(this.events.length.result)
  }

  // EVENT table definition
  // TODO add creationDate and filter events by it
  private class EventTable(tag: Tag) extends Table[Event](tag, "EVENT") {
    // mappers
    implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))
    implicit val eventCategoryMapper = MappedColumnType.base[EventCategory.Value, String](
      b => b.toString,
      i => EventCategory.withName(i)
    )

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def description = column[String]("DESCRIPTION")

    def category = column[EventCategory.Value]("EVENT_CATEGORY")

    def startDateTime = column[Date]("START_DATE_TIME")

    def endDateTime = column[Date]("END_DATE_TIME")

    def * = (id.?, name, description, category, startDateTime, endDateTime) <> (Event.tupled, Event.unapply)
  }

}
