package com.example.spark

import org.apache.spark.sql.SparkSession

object HelloWorld {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("HelloWorld")
      .getOrCreate()
    import spark.implicits._
    Seq("Hello, World!").toDF("message").show(false)
    spark.stop()
  }
}
