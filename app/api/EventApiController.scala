package api

import java.util.{Date}
import javax.inject.Inject
import controllers.Assets.Asset.string2Asset
import dao.EventDAO
import models.{Event, EventCategory}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class EventApiController @Inject()(eventDAO: EventDAO)(implicit ec: ExecutionContext) extends Controller {

  implicit val eventWrites = new Writes[Event] {
    def writes(event: Event) = Json.obj(
      "id" -> event.id,
      "name" -> event.name,
      "description" -> event.description,
      "category" -> event.category.toString,
      "startDateTime" -> event.startDateTime.toString,
      "endDateTime" -> event.endDateTime.toString
    )
  }

  implicit val eventCategoryReads = Reads.enumNameReads(EventCategory)
  val pattern = "yyyy-MM-dd HH:mm"
  implicit val dateFormat = Format[Date](Reads.dateReads(pattern), Writes.dateWrites(pattern))

  implicit val eventsReads: Reads[Event] = (
      (JsPath \ "id").readNullable[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "category").read[EventCategory.EventCategory] and
      (JsPath \ "startDateTime").read[Date] and
      (JsPath \ "endDateTime").read[Date]
    ) (Event.apply _)


  def index = Action.async {
    implicit request =>
      eventDAO.all().map {
        events => Ok(Json.toJson(events))
      }
  }

  def getEventById(id: Long) = Action.async{
    implicit request =>{
      eventDAO.findById(id).map{
        case Some(event) => Ok(Json.toJson(event))
        case None => NotFound(Json.obj())
      }
    }
  }

  def createEvent = Action.async(parse.json) { implicit request =>
    val eventResult = request.body.validate[Event]
    eventResult.fold(
    errors => {
        Future.successful(BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors))))
      },
    event => {
        eventDAO.insert(event) map {
          e =>  Ok(Json.obj("status" -> "OK", "message" -> ("Event '" + event.name + "' saved with id "+ e +".")))
        } recover {
          case e => BadRequest(Json.obj("message" -> e.getMessage))
        }
      }
    )
  }

}
