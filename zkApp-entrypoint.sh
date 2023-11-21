#!/bin/bash
set -e

echo "Container's IP address: `awk 'END{print $1}' /etc/hosts`"

java -Dserver.port=8081 -Dzk.url=localhost:2181 -Dleader.algo=2 -jar /zkApp.jar 2>&1 > /node.log

java -jar /zkApp.jar $@
# if [ "$1" = 'server' ]; then
#     sh examples/server/$@ -bi
# else
#     if [ "$1" = 'client' ]; then
#         sh examples/client/$@
#     else
#         exec "$@"
#     fi
# fi