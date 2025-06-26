package com.example.spark

import org.apache.spark.sql.{SparkSession, DataFrame}

/**
 * A simple class to encapsulate the logic of our Spark job.
 * This demonstrates abstraction by separating the "what" from the "how".
 *
 * @param spark The active SparkSession.
 */
class SparkJob(val spark: SparkSession) {
  // Import implicits from the SparkSession to enable conversions like .toDF()
  import spark.implicits._

  /**
   * Creates a sample DataFrame.
   * @return A DataFrame with a single column "message".
   */
  def createMessageData(): DataFrame = {
    val data = Seq(
      ("Hello, World!"),
      ("This is a Spark application on Kubernetes.")
    )
    data.toDF("message")
  }

  /**
   * The main execution logic for the job.
   */
  def run(): Unit = {
    println("SparkJob is running...")
    val messagesDF = createMessageData()

    println("Showing sample data from DataFrame:")
    messagesDF.show(false) // `false` prevents truncating long strings

    val messageCount = messagesDF.count()
    println(s"Successfully processed $messageCount messages.")
  }
}

/**
 * The main entry point for the Spark application.
 * Its primary responsibility is to initialize the SparkSession
 * and delegate the execution to the SparkJob class.
 */
object HelloWorld {
  def main(args: Array[String]): Unit = {
    println("Initializing Spark Session...")

    // Create a SparkSession. When running on Kubernetes, the master URL
    // will be provided by the Spark driver environment, so we don't
    // need to set it to "local[*]" here. The appName is a good practice.
    val spark = SparkSession.builder
      .appName("ScalaHelloWorldSparkApp")
      .getOrCreate()

    // Create an instance of our job and run it
    val job = new SparkJob(spark)
    job.run()

    // Stop the SparkSession to release resources
    println("Stopping Spark Session.")
    spark.stop()
  }
}
