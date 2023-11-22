#!/usr/bin/env bash -x

APP_JAR=$(pwd)/target/zkApp-1.0-SNAPSHOT.jar
/bin/rm -rf ./nodes && \
mkdir -p nodes/node1 && \
mkdir -p nodes/node2 && \
mkdir -p nodes/node3 && \
cd nodes/node1 && \
(java -Dserver.port=8081 -Dmanagement.server.port=9091 -Dzk.url=127.0.0.1:2181 -Dleader.algo=2 -jar ${APP_JAR} 2>&1 > node1.log &) && \
cd ../node2 && \
(java -Dserver.port=8082 -Dmanagement.server.port=9092 -Dzk.url=127.0.0.1:2182 -Dleader.algo=2 -jar ${APP_JAR}  2>&1 > node2.log &) && \
cd ../node3 && \
(java -Dserver.port=8083 -Dmanagement.server.port=9093 -Dzk.url=127.0.0.1:2183 -Dleader.algo=2 -jar ${APP_JAR}  2>&1 > node3.log &)
cd ../..

#mvn verify sonar:sonar -Dsonar.login=token

#
# which process IDs (pids) are running?
# ps aux|egrep zkApp-1.0-SNAPSHOT.jar | egrep -v egrep | awk {'print $2'}

# kill the running pids
# ps aux|egrep zkApp-1.0-SNAPSHOT.jar | egrep -v egrep | awk {'print $2'} | xargs kill