// -------------------------------------------------------------------
// FILE: project/plugins.sbt
// -------------------------------------------------------------------
// This plugin is used to create a single "fat" JAR containing all
// dependencies, which is standard for Spark applications.
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.2.0")