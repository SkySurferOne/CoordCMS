package dao

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import javax.inject.Inject

import models.{Entity, Event}

import scala.concurrent.{ExecutionContext, Future}


abstract class BaseDAO[T <: Entity] extends HasDatabaseConfigProvider[JdbcProfile] {

  // Create
  def insert(entity: T): Future[Any]
  def insert(entities: Seq[T]): Future[Any]

  // Update
  def update(id: Long)

  // Delete
  def delete(id: Long): Future[Any]

  // Utils
  def count(): Future[Int]

}
