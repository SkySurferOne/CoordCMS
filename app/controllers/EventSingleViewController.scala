package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext


@Singleton
class EventSingleViewController @Inject()()(implicit executionContext: ExecutionContext) extends Controller {

}
