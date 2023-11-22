#!/usr/bin/env bash -x

APP_JAR=$(pwd)/target/zkApp-1.0-SNAPSHOT.jar
clear && mvn clean package -Dmaven.test.skip=true