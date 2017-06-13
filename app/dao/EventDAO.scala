package dao

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject

import models.Event
import models.EventCategory.EventCategory
import models.EventCategory
import org.joda.time.DateTime
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

class EventDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
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
  def insert(event: Event): Future[String] = {
    Logger.debug("I'm inserting event")

    db.run(this.events += event)
      .map { _ => "Event is successfully added" }
      .recover {
        case ex: Exception => ex.getCause.getMessage
      }

  }

  // insert couple events in one batch
  def insert(events: Seq[Event]): Future[String] = {
    Logger.debug("I'm inserting events")

    db.run(this.events ++= events)
      .map { _ => "Events are successfully added" }
      .recover {
        case ex: Exception => ex.getCause.getMessage
      }
  }

  // return number of events
  def count(): Future[Int] = {
    db.run (this.events.length.result)
  }

  // EVENT table definition
  private class EventTable(tag: Tag) extends Table[Event](tag, "EVENT") {

    implicit val eventCategoryMapper = MappedColumnType.base[EventCategory.Value, String](
      b => b.toString,
      i => EventCategory.withName(i)
    )
    implicit val dateColumnType = MappedColumnType.base[DateTime, Long](d => d.getMillisOfSecond, d => new DateTime(d))

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def description = column[String]("DESCRIPTION")
    def category = column[EventCategory]("EVENT_CATEGORY")
    def startDateTime = column[DateTime]("START_DATE_TIME")
    def endDateTime = column[DateTime]("END_DATE_TIME")

    def * = (id.?, name, description, category, startDateTime, endDateTime) <> (Event.tupled, Event.unapply)
  }
}
