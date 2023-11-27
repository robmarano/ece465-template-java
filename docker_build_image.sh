#!/usr/bin/env bash

docker image rm ece465_zkapp:v1 && \
mvn clean package -Dmaven.test.skip=true && \
docker build -t ece465_zkapp:v1 .