package dao

import javax.inject.Inject

import models.{Event, Page}
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}


class PageDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider, val pageDAO: SectionDAO, val fieldDAO: FieldDAO)
                       (implicit executionContext: ExecutionContext) extends BaseDAO[Page] {

  import profile.api._

  private val pages = TableQuery[PageTable]

  def insert(entity: Page): Future[Long] = {
    db.run(pages returning pages.map(_.id) += entity)
  }

  def insert(entities: Seq[Page]): Future[String] = {
    db.run(pages ++= entities)
      .map { _ => "Collection successfully added" }
      .recover {
        case ex: Exception => ex.getCause.getMessage
      }
  }

  def findByEventId(eventId: Long): Future[Seq[Page]] = {
    val q = pages.filter(_.eventId === eventId).result
    db.run(q)
  }

  def findByEventIdAndOrdering(eventId: Long, ordering: Int): Future[Option[Page]] = {
    val q = pages.filter(e => e.eventId === eventId && e.ordinal === ordering).result.headOption
    db.run(q)
  }

  def update(updatedPage: Page) = ???

  // It must delete all sections before
  def delete(id: Long): Future[Int] = {
    val q = pages.filter(_.id === id).delete
    db.run(q)
  }

  def count(): Future[Int] =
    db.run(pages.length.result)

  // only for test purpose
  def all(): Future[Seq[Page]] =
    db.run(pages.result)

  private class PageTable(tag: Tag) extends Table[Page](tag, "PAGE") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def eventId = column[Long]("EVENT_ID")
    def ordinal = column[Int]("ORDINAL")
    def title = column[String]("TITLE")

    def * = (id.?, eventId, ordinal, title) <> (Page.tupled, Page.unapply)
  }
}
