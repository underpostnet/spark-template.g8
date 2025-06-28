package com.example.spark.app

import com.example.spark.app.HelloWorld
import com.example.spark.test.SparkSessionTestWrapper
import com.github.mrpowers.spark.fast.tests.DataFrameComparer
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
// Removed BeforeAndAfterAll as SparkSession lifecycle is managed by TestRunner
// import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec

class HelloWorldSpec
    extends AnyFunSpec
    with DataFrameComparer
    with SparkSessionTestWrapper { // Removed BeforeAndAfterAll
  // The 'spark' instance is now provided by SparkSessionTestWrapper.
  // The SparkSession lifecycle (start/stop) is now managed by TestRunner,
  // so there's no need for individual test suites to stop it.
  // The original afterAll method has been removed to prevent premature SparkSession stops.
  /*
  override def afterAll(): Unit = {
    if (spark != null) {
      spark.stop()
    }
    super.afterAll() // Call super.afterAll() if you have other cleanup
  }
   */

  describe("createGreetingDF") {
    it("creates a DataFrame with a 'Hello, World!' message") {
      // Call the method under test
      val actualDF = HelloWorld.createGreetingDF(spark)

      // Define the expected result.
      val expectedSchema =
        StructType(Seq(StructField("message", StringType, nullable = false)))
      val expectedData = Seq(Row("Hello, World!"))
      val expectedDF = spark.createDataFrame(
        spark.sparkContext.parallelize(expectedData),
        expectedSchema
      )

      // Assert that the actual DataFrame matches the expected one
      assertSmallDataFrameEquality(actualDF, expectedDF, ignoreNullable = true)
    }
  }
}
