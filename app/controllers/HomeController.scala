package controllers

import javax.inject._

import dao.EventDAO
import models.{Event, EventCategory}
import org.joda.time.DateTime
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(eventDAO: EventDAO)(implicit executionContext: ExecutionContext) extends Controller {

  def index = Action.async {
    eventDAO.all().map(events => Ok(views.html.index(events)))
  }

  def testAdd = Action.async {
    val event = Event(Option(5L), "Android Master", "Android workshops", EventCategory.Workshops, DateTime.now(), DateTime.now.plusDays(5))
    eventDAO.insert(event).map { _ =>
      Redirect(routes.HomeController.index)
    }
  }
}
