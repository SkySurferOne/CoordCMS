package controllers

import javax.inject._
import dao.EventDAO
import play.api.mvc._
import scala.concurrent.{ExecutionContext}
import play.api.i18n._


@Singleton
class HomeController @Inject()(val eventDAO: EventDAO, val messagesApi: MessagesApi, implicit val webJarAssets: WebJarAssets)
                              (implicit executionContext: ExecutionContext) extends Controller with I18nSupport {

  import EventFormController._

  def index = Action {
    Redirect(routes.HomeController.listEvents)
  }

  def listEvents = Action.async {
    implicit request =>
      eventDAO.all().map(events => Ok(views.html.index(events)))
  }

  def getEventById(id: Long) = Action.async {
    implicit request =>
      eventDAO.findById(id).map {
        case Some(event) => Ok(views.html.showEventDetail(event))
        case None => NotFound(views.html.notFound("404 - Event not found"))
      }
  }

  def showNewEventForm() = Action {
    implicit request => Ok(views.html.createEventForm(eventForm))
  }

  def showUpdateEventForm(id: Long) = Action.async {
    implicit request =>
      eventDAO.findById(id).map {
        case Some(event) => Ok(views.html.updateEventForm(eventForm.fill(event)))
        case None => NotFound(views.html.notFound("404 - Event not found"))
      }
  }
}
