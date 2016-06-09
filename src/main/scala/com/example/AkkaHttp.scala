package com.example

import java.nio.ByteBuffer
import java.util.Calendar

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server._

import com.amazonaws.auth.{AWSCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.kinesis.model.{PutRecordRequest, PutRecordResult}
import com.amazonaws.services.kinesis.{AmazonKinesis, AmazonKinesisClient}
import com.typesafe.config.ConfigFactory
import org.apache.commons.lang.RandomStringUtils

trait Kinesis {
  val accessKeyId = System.getProperty("accessKeyId")
  val secretAccessKey = System.getProperty("secretAccessKey")

  val appName = "kinesis-test-app"
  val streamName = "kinesis-test-stream"

  val initialPosition = "LATEST"
  val region = "ap-northeast-1"
  val credentialsProvider: AWSCredentialsProvider = new StaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey))

  val kinesis: AmazonKinesis = new AmazonKinesisClient(credentialsProvider)
  kinesis.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1))

  def put(data: String): PutRecordResult
}
object AkkaHttp extends App with Kinesis {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  import Directives._

  val route =
    parameters('color, 'backgroundColor) { (color, backgroundColor) =>
      val data: String = s"The color is '$color' and the background is '$backgroundColor'"
      put(data)
      complete(data)
    }

  val bindingFuture = Http().bindAndHandle(route, config.getString("http.interface"), config.getInt("http.port"))

  override def put(data: String): PutRecordResult = {
    val key = RandomStringUtils.randomAlphanumeric(10)
    val request: PutRecordRequest = new PutRecordRequest()
    request.setStreamName(streamName)
    request.setData(ByteBuffer.wrap(data.getBytes("UTF-8")))
    request.setPartitionKey(key)
    val putRecord: PutRecordResult = kinesis.putRecord(request)
    println(s"key:${key} ,record:${data}, ${putRecord}")
    println("--------")
    kinesis.putRecord(request)
  }
}
