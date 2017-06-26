package dao

import javax.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}


class FieldDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                        (implicit executionContext: ExecutionContext) extends BaseDAO[Field] {

  import profile.api._

  private val images = TableQuery[ImageTable]
  private val paragraphs = TableQuery[ParagraphTable]
  private val headings = TableQuery[HeadingTable]

  def insert(entity: Field): Future[Long] = {
    entity match {
      case image: Image => db.run(images returning images.map(_.id) += image)
      case paragraph: Paragraph => db.run(paragraphs returning paragraphs.map(_.id) += paragraph)
      case heading: Heading => db.run(headings returning headings.map(_.id) += heading)
      case _ => throw new Exception("No such type")
    }
  }

  def insert(entities: Seq[Field]): Future[Seq[Long]] = {
    Future.sequence(entities.map(field => insert(field)))
  }

  def findBySectionId(sectionId: Long): Future[Seq[Field]] = {
    val q = images.filter(_.sectionId === sectionId).result
    val q1 = paragraphs.filter(_.sectionId === sectionId).result
    val q2 = headings.filter(_.sectionId === sectionId).result

    // in parallel
    Future.sequence(Seq(db.run(q), db.run(q1), db.run(q2))).map(_.flatten)

    // concurrently
    //    for {
    //      i <- db.run(q)
    //      p <- db.run(q1)
    //      h <- db.run(q2)
    //    } yield i ++ p ++ h
  }

  // only for test purpose
  def all(): Future[Seq[Field]] =
    for {
      i <- db.run(images.result)
      p <- db.run(paragraphs.result)
      h <- db.run(headings.result)
    } yield i ++ p ++ h

  def update(updatedField: Field) = ???

  def delete(id: Long) = ???

  def count(): Future[Int] = for {
    i <- db.run(images.length.result)
    p <- db.run(paragraphs.length.result)
    h <- db.run(headings.length.result)
  } yield i + p + h

  private abstract class FieldTable[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def sectionId = column[Long]("SECTION_ID")
    def ordinal = column[Int]("ORDINAL")
  }

  private class HeadingTable(tag: Tag) extends FieldTable[Heading](tag, "HEADING") {
    def content = column[String]("CONTENT")

    def * = (id.?, sectionId, ordinal, content) <> (Heading.tupled, Heading.unapply)
  }

  private class ParagraphTable(tag: Tag) extends FieldTable[Paragraph](tag, "PARAGRAPH") {
    def content = column[String]("CONTENT")

    def * = (id.?, sectionId, ordinal, content) <> (Paragraph.tupled, Paragraph.unapply)
  }

  private class ImageTable(tag: Tag) extends FieldTable[Image](tag, "IMAGE") {
    def url = column[String]("URL")
    def description = column[String]("DESCRIPTION")

    def * = (id.?, sectionId, ordinal, url, description) <> (Image.tupled, Image.unapply)
  }

}
