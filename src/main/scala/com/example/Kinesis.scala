package com.example

import com.amazonaws.services.kinesis.model.PutRecordResult

trait Kinesis {
  val accessKeyId = Option(System.getProperty("accessKeyId"))
  val secretAccessKey = Option(System.getProperty("secretAccessKey"))

  val appName = "kinesis-test-app"
  val streamName = "kinesis-test-stream"

  val initialPosition = "LATEST"
  val region = "ap-northeast-1"

  def put(key: String, value: String): PutRecordResult
}