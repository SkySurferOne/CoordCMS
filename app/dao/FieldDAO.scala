package dao

import javax.inject.Inject

import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

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

//    entities match {
//      case imgs: Seq[Image] => db.run(images ++= imgs)
//        .map { _ => "Collection successfully added" }
//        .recover {
//          case ex: Exception => ex.getCause.getMessage
//        }
//      case para: Seq[Paragraph] => db.run(paragraphs ++= para)
//        .map { _ => "Collection successfully added" }
//        .recover {
//          case ex: Exception => ex.getCause.getMessage
//        }
//      case head: Seq[Heading] => db.run(headings ++= head)
//        .map { _ => "Collection successfully added" }
//        .recover {
//          case ex: Exception => ex.getCause.getMessage
//        }
//      case _ => throw new Exception("No such type")
//    }
  }

  def findBySectionId(sectionId: Long) = ???

  def update(id: Long) = ???

  def delete(id: Long) = ???

  def count(): Future[Int] = for {
    i <- db.run(images.length.result)
    p <- db.run(paragraphs.length.result)
    h <- db.run(headings.length.result)
  } yield i + p + h

  // only for test purpose
  def all(): Future[Seq[Field]] =
    for {
      i <- db.run(images.result)
      p <- db.run(paragraphs.result)
      h <- db.run(headings.result)
    } yield i ++ p ++ h

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
