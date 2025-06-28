package com.example.spark.app

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._ // Import for UDFs

object GpuTest {

  /** Defines a simple UDF that performs a calculation. In a true
    * GPU-accelerated Spark environment (e.g., with Spark-Rapids), certain UDFs
    * can be offloaded to the GPU. This is a placeholder for such logic. For
    * demonstration, we'll simply multiply by 2.
    *
    * @param spark
    *   The SparkSession.
    * @return
    *   A DataFrame with a 'value' column and an 'output' column after UDF
    *   application.
    */
  def processDataWithUDF(spark: SparkSession): DataFrame = {
    import spark.implicits._

    // Sample data
    val data = Seq(1, 2, 3, 4, 5).toDF("value")

    // Define a UDF. For actual GPU acceleration with Spark, this would
    // typically involve a catalyst expression that Spark-Rapids can optimize
    // or a custom GPU-aware UDF. This is a simple example.
    val multiplyByTwoUDF = udf((value: Int) => value * 2)

    // Apply the UDF
    val resultDF = data.withColumn("output", multiplyByTwoUDF(col("value")))

    resultDF
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("GpuTestApp")
      .getOrCreate()

    println("=============================================")
    println("===          RUNNING GPU TEST APP         ===")
    println("=============================================")

    val result = processDataWithUDF(spark)
    result.show()

    println("=============================================")
    println("===        GPU TEST APP COMPLETED         ===")
    println("=============================================")

    spark.stop()
  }
}
