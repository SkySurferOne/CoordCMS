package controllers

import javax.inject.{Inject, Singleton}

import dao.EventDAO
import models.{Event, EventCategory, FieldType}
import models.FieldType.FieldType
import play.Logger
import play.api.data.{Form, FormError, Forms}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import play.api.data.Forms.{mapping, _}
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class EventPagesController @Inject()(val eventDAO: EventDAO, val messagesApi: MessagesApi, implicit val webJarAssets: WebJarAssets)
                                    (implicit executionContext: ExecutionContext) extends Controller with I18nSupport {
  import EventPagesController._

  def showEventPagesForm(eventId: Long) = Action.async {
    implicit request =>
      eventDAO.findById(eventId).map {
        case Some(event) => Ok(views.html.eventPagesForm(eventId, event.name, pagesForm))
        case None => NotFound(views.html.notFound("404 - Event not found"))
      }
  }

  def createEventPages(eventId: Long) = Action.async {
    implicit request =>
      pagesForm.bindFromRequest.fold(
        formWithErrors => {
          eventDAO.findById(eventId).map {
              case Some(event) => {
                Logger.debug(formWithErrors.toString)
                BadRequest(views.html.eventPagesForm(eventId, event.name, formWithErrors))
              }
              case None => NotFound(views.html.notFound("404 - Event not found"))
          }
        },
        pagesData => {
          // create pages, sections ect
          Logger.debug(pagesData.toString)
          Future.successful(Redirect(routes.HomeController.index()))
        }
      )
  }

}

object EventPagesController {

  case class PagesDTO(pages: List[PageDTO])
  case class PageDTO(ordinal: Int, title: String, sections: List[SectionDTO])
  case class SectionDTO(ordinal: Int, title: String, fields: List[FieldDTO])
  case class FieldDTO(fieldType: FieldType, content: String, ordinal: Int, url: String, description: String)

  implicit def fieldTypeFormat: Formatter[FieldType.FieldType] =
    new Formatter[FieldType.FieldType] {
      def bind(key: String, data: Map[String, String]) = {
        try {
          val name = data.getOrElse(key, "")
          Right(FieldType.withName(name))
        } catch {
          case e: Exception => Left(List(FormError(key, "Wrong field type name")))
        }
      }

      def unbind(key: String, value: FieldType.FieldType) =
        Map(key -> value.toString)
    }

  private val fieldMapping = mapping(
    "fieldType" -> Forms.of[FieldType.FieldType],
    "content" -> text,
    "ordinal" -> number,
    "url" -> text,
    "description" -> text
  )(FieldDTO.apply)(FieldDTO.unapply)

  private val sectionMapping = mapping(
    "ordinal" -> number,
    "title" -> nonEmptyText,
    "fields" -> list(
      fieldMapping
    )
  )(SectionDTO.apply)(SectionDTO.unapply)

  private val pageMapping = mapping(
    "ordinal" -> number,
    "title" -> nonEmptyText,
    "sections" -> list(
      sectionMapping
    )
  )(PageDTO.apply)(PageDTO.unapply)

  def nonEmptyList[T]: Constraint[List[T]] = Constraint[List[T]]("constraint.required") { o =>
    if (o.nonEmpty) Valid else Invalid(ValidationError("error.required"))
  }

  var pagesForm = Form(
    mapping(
      "pages" -> list(
        pageMapping
      ).verifying(nonEmptyList)
    )(PagesDTO.apply)(PagesDTO.unapply)
  )
}
