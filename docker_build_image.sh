#!/usr/bin/env bash
image=ece465_zkapp
tag=v1

image_and_tag="${image}:${tag}"
image_and_tag_array=(${image_and_tag//:/ })
if [[ "$(docker images ${image_and_tag_array[0]} | grep ${image_and_tag_array[1]} 2> /dev/null)" != "" ]]; then
  echo "exists"
  docker image rm ${image_and_tag}
else
  echo "${image_and_tag} doesn't exist yet"
fi
mvn clean package -Dmaven.test.skip=true && \
docker build -t ece465_zkapp:v1 .