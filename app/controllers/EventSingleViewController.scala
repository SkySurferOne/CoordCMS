package controllers

import javax.inject.{Inject, Singleton}

import dao.EventDAO
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext


@Singleton
class EventSingleViewController @Inject()(val eventDAO: EventDAO)(implicit executionContext: ExecutionContext) extends Controller {

  def deleteEventById(id: Long) = Action.async {
    implicit request =>
      eventDAO.delete(id).map {
        _ => Redirect(routes.HomeController.index())
      }
  }

}
