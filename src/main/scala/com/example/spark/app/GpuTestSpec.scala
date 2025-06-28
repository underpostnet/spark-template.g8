package com.example.spark.app

import org.apache.spark.sql.SparkSession
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, Outcome}

class GpuTestSpec extends AnyFunSpec with Matchers with BeforeAndAfterAll {

  // Use a transient lazy val for the SparkSession.
  @transient private var spark: SparkSession = _

  /**
   * Set up a SparkSession with RAPIDS plugin configuration before tests run.
   * This is crucial for simulating the GPU-enabled environment.
   */
  override def beforeAll(): Unit = {
    super.beforeAll()
    spark = SparkSession.builder
      .appName("GpuTestSpec")
      .master("local[*]")
      .config("spark.plugins", "com.nvidia.spark.SQLPlugin")
      .config("spark.rapids.sql.enabled", "true")
      // The following configs are good practice but may not be strictly
      // necessary for a simple local test run.
      .config("spark.sql.shuffle.manager", "com.nvidia.spark.rapids.spark351.RapidsShuffleManager")
      .getOrCreate()
  }

  /**
   * Clean up the SparkSession after all tests are complete.
   */
  override def afterAll(): Unit = {
    if (spark != null) {
      spark.stop()
    }
    super.afterAll()
  }

  // Instantiate the logic to be tested.
  object TestGpuOperations extends GpuOperations

  describe("GpuOperations with RAPIDS") {

    it("should confirm that the RAPIDS plugin is configured in the SparkSession") {
      val isEnabled = TestGpuOperations.isRapidsPluginEnabled(spark)
      isEnabled shouldBe true
    }

    it("should execute a GPU-accelerated aggregation successfully") {
      // Execute the operation.
      val resultDF = TestGpuOperations.performGpuAggregation(spark)

      // Collect results to trigger the computation.
      val results = resultDF.collect()

      // Assert basic properties of the result.
      results.length shouldBe 10 // We have 10 categories.
      resultDF.columns should contain allOf ("category", "count", "avg_value")

      // Note: This test confirms the code runs without error with the plugin enabled.
      // To truly verify GPU execution, you must inspect the Spark UI or logs for
      // operators prefixed with "Gpu" (e.g., GpuHashAggregateExec).
    }
  }
}