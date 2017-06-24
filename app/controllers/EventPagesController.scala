package controllers

import javax.inject.{Inject, Singleton}
import dao.EventDAO
import models.Event
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext


@Singleton
class EventPagesController @Inject()(val eventDAO: EventDAO, val messagesApi: MessagesApi, implicit val webJarAssets: WebJarAssets)
                                    (implicit executionContext: ExecutionContext) extends Controller with I18nSupport {

}
