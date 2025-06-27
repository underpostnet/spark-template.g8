package com.example.spark

import com.example.spark.app.HelloWorld
import com.example.spark.testutil.SparkSessionTestWrapper
import com.github.mrpowers.spark.fast.tests.DataFrameComparer
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec

class HelloWorldSpec extends AnyFunSpec with DataFrameComparer with SparkSessionTestWrapper with BeforeAndAfterAll {
  // The 'spark' instance is now provided by SparkSessionTestWrapper.
  // We still need BeforeAndAfterAll to explicitly stop the SparkSession
  // after all tests in this suite are done, ensuring resources are released.
  override def afterAll(): Unit = {
    if (spark != null) {
      spark.stop()
    }
    super.afterAll() // Call super.afterAll() if you have other cleanup
  }

  describe("createGreetingDF") {
    it("creates a DataFrame with a 'Hello, World!' message") {
      // Call the method under test
      val actualDF = HelloWorld.createGreetingDF(spark)

      // Define the expected result.
      val expectedSchema = StructType(Seq(StructField("message", StringType, nullable = false)))
      val expectedData = Seq(Row("Hello, World!"))
      val expectedDF = spark.createDataFrame(spark.sparkContext.parallelize(expectedData), expectedSchema)

      // Assert that the actual DataFrame matches the expected one
      assertSmallDataFrameEquality(actualDF, expectedDF, ignoreNullable = true)
    }
  }
}