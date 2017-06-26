package controllers

import javax.inject.{Inject, Singleton}
import dao.EventDAO
import models.{Event, EventCategory}
import play.api.data.{Form, FormError, Forms}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import play.api.data.Forms.{date, longNumber, mapping, nonEmptyText, optional, text}
import scala.concurrent.ExecutionContext
import play.api.data.format.Formatter


@Singleton
class EventFormController @Inject()(val eventDAO: EventDAO, val messagesApi: MessagesApi, implicit val webJarAssets: WebJarAssets)
                                   (implicit executionContext: ExecutionContext) extends Controller with I18nSupport {

  import EventFormController._

  def createEvent = Action.async {
    implicit request =>
      eventForm.bindFromRequest.fold(
        formWithErrors => {
          eventDAO.all().map(events => BadRequest(views.html.createEventForm(formWithErrors)))
        },
        eventData => {
          val event = Event(Option.empty, eventData.name, eventData.description, eventData.category, eventData.startDateTime, eventData.endDateTime)
          eventDAO.insert(event).map { _ =>
            Redirect(routes.HomeController.index())
          }
        }
      )
  }

  def updateEvent() = Action.async {
    implicit request =>
      eventForm.bindFromRequest.fold(
        formWithErrors => {
          eventDAO.findById(formWithErrors.data("id").toLong).map {
            case Some(event) => BadRequest(views.html.updateEventForm(eventForm.fill(event)))
            case None => NotFound(views.html.notFound("404 - Event not found"))
          }
        },
        eventData => {
          val event = Event(eventData.id, eventData.name, eventData.description, eventData.category, eventData.startDateTime, eventData.endDateTime)
          eventDAO.update(event).map { _ =>
            Redirect(routes.HomeController.index())
          }
        }
      )
  }
}

object EventFormController {
  implicit def eventCategoryFormat: Formatter[EventCategory.EventCategory] =
    new Formatter[EventCategory.EventCategory] {
      def bind(key: String, data: Map[String, String]) = {
        try {
          val name = data.getOrElse(key, "")
          Right(EventCategory.withName(name))
        } catch {
          case e: Exception => Left(List(FormError(key, "Wrong category name")))
        }
      }

      def unbind(key: String, value: EventCategory.EventCategory) =
        Map(key -> value.toString)
    }

  val eventForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText,
      "description" -> text,
      "category" -> Forms.of[EventCategory.EventCategory],
      "startDateTime" -> date("yyyy-MM-dd'T'HH:mm"),
      "endDateTime" -> date("yyyy-MM-dd'T'HH:mm")
    )(Event.apply)(Event.unapply)
  )
}