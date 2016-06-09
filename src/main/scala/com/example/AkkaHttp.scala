package com.example

import java.nio.ByteBuffer
import java.util.Calendar

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server._

import com.amazonaws.services.kinesis.model.{PutRecordRequest, PutRecordResult}
import com.typesafe.config.ConfigFactory

object AkkaHttp extends App with Kinesis {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  import Directives._

  val route =
    parameters('key, 'value) { (key, value) =>
      put(key, value)
      complete(s"key: ${key}, value: ${value}")
    }

  val bindingFuture = Http().bindAndHandle(route, config.getString("http.interface"), config.getInt("http.port"))

  override def put(key: String, value: String): PutRecordResult = {
    val request: PutRecordRequest = new PutRecordRequest()
    request.setStreamName(streamName)
    request.setData(ByteBuffer.wrap(value.getBytes("UTF-8")))
    request.setPartitionKey(key)
    val putRecord: PutRecordResult = kinesis.putRecord(request)
    println(s"key:${key} ,record:${value}, ${putRecord}")
    println("--------")
    kinesis.putRecord(request)
  }
}
