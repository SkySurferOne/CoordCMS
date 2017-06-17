package controllers

import javax.inject._

import dao.EventDAO
import models.{Event, EventCategory}
import play.api.mvc._
import play.api.data.{Form, FormError, Forms}
import play.api.data.Forms.{date, longNumber, mapping, nonEmptyText, optional, text}
import play.api.data.format.Formatter
import play.api.Logger
import scala.concurrent.{ExecutionContext, Future}
import play.api.i18n._

@Singleton
class HomeController @Inject()(eventDAO: EventDAO, val messagesApi: MessagesApi, implicit val webJarAssets: WebJarAssets)
                              (implicit executionContext: ExecutionContext) extends Controller with I18nSupport {

  def index = Action {
    Redirect(routes.HomeController.listEvents)
  }

  def listEvents = Action.async {
    implicit request =>
      eventDAO.all().map(events => Ok(views.html.index(events)))
  }

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

  // TODO make separate page for this
  def createEvent = Action.async {
    implicit request =>
      eventForm.bindFromRequest.fold(
        formWithErrors => {
          Logger.debug("eerror kurna!")
          Logger.debug(formWithErrors.toString)
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

  def getEventById(id: Long) = Action.async {
    implicit request =>
      eventDAO.findById(id).map {
        case Some(event) => Ok(views.html.showEventDetail(event))
        case None => NotFound(views.html.notFound("404 - Event not found"))
      }
  }

  def deleteEventById(id: Long) = Action.async {
    implicit request =>
      eventDAO.delete(id).map {
        _ => Redirect(routes.HomeController.index())
      }
  }

  def showEventForm = Action {
    implicit request =>
      Ok(views.html.createEventForm(eventForm))
  }

}
