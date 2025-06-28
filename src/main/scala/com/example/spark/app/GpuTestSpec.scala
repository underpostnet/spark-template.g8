package com.example.spark.app

import com.example.spark.test.SparkSessionTestWrapper
import com.example.spark.runner.TestRunner // Import TestRunner to access the shared SparkSession
import com.github.mrpowers.spark.fast.tests.DataFrameComparer
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}
import org.scalatest.funspec.AnyFunSpec

class GpuTestSpec
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

  describe("processDataWithUDF") {
    it("should correctly apply the UDF and produce expected output") {
      // Call the method under test
      val actualDF = GpuTest.processDataWithUDF(spark)

      // Define the expected schema and data for the output DataFrame
      val expectedSchema = StructType(
        Seq(
          StructField(
            "value",
            IntegerType,
            nullable = true
          ), // Nullable based on how Seq.toDF infers
          StructField("output", IntegerType, nullable = true)
        )
      )
      val expectedData = Seq(
        Row(1, 2),
        Row(2, 4),
        Row(3, 6),
        Row(4, 8),
        Row(5, 10)
      )
      val expectedDF = spark.createDataFrame(
        spark.sparkContext.parallelize(expectedData),
        expectedSchema
      )

      // Assert that the actual DataFrame matches the expected one
      // ignoreNullable = true is often useful as Spark's toDF can infer nullability
      // differently than explicit schema definition, but the values match.
      assertSmallDataFrameEquality(actualDF, expectedDF, ignoreNullable = true)
    }
  }
}
