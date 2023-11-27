@ECHO OFF
REM
REM run_zkApp1_interactive.bat
REM

SET NAME=zkApp1
SET VERSION=v1
SET CMD=bash
@ECHO ON
docker run --name %NAME% --rm=true --interactive --tty --hostname %NAME% --publish 2181:2181 --publish 9665:8080 --publish 8091:8081 --publish 9091:9090 --env-file zookeeper1.env -v zookeeper1-dataDir:/data -v zookeeper1-dataLogDir:/datalog -v zookeeper1-logs:/logs --network zookeeper-cluster ece465_zkapp:%VERSION% %CMD%
