#!/bin/bash

#$1 -- image
#../.././gradlew clean build
docker build . --platform linux/amd64 -t $1
docker push $1
