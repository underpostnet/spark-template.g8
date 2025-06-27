package com.example.spark.test

import org.apache.spark.sql.SparkSession

/**
 * A trait to provide a standardized SparkSession for test suites.
 * Inspired by spark-fast-tests documentation.
 */
trait SparkSessionTestWrapper {
  lazy val spark: SparkSession = {
    SparkSession
      .builder()
      .master("local[*]") // Use all available cores for local testing
      .appName("Spark Test Suite")
      .config("spark.sql.shuffle.partitions", "1") // Optimize for local test performance
      .getOrCreate()
  }
}