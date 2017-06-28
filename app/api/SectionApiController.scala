package api

import javax.inject.Inject

import controllers.EventPagesController.FieldDTO
import dao.{FieldDAO, SectionDAO}
import models._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class SectionApiController @Inject()(sectionDAO: SectionDAO, fieldDAO: FieldDAO)(implicit ec: ExecutionContext) extends Controller {
  implicit val fieldDTOWrites = new Writes[FieldDTO] {
    def writes(field: FieldDTO) = Json.obj(
      "fieldtype" -> field.fieldType,
      "content" -> field.content,
      "oridinal" -> field.ordinal,
      "url" -> field.url,
      "description" -> field.description
    )
  }

  def getSectionsByPageId(id: Long) = Action.async {
    implicit request => {
      val sections = sectionDAO.findByPageId(id)
      val images = fieldDAO.getAllImages
      val headings = fieldDAO.getAllHeadings
      val paragraphs = fieldDAO.getAllParagraphs
      for {
        s <- sections
        i <- images
        h <- headings
        p <- paragraphs
      } yield {
        val fieldDTOs = i.map(image => FieldDTO(FieldType.Image, "", image.ordinal, image.url, image.description)) ++
          h.map(heading => FieldDTO(FieldType.Heading, heading.content, heading.ordinal, "", "")) ++
          p.map(paragraph => FieldDTO(FieldType.Paragraph, paragraph.content, paragraph.ordinal, "", ""))
        if (s.isEmpty) {
          NotFound(Json.arr())
        }
        else {
          Ok(s.map {
            s => {
              Json.arr(Json.obj(
                "id" -> s.id,
                "pageid" -> s.pageId,
                "ordinal" -> s.ordinal,
                "title" -> s.title,
                "fields" -> Json.toJson(fieldDTOs)
              ))
            }
          }.reduce((s1, s2) => s1 ++ s2))
        }
      }
    }
  }
}