package api

import javax.inject.Inject

import dao.SectionDAO
import models.Section
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext


class SectionApiController @Inject()(sectionDAO: SectionDAO)(implicit ec: ExecutionContext) extends Controller {

  implicit val sectionWrites = new Writes[Section] {
    def writes(section: Section) = Json.obj(
      "id" -> section.id,
      "pageid" -> section.pageId,
      "ordinal" -> section.ordinal,
      "title" -> section.title
    )
  }

  def getSectionsByPageId(id: Long) = Action.async {
    implicit request =>
      sectionDAO.findByPageId(id).map {
        section => Ok(Json.toJson(section))
      }
  }
}
