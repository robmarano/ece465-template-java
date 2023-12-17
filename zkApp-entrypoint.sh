#!/bin/bash
set -e

ZK_NODE_IP=$(awk 'END{print $1}' /etc/hosts)
ZK_NODE_HOSTNAME=$(awk 'END{print $2}' /etc/hosts)
APP_LOG=/logs/app-node.log
#echo "Container's IP address: `awk 'END{print $1}' /etc/hosts`"
echo "Container's IP address: $ZK_NODE_IP" > ${APP_LOG}
echo "Container's hostname: $ZK_NODE_HOSTNAME" > ${APP_LOG}
echo "Starting ZooKeeper Server"
zkServer.sh start-foreground 2>&1 > /logs/zk.log &
sleep 30
echo "Starting App Server"
APP_JAR=/zkApp.jar
RUN_CONFIG="-Dserver.port=8081 -Dmanagement.server.port=9091 -Dzk.url=${ZK_NODE_IP}:2181 -Dleader.algo=2"
java ${RUN_CONFIG} -jar ${APP_JAR} 2>&1 > ${APP_LOG} &

#java -Dserver.port=8080 -Dmanagement.server.port=9090 -Dzk.url=zookeeper1:2181 -Dleader.algo=2 -jar ${APP_JAR} 2>&1 > /node.log
#java -Dserver.port=8081 -Dzk.url=localhost:2181 -Dleader.algo=2 -jar /zkApp.jar 2>&1 > /node.log


#java -jar /zkApp.jar $@
# if [ "$1" = 'server' ]; then
#     sh examples/server/$@ -bi
# else
#     if [ "$1" = 'client' ]; then
#         sh examples/client/$@
#     else
#         exec "$@"
#     fi
# fi