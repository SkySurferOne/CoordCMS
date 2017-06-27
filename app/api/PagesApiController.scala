package api

import javax.inject.Inject
import dao.PageDAO
import models.Page
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext


class PagesApiController @Inject()(pageDAO: PageDAO)(implicit ec: ExecutionContext) extends Controller {

  implicit val pageWrites = new Writes[Page] {
    def writes(page: Page) = Json.obj(
      "id" -> page.id,
      "eventid" -> page.eventId,
      "ordinal" -> page.ordinal,
      "title" -> page.title
    )
  }

  def getPagesByEventId(id: Long) = Action.async {
    implicit request =>
      pageDAO.findByEventId(id).map {
        pages => Ok(Json.toJson(pages))
      }
  }

  def getPageById(id: Long) = Action.async{
    implicit request =>
      pageDAO.findByPageId(id).map{
        page => Ok(Json.toJson(page))
      }
  }
}
