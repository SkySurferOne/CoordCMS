package api

import javax.inject.Inject

import dao.{FieldDAO, SectionDAO}
import models.{Field, Heading, Section}
import play.Logger
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class SectionApiController @Inject()(sectionDAO: SectionDAO, fieldDAO: FieldDAO)(implicit ec: ExecutionContext) extends Controller {

  implicit val fieldWrites = new Writes[Heading] {
    def writes(h: Heading) = Json.obj(
      "id" -> h.id,
      "pageid" -> h.sectionId,
      "ordinal" -> h.ordinal,
      "title" -> h.content
    )
  }


  def getSectionsByPageId(id: Long) = Action.async {
    implicit request =>{
      val sections = sectionDAO.findBySectionId(id)
      val fields = fieldDAO.tmpall()
      for{
        s <- sections
        f <- fields
      }yield {
        //Logger.debug(f.toString)
        Ok(s.map{
          s => Json.obj(
            "id" -> s.id,
            "pageid" -> s.pageId,
            "ordinal" -> s.ordinal,
            "title" -> s.title,
            "fields" -> Json.toJson(f.filter(_.sectionId == s.id.get))
          )
        }.reduce((s1, s2) => {Logger.debug(s1.toString);s1.deepMerge(s2)}))
      }
    }
  }
}
