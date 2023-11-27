@ECHO OFF
REM
REM cluster_app_run.bat
REM
REM Run clusters after you create the cluster with cluster_zk_create.sh

REM using docker image zookeeper:3.8.3
REM separate servers for ZK and for APP clusters

REM Create and run ZK node 1
REM docker run -d --rm=true --publish 2181:2181 --publish 9665:8080 --publish 8091:8081 --env-file zookeeper1.env -v zookeeper1-dataDir:/data -v zookeeper1-dataLogDir:/datalog -v zookeeper1-logs:/logs --name zkApp1 --network zookeeper-cluster zookeeper:3.8.3

REM Create and run ZK node 2
REM docker run -d --rm=true --publish 2182:2181 --publish 9666:8080 --publish 8092:8081 --env-file zookeeper2.env -v zookeeper2-dataDir:/data -v zookeeper2-dataLogDir:/datalog -v zookeeper2-logs:/logs --name zkApp2 --network zookeeper-cluster zookeeper:3.8.3

REM Create and run ZK node 3
REM docker run -d --rm=true --publish 2183:2181 --publish 9667:8080 --publish 8093:8081 --env-file zookeeper3.env -v zookeeper3-dataDir:/data -v zookeeper3-dataLogDir:/datalog -v zookeeper3-logs:/logs --name zkApp3 --network zookeeper-cluster zookeeper:3.8.3

REM using docker image ece465_zkapp:v1
REM one for each node running both ZK and for APP

@ECHO ON
SET IMAGE=ece465_zkapp
SET VERSION=v1
REM SET IMAGE=zookeeper
REM SET VERSION=3.8.3


REM Create and run ZK node 1
REM docker run -d --rm=true --publish 2181:2181 --publish 9665:8080 --publish 8091:8081 --publish 9091:9090 --env-file zookeeper1.env -v zookeeper1-dataDir:/data -v zookeeper1-dataLogDir:/datalog -v zookeeper1-logs:/logs --name zkApp1 --network zookeeper-cluster ece465_zkapp:v1
SET NODE=1
SET NAME=zkApp%NODE%
REM docker run -d --name %NAME% --rm=true --hostname %NAME% --publish 2181:2181 --publish 9665:8080 --publish 8091:8081 --publish 9091:9090 --env-file zookeeper1.env -v zookeeper1-dataDir:/data -v zookeeper1-dataLogDir:/datalog -v zookeeper1-logs:/logs --network zookeeper-cluster ece465_zkapp:%VERSION%
docker run -d --name %NAME% --rm=true --hostname %NAME% --publish 218%NODE%:2181 --publish 906%NODE%:8080 --publish 807%NODE%:8081 --publish 909%NODE%:9090 --env-file zookeeper%NODE%.env -v zookeeper%NODE%-dataDir:/data -v zookeeper%NODE%-dataLogDir:/datalog -v zookeeper%NODE%-logs:/logs --network zookeeper-cluster %IMAGE%:%VERSION%

REM Create and run ZK node 2
REM docker run -d --rm=true --publish 2182:2181 --publish 9666:8080 --publish 8092:8081 --publish 9092:9090 --env-file zookeeper2.env -v zookeeper2-dataDir:/data -v zookeeper2-dataLogDir:/datalog -v zookeeper2-logs:/logs --name zkApp2 --network zookeeper-cluster ece465_zkapp:v1
SET NODE=2
SET NAME=zkApp%NODE%
SET VERSION=v1
REM docker run -d --name %NAME% --rm=true --hostname %NAME% --publish 2181:2181 --publish 9665:8080 --publish 8091:8081 --publish 9091:9090 --env-file zookeeper1.env -v zookeeper1-dataDir:/data -v zookeeper1-dataLogDir:/datalog -v zookeeper1-logs:/logs --network zookeeper-cluster ece465_zkapp:%VERSION%
docker run -d --name %NAME% --rm=true --hostname %NAME% --publish 218%NODE%:2181 --publish 906%NODE%:8080 --publish 807%NODE%:8081 --publish 909%NODE%:9090 --env-file zookeeper%NODE%.env -v zookeeper%NODE%-dataDir:/data -v zookeeper%NODE%-dataLogDir:/datalog -v zookeeper%NODE%-logs:/logs --network zookeeper-cluster %IMAGE%:%VERSION%

REM Create and run ZK node 3
REM docker run -d --rm=true --publish 2183:2181 --publish 9667:8080 --publish 8093:8081 --publish 9093:9090 --env-file zookeeper3.env -v zookeeper3-dataDir:/data -v zookeeper3-dataLogDir:/datalog -v zookeeper3-logs:/logs --name zkApp3 --network zookeeper-cluster ece465_zkapp:v1
SET NODE=3
SET NAME=zkApp%NODE%
SET VERSION=v1
REM docker run -d --name %NAME% --rm=true --hostname %NAME% --publish 2181:2181 --publish 9665:8080 --publish 8091:8081 --publish 9091:9090 --env-file zookeeper1.env -v zookeeper1-dataDir:/data -v zookeeper1-dataLogDir:/datalog -v zookeeper1-logs:/logs --network zookeeper-cluster ece465_zkapp:%VERSION%
docker run -d --name %NAME% --rm=true --hostname %NAME% --publish 218%NODE%:2181 --publish 906%NODE%:8080 --publish 807%NODE%:8081 --publish 909%NODE%:9090 --env-file zookeeper%NODE%.env -v zookeeper%NODE%-dataDir:/data -v zookeeper%NODE%-dataLogDir:/datalog -v zookeeper%NODE%-logs:/logs --network zookeeper-cluster %IMAGE%:%VERSION%

REM Sleep 5 sec and then check that all nodes are running.
REM sleep 5
REM curl http://localhost:9666/commands/leader
