package com.example

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpHeader, HttpRequest}
import spray.json.DefaultJsonProtocol._

object AkkaHttp extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  case class HelloRequest(method: String, uri: String, headers: String, entity: String, protocol: String)
  case class HelloResponse(message: String)

  implicit val helloRequestProtocol = jsonFormat5(HelloRequest)
  implicit val helloReponseProtocol = jsonFormat1(HelloResponse)

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)


  val route = path("") {
    get {
      handleWith((a: HttpRequest) => {
        HelloRequest(a.method.toString(), a.uri.toString, a.headers.toString, a.entity.toString, a.protocol.toString)
      })
    }
  }

  Http().bindAndHandle(route, config.getString("http.interface"), config.getInt("http.port"))
}
