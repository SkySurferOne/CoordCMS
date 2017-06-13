package bootstrap

import play.api.Logger
import javax.inject.Inject

import dao.EventDAO
import models.{Event, EventCategory}
import org.joda.time.DateTime

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.Try

private[bootstrap] class InitialData @Inject() (eventDAO: EventDAO)
                            (implicit executionContext: ExecutionContext) {

  def insert(): Unit = {
    val insertInitialDataFuture = for {
      count <- eventDAO.count() if count == 0
      _ <- eventDAO.insert(InitialData.events)
    } yield()

    Try(Await.result(insertInitialDataFuture, Duration.Inf))
  }

  Logger.debug("I'm in InitialData")
  insert()
}

private[bootstrap] object InitialData {

  def events = Seq(
    Event(Option(1L), "SSA4", "Nice softskills wokshop", EventCategory.Workshops, DateTime.now(), DateTime.now.plusDays(2)),
    Event(Option(2L), "Web Platform", "For HP lovers", EventCategory.InternationalEvent, DateTime.now(), DateTime.now.plusDays(4)),
    Event(Option(3L), "TPA", "Nice softskills wokshop", EventCategory.LocalEvent, DateTime.now(), DateTime.now.plusDays(2)),
    Event(Option(4L), "Hacknarok", "Best hackathon in the world!", EventCategory.LocalEvent, DateTime.now(), DateTime.now.plusDays(1)),
    Event(Option(5L), "Android Master", "Android workshops", EventCategory.Workshops, DateTime.now(), DateTime.now.plusDays(5))
  )
}
