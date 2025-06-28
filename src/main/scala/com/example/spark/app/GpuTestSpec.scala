package com.example.spark.app

import com.example.spark.test.SparkSessionTestWrapper
import com.github.mrpowers.spark.fast.tests.DataFrameComparer
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec

class GpuTestSpec
    extends AnyFunSpec
    with DataFrameComparer
    with SparkSessionTestWrapper
    with BeforeAndAfterAll {

  // Ensure SparkSession is stopped after all tests in this suite.
  override def afterAll(): Unit = {
    if (spark != null) {
      spark.stop()
    }
    super.afterAll() // Call super.afterAll() if you have other cleanup
  }

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
