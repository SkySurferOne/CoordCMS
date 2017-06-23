package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext


@Singleton
class EventPagesController @Inject()()(implicit executionContext: ExecutionContext) extends Controller {

}
