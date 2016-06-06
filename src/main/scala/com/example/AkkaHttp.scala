package com.example

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object AkkaHttp extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  val route = path("") {
    get {
      handleWith((a: HttpRequest) => s"your request is\n\n${a.headers.mkString("\n")}")
    }
  }

  Http().bindAndHandle(route, config.getString("http.interface"), config.getInt("http.port"))
}
