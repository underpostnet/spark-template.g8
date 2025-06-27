package com.example.spark

import org.apache.spark.sql.SparkSession
import org.scalatest.tools.Runner

object TestRunner {
  def main(args: Array[String]): Unit = {
    // Although the tests create their own Spark session via SparkTestingBase,
    // we create one here. This is because when running on a cluster,
    // Spark context initialization needs to happen in the driver's main method
    // for resource allocation to work correctly. The test-specific sessions
    // will likely reuse this context.
    val spark = SparkSession.builder
      .appName("Spark Test Runner")
      .getOrCreate()

    println("=============================================")
    println("===          RUNNING SPARK TESTS          ===")
    println("=============================================")

    // Discover and run all ScalaTest suites.
    // The '-o' flag prints results to standard out.
    // By default, ScalaTest Runner discovers tests from the classpath.
    // Since spark-submit places the application JAR on the driver's classpath,
    // we will explicitly specify the test suite to run using the '-s' flag.
    // This bypasses the discovery mechanism and directly attempts to load and run the suite.
    // If this works, it confirms the suite is correctly packaged and loadable,
    // indicating the issue is specifically with ScalaTest's discovery process
    // in the Spark Kubernetes environment.
    val testResult = Runner.run(Array("-o", "-s", "com.example.spark.HelloWorldSpec"))

    println("=============================================")
    println("===        SPARK TESTS COMPLETED          ===")
    println("=============================================")

    spark.stop()

    // Exit with a non-zero status code if tests failed.
    if (!testResult) {
      System.exit(1)
    }
  }
}