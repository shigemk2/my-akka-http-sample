package com.example

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object AkkaHttp extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  import Directives._

  val route =
    parameters('color, 'backgroundColor) { (color, backgroundColor) =>
      complete(s"The color is '$color' and the background is '$backgroundColor'")
    }

  val bindingFuture = Http().bindAndHandle(route, config.getString("http.interface"), config.getInt("http.port"))
}
