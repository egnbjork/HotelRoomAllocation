#!/usr/bin/env sh
set -e

echo "Building application..."
./gradlew clean bootJar -x test

echo "Starting application..."
export "$(<.env xargs)"
java -jar build/libs/*.jar
exit 1
