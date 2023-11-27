@ECHO OFF
REM
REM run_interactive_node_zkApp3.bat
REM

SET NUM=3
SET NAME=zkApp%NUM%
SET VERSION=v1
SET CMD=bash
@ECHO ON
docker run --name %NAME% --rm=true --interactive --tty --hostname %NAME% --publish 218%NUM%:2181 --publish 960%NUM%:8080 --publish 970%NUM%:8081 --publish 980%NUM%:9090 --env-file zookeeper%NUM%.env -v zookeeper%NUM%-dataDir:/data -v zookeeper%NUM%-dataLogDir:/datalog -v zookeeper%NUM%-logs:/logs --network zookeeper-cluster ece465_zkapp:%VERSION% %CMD%

# http://localhost:9803/actuator/swagger-ui/index.html