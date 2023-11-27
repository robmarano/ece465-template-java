#!/usr/bin/env bash
APP_JAR=$(pwd)/target/zkApp-1.0-SNAPSHOT.jar
SERVER_PORT=808
MGMT_PORT=909
/bin/rm -rf ./nodes && \
mkdir -p nodes/node1 && \
mkdir -p nodes/node2 && \
mkdir -p nodes/node3 && \
NODE=1 && \
cd ./nodes/node${NODE} && \
(java -Dserver.port=${SERVER_PORT}${NODE} -Dmanagement.server.port=${MGMT_PORT}${NODE} -Dzk.url=localhost:218${NODE} -Dleader.algo=2 -jar ${APP_JAR} 2>&1 > node${NODE}.log &) && \
NODE=2 && \
cd ../node${NODE} && \
(java -Dserver.port=${SERVER_PORT}${NODE} -Dmanagement.server.port=${MGMT_PORT}${NODE} -Dzk.url=localhost:218${NODE} -Dleader.algo=2 -jar ${APP_JAR} 2>&1 > node${NODE}.log &) && \
NODE=3 && \
cd ../node${NODE} && \
(java -Dserver.port=${SERVER_PORT}${NODE} -Dmanagement.server.port=${MGMT_PORT}${NODE} -Dzk.url=localhost:218${NODE} -Dleader.algo=2 -jar ${APP_JAR} 2>&1 > node${NODE}.log &) && \
cd ../..

#mvn verify sonar:sonar -Dsonar.login=token

#
# which process IDs (pids) are running?
# ps aux|egrep zkApp-1.0-SNAPSHOT.jar | egrep -v egrep | awk {'print $2'}

# kill the running pids
# ps aux|egrep zkApp-1.0-SNAPSHOT.jar | egrep -v egrep | awk {'print $2'} | xargs kill
