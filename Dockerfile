# Stage 1: Build the Scala application using sbt
# We use an official sbt image which contains the necessary tools (sbt, jdk).
FROM sbtscala/scala-sbt:eclipse-temurin-jammy-11.0.17_8_1.9.3_2.13.11 as builder

WORKDIR /app

# Copy only the necessary build files first to leverage Docker layer caching.
# This ensures that dependencies are only re-downloaded if build.sbt changes.
COPY build.sbt .
COPY project/build.properties ./project/
COPY project/plugins.sbt ./project/

# This command will trigger sbt to download dependencies.
RUN sbt update

# Now copy the source code
COPY src ./src

# Build the project and create the "fat" JAR file.
RUN sbt assembly


# Stage 2: Create the final runtime image
# We use an official Apache Spark image as the base.
# The version (3.5.3) and Scala version (2.12) MUST match the dependencies in build.sbt.
# Note: Spark 3.x is typically built with Scala 2.12 or 2.13. Even though we use Scala 3
# to compile, the Spark libraries are for 2.13, which is compatible.
FROM apache/spark:3.5.3-scala2.12-java17-python3-r-ubuntu

# The Spark operator runs applications from this directory by default.
WORKDIR /opt/spark/work-dir

# Copy the assembled JAR from the builder stage into the final image.
COPY --from=builder /app/target/scala-2.12/spark-scala.jar .

# The image is now ready. The Spark operator will invoke `spark-submit`
# with this image and point it to the JAR file inside.
