#!/bin/bash
set -e

echo "Container's IP address: `awk 'END{print $1}' /etc/hosts`"
echo "Starting ZooKeeper Server"
zkServer.sh start-foreground 2>&1 > /dev/null &
sleep 5
echo "Starting App Server"
APP_JAR=/zkApp.jar
java -Dserver.port=8081 -Dmanagement.server.port=9091 -Dzk.url=127.0.0.1:2181 -Dleader.algo=2 -jar ${APP_JAR} 2>&1 > /node.log &

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