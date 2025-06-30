## Spark on Kubernetes Template

[![Spark CI](https://github.com/underpostnet/spark-template/actions/workflows/docker-image.yml/badge.svg?branch=master)](https://github.com/underpostnet/spark-template/actions/workflows/docker-image.yml)

Build, test, and deploy Scala Spark applications on Kubernetes using the Spark Operator.

### Core Features

- **Scala & sbt**: Standard Scala project structure managed by `sbt`.
- **Containerized**: A multi-stage `Dockerfile` builds the application and produces a lean runtime image based on the official Spark image.
- **Kubernetes-Native**: Designed for deployment and management via the Spark on Kubernetes Operator.
- **Integrated Testing**: Supports unit and integration testing with `scalatest` and `spark-fast-tests`.
- **In-Cluster Testing**: Demonstrates how to run test suites directly on the Spark cluster, ensuring tests execute in an environment identical to production.
- **Pre-configured RBAC**: Includes necessary Kubernetes Role-Based Access Control (`spark-rbac.yaml`) to allow the Spark driver to manage its executor pods.
