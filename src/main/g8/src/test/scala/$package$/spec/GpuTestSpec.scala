package $organization$.spec

import $organization$.SparkSessionTestWrapper
import com.github.mrpowers.spark.fast.tests.DataFrameComparer
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}
import $organization$.runner.TestRunner // Import TestRunner to access the shared SparkSession
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

  describe("SqlOnGpuExample") {
    it("should correctly filter data using SQL and produce expected output") {
      // Assign 'this.spark' to a local val to make it a stable identifier for implicits
      val currentSpark = this.spark
      import currentSpark.implicits._

      // Create the initial DataFrame as done in GpuTest.scala
      val initialData = Seq(1, 2, 3).toDF("value")
      initialData.createOrReplaceTempView("df") // Create the temporary view

      // Execute the SQL query directly within the test, mirroring GpuTest's logic
      val actualDF = currentSpark.sql("SELECT value FROM df WHERE value <> 1")

      // Define the expected schema and data for the output DataFrame
      // The query "SELECT value FROM df WHERE value <> 1" on Seq(1, 2, 3)
      // should result in values 2 and 3.
      val expectedSchema = StructType(
        Seq(
          StructField(
            "value",
            IntegerType,
            nullable = true
          ) // Nullable based on how Seq.toDF infers
        )
      )
      val expectedData = Seq(
        Row(2),
        Row(3)
      )
      val expectedDF = currentSpark.createDataFrame(
        currentSpark.sparkContext.parallelize(expectedData),
        expectedSchema
      )

      // Assert that the actual DataFrame matches the expected one
      // ignoreNullable = true is often useful as Spark's toDF can infer nullability
      // differently than explicit schema definition, but the values match.
      assertSmallDataFrameEquality(actualDF, expectedDF, ignoreNullable = true)
    }
  }
}
