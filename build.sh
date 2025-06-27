PROJECT_DIR=/home/dd/spark-template
PROJECT_VERSION=latest
IMAGE_NAME=spark-template

IMAGE_NAME_FULL="${IMAGE_NAME}:${PROJECT_VERSION}"

cd ${PROJECT_DIR}

# Pull the builder image that matches the project's Scala version.
sudo podman pull docker.io/sbtscala/scala-sbt:eclipse-temurin-jammy-11.0.17_8_1.9.3_2.12.18
sudo podman pull apache/spark:3.5.3-scala2.12-java17-python3-r-ubuntu

sudo rm -rf ${PROJECT_DIR}/${IMAGE_NAME}_${PROJECT_VERSION}.tar

underpost dockerfile-image-build --path ${PROJECT_DIR} --image-name=${IMAGE_NAME_FULL} --image-path=${PROJECT_DIR} --podman-save --kind-load --no-cache

# Apply the RBAC rules first to create the service account and its permissions.
# This must be done before the SparkApplication is created.
kubectl apply -f ./manifests/sparkapplication/spark-rbac.yaml

kubectl apply -f ./manifests/sparkapplication/spark-application.yaml
kubectl apply -f ./manifests/sparkapplication/spark-test-runner-application.yaml
kubectl get pods -w -o wide