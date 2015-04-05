package models

import play.api.db.DB
import scala.slick.driver.MySQLDriver.simple._

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Set

import akka.actor.Actor

case class User(
	var name: Option[String] = None,
	var updtime: Option[Long] = Option(System.currentTimeMillis()/1000L),
	var tombstone: Option[Int] = Option(0),
	var inittime: Option[Long] = Option(System.currentTimeMillis()/1000L),
	var id: Option[Long] = None)

class Users(tag: Tag)
	extends Table[User](tag, "user") {
	
	def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
	def name = column[Option[String]]("name")
	def tombstone = column[Option[Int]]("tombstone")
	def inittime = column[Option[Long]]("init_time")
	def updtime = column[Option[Long]]("update_time")

	def * = (name, updtime, tombstone, inittime, id) <>
			(User.tupled, User.unapply _)
}

trait UserJSONTrait {
	implicit val UserJSONFormat = Json.format[User]
}

object Users extends UserJSONTrait {
	val table = TableQuery[Users]

	def init(name: String)
		(implicit session: Session): Option[User] = {
		var retUserOpt = None: Option[User]

		// construct a user and insert into database
		val user = User(Option(name))
		val idOpt = (table returning table.map(_.id)) += user
		user.id = idOpt

		// return the AutoInc id
		retUserOpt = Option(user)

		retUserOpt	
	}

	def retrieve(id: Long)
		(implicit session: Session): Option[User] = {
		var retUserOpt = None: Option[User]

		val queryUserOpt = table.filter(_.id === id)
								.filter(_.tombstone === 0)
								.take(1).firstOption
		if (queryUserOpt.isDefined) {
			retUserOpt = Option(queryUserOpt.get)
		}

		retUserOpt
	}
}

import models.UsersActor.{UserInit, UserRetrieve}

object UsersActor {
	case class UserInit(db: Database, name: String)
	case class UserRetrieve(db: Database, id: Long)
}

class UsersActor extends Actor {
	def receive: Receive = {
		case UserInit(db: Database, name: String) => {
			db.withSession { implicit session =>
				sender ! Users.init(name)
			}
		}
		case UserRetrieve(db: Database, id: Long) => {
			db.withSession { implicit session =>
				sender ! Users.retrieve(id)
			}
		}
	}
}