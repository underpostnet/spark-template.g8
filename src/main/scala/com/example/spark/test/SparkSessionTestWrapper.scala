package com.example.spark.test

import org.apache.spark.sql.SparkSession
// Removed BeforeAndAfterAll import as we are removing its usage here
// import org.scalatest.BeforeAndAfterAll // No longer needed here

/** A trait to provide a standardized SparkSession for test suites. The
  * SparkSession lifecycle (starting and stopping) should be managed externally,
  * for example, by the main application or test runner. This trait ensures
  * tests use a shared SparkSession without prematurely stopping it.
  */
trait SparkSessionTestWrapper {
  lazy val spark: SparkSession = {
    SparkSession
      .builder()
      .master("local[*]") // Use all available cores for local testing
      .appName("Spark Test Suite")
      .config(
        "spark.sql.shuffle.partitions",
        "1"
      ) // Optimize for local test performance
      .getOrCreate()
  }

  // Removed the afterAll method. The SparkSession is now stopped by the TestRunner
  // after all test suites have completed.
  // override def afterAll(): Unit = {
  //   if (spark != null) {
  //     spark.stop()
  //   }
  //   super.afterAll() // Call super.afterAll() if you have other cleanup
  // }
}
