package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
// Akka imports
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import akka.actor.{Props, ActorSystem}
// slick db access libs
import play.api.db.DB
import scala.slick.driver.MySQLDriver.simple._

import models._
import models.UsersActor.{UserInit, UserRetrieve}
import globals._

object Application extends Controller with UserJSONTrait {

	implicit val timeout = Timeout(5 seconds)
	implicit lazy val system = ActorSystem()
	implicit lazy val userActor = system.actorOf(Props[UsersActor])

	def index = Action {
	    Ok(views.html.index("Your play-compose-seed application is ready."))
	}

	def initUser(name: String) = Action {
		var retUserOpt = None: Option[User]

		val future = userActor ? UserInit(Global.db, name)
		retUserOpt = Await.result(future, timeout.duration)
						.asInstanceOf[Option[User]] 

		if (retUserOpt.isDefined) {
			Ok(Json.toJson(retUserOpt.get))
		} else {
			InternalServerError
		}
	}

	def getUser(id: Long) = Action {
		var retUserOpt = None: Option[User]

		val future = userActor ? UserRetrieve(Global.db, id)
		retUserOpt = Await.result(future, timeout.duration)
						.asInstanceOf[Option[User]]

		if (retUserOpt.isDefined) {
			Ok(Json.toJson(retUserOpt.get))
		} else {
			InternalServerError
		}
	}

}