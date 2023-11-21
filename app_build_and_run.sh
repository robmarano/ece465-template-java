#!/usr/bin/env bash -x

APP_JAR=$(pwd)/target/zkApp-1.0-SNAPSHOT.jar
clear && mvn clean package -Dmaven.test.skip=true && \
/bin/rm -rf ./nodes && \
mkdir -p nodes/node1 && \
mkdir -p nodes/node2 && \
mkdir -p nodes/node3 && \
cd nodes/node1 && \
(java -Dserver.port=8081 -Dzk.url=localhost:2181 -Dleader.algo=2 -jar ${APP_JAR} 2>&1 > node1.log &) && \
cd ../node2 && \
(java -Dserver.port=8082 -Dzk.url=localhost:2182 -Dleader.algo=2 -jar ${APP_JAR}  2>&1 > node2.log &) && \
cd ../node3 && \
(java -Dserver.port=8083 -Dzk.url=localhost:2183 -Dleader.algo=2 -jar ${APP_JAR}  2>&1 > node3.log &)
cd ../..

#mvn verify sonar:sonar -Dsonar.login=token