// -------------------------------------------------------------------
// FILE: build.sbt
// -------------------------------------------------------------------

// Define the Scala version and project metadata
val scala3Version = "3.4.1"

lazy val root = (project in file("."))
  .settings(
    name := "scala-underpost-project",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    organization := "com.example",

    // Define Spark version. This should be consistent across your
    // Docker image and Kubernetes spec.
    val sparkVersion = "3.5.1",

    // Add Spark dependencies. They are marked as "provided" because
    // the Spark runtime environment (in the Docker container) will
    // already have these libraries. This keeps our application JAR small.
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
    ),

    // sbt-assembly settings to create a runnable JAR
    assembly / mainClass := Some("com.example.spark.HelloWorld"),
    assembly / assemblyJarName := s"${name.value}-assembly-${version.value}.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )