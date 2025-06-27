// -------------------------------------------------------------------
// FILE: build.sbt
// -------------------------------------------------------------------

// Define project versions.
// The Scala version should match the one Spark was built with in your Docker image.
// The apache/spark:3.5.3 image uses Scala 2.12.
val scalaVersionUsed = "2.12.18" // A recent version of Scala 2.12
val sparkVersion = "3.5.3"

lazy val root = (project in file("."))
  .settings(
    name := "spark-template",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scalaVersionUsed,
    organization := "com.example",

    // Add Spark dependencies. They are marked as "provided" because
    // the Spark runtime environment (in the Docker container) will
    // already have these libraries. This keeps our application JAR small.
    // The `%%` will correctly append the `_2.12` suffix.
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",

      // Test dependency for writing Spark tests. It is NOT scoped to "test"
      // so it will be included in the assembly JAR. This is required to allow
      // TestRunner to execute the tests on the Spark cluster.
      // spark-fast-tests brings in scalatest as a transitive dependency.
      "com.github.mrpowers" %% "spark-fast-tests" % "3.0.1",
      "org.scalatest" %% "scalatest" % "3.2.10"
    ),

    // sbt-assembly settings to create a runnable JAR
    assembly / mainClass := Some("com.example.spark.TestRunner"),
    // Use a static JAR name to make the build process more robust.
    assembly / assemblyJarName := "spark-template.jar",
    // Include test classes in the assembly JAR to allow running tests on the cluster.
    assembly / test := true,
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )