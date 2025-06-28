package com.example.spark.app

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{avg, col, count, rand}

/**
 * A trait that encapsulates GPU-accelerated Spark operations.
 * By defining the logic in a trait, we can easily mix it into different
 * contexts (e.g., a main application, a test suite) and mock it if needed.
 */
trait GpuOperations {

  /**
   * Performs a simple aggregation on a DataFrame. This type of operation
   * is a prime candidate for GPU acceleration with RAPIDS.
   *
   * @param spark The active SparkSession.
   * @return A DataFrame with the aggregated results.
   */
  def performGpuAggregation(spark: SparkSession): DataFrame = {
    import spark.implicits._

    // Create a sample DataFrame.
    val df = (1 to 100000).toDF("id")
      .withColumn("value", rand(seed = 42) * 100)
      .withColumn("category", (col("id") % 10).cast("string"))

    // Perform a groupBy and aggregation.
    df.groupBy("category")
      .agg(
        count("*").as("count"),
        avg("value").as("avg_value")
      )
      .orderBy("category")
  }

  /**
   * Checks if the RAPIDS SQL plugin is enabled in the Spark configuration.
   * @param spark The active SparkSession.
   * @return True if the plugin is configured, false otherwise.
   */
  def isRapidsPluginEnabled(spark: SparkSession): Boolean = {
    spark.conf.getOption("spark.plugins").contains("com.nvidia.spark.SQLPlugin")
  }
}

/**
 * Main application object to run the GpuTest logic.
 * This demonstrates how the GpuOperations trait can be used.
 */
object GpuTest extends GpuOperations {
  // You could add a main method here to run this as a standalone Spark job.
}