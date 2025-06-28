package com.example.spark.app

import com.example.spark.app.HelloWorld
import com.example.spark.test.SparkSessionTestWrapper
import com.example.spark.runner.TestRunner // Import TestRunner to access the shared SparkSession
import com.github.mrpowers.spark.fast.tests.DataFrameComparer
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.scalatest.funspec.AnyFunSpec

class HelloWorldSpec
    extends AnyFunSpec
    with DataFrameComparer
    with SparkSessionTestWrapper {

  // Implement the abstract 'spark' val from SparkSessionTestWrapper
  // This makes the SparkSession managed by TestRunner available to this test suite.
  override def spark: SparkSession = TestRunner.sparkSessionInstance.getOrElse(
    throw new IllegalStateException(
      "SparkSession is not initialized by TestRunner."
    )
  )

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
