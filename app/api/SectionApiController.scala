package api

import javax.inject.Inject

import dao.{FieldDAO, SectionDAO}
import models._
import play.Logger
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class SectionApiController @Inject()(sectionDAO: SectionDAO, fieldDAO: FieldDAO)(implicit ec: ExecutionContext) extends Controller {
   def getSectionsByPageId(id: Long) = Action.async {
    implicit request =>{
      val sections = sectionDAO.findByPageId(id)
      val images = fieldDAO.getAllImages
      val headings = fieldDAO.getAllHeadings
      val paragraphs = fieldDAO.getAllParagraphs
      for{
        s <- sections
        i <- images
        h <- headings
        p <- paragraphs
      }yield {
        if(s.isEmpty){
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
                getJsonWithFields(i, p, h, s.id.get)
              ))
            }
          }.reduce((s1, s2) => s1 ++ s2))
        }
      }
    }
  }

  def getJsonWithFields(images:Seq[Image], paragraphs:Seq[Paragraph], headings:Seq[Heading], id: Long): (String, Json.JsValueWrapper)={
    val t1 = images.filter(_.sectionId == id).map(image => Json.arr(Json.obj(
      "id" -> image.id,
      "pageid" -> image.sectionId,
      "ordinal" -> image.ordinal,
      "url" -> image.url,
      "description:" -> image.description)
    ))
    val t3 = headings.filter(_.sectionId == id).map(heading => Json.arr(Json.obj(
      "id" -> heading.id,
      "pageid" -> heading.sectionId,
      "ordinal" -> heading.ordinal,
      "title" -> heading.content)
    ))
    val t2 = paragraphs.filter(_.sectionId == id).map(paragraph => Json.arr(Json.obj(
      "id" -> paragraph.id,
      "pageid" -> paragraph.sectionId,
      "ordinal" -> paragraph.ordinal,
      "title" -> paragraph.content)
    ))
    val t4 = t1 ++ t2 ++ t3
    if(t4.nonEmpty) "fields" -> t4.reduce((x,y) => x ++ y)
    else "fields" ->  Json.arr()
  }
}
