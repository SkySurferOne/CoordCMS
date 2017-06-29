package controllers

import javax.inject.{Inject, Singleton}

import dao.{EventDAO, FieldDAO, PageDAO, SectionDAO}
import models._
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
class EventPagesController @Inject()(val eventDAO: EventDAO, val pageDAO: PageDAO, val sectionDAO: SectionDAO, val fieldDAO: FieldDAO,
                                     val messagesApi: MessagesApi, implicit val webJarAssets: WebJarAssets)
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

          val future = pagesData.pages.map { pageDTO =>
            val page = Page(Option.empty, eventId, pageDTO.ordinal, pageDTO.title)

            // add pages
            pageDAO.insert(page).map { pageId =>
              pageDTO.sections.map { sectionDTO =>
                val section = Section(Option.empty, pageId, sectionDTO.ordinal, sectionDTO.title)

                // add sections
                sectionDAO.insert(section).map { sectionId =>
                  sectionDTO.fields.map { fieldDTO =>
                    val field = fieldDTO.fieldType match {
                      case FieldType.Heading => Heading(Option.empty, sectionId, fieldDTO.ordinal, fieldDTO.content)
                      case FieldType.Paragraph => Paragraph(Option.empty, sectionId, fieldDTO.ordinal, fieldDTO.content)
                      case FieldType.Image => Image(Option.empty, sectionId, fieldDTO.ordinal, fieldDTO.url, fieldDTO.description)
                    }

                    // insert fields
                    fieldDAO.insert(field)
                  }
                }
              }
            }
          }

          Future.sequence(future).map { _ =>
            // TODO redirect to single view
            Redirect(routes.HomeController.index())
          }
        }
      )
  }

}

object EventPagesController {

  case class PagesDTO(pages: List[PageDTO])
  case class PageDTO(id: Option[Long], ordinal: Int, title: String, sections: List[SectionDTO])
  case class SectionDTO(id: Option[Long], ordinal: Int, title: String, fields: List[FieldDTO])
  case class FieldDTO(id: Option[Long], fieldType: FieldType, content: String, ordinal: Int, url: String, description: String)

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
    "id" -> optional(longNumber),
    "fieldType" -> Forms.of[FieldType.FieldType],
    "content" -> text,
    "ordinal" -> number,
    "url" -> text,
    "description" -> text
  )(FieldDTO.apply)(FieldDTO.unapply)

  private val sectionMapping = mapping(
    "id" -> optional(longNumber),
    "ordinal" -> number,
    "title" -> nonEmptyText,
    "fields" -> list(
      fieldMapping
    )
  )(SectionDTO.apply)(SectionDTO.unapply)

  private val pageMapping = mapping(
    "id" -> optional(longNumber),
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
