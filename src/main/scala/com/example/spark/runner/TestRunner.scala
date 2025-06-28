package com.example.spark.runner

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

    // Explicitly specify the test suite to run using the '-s' flag.
    // The '-o' flag prints results to standard out.
    // This bypasses ScalaTest's discovery mechanism (`-R` flag), which can be
    // unreliable in complex classloader environments like a Spark driver.
    // By directly naming the test class, we ensure it's loaded and run.
    // The fully qualified name must match the test's package and class name.
    val testResult = Runner.run(
      Array(
        "-o",
        "-s",
        "com.example.spark.app.HelloWorldSpec", // Existing test suite
        "-s",
        "com.example.spark.app.GpuTestSpec" // New GPU test suite
      )
    )

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
