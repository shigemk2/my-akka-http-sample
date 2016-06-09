package com.example

import com.amazonaws.auth.{BasicAWSCredentials, AWSCredentialsProvider}
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.kinesis.model.PutRecordResult
import com.amazonaws.services.kinesis.{AmazonKinesisClient, AmazonKinesis}

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

  def put(key: String, value: String): PutRecordResult
}