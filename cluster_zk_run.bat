@ECHO OFF
REM
REM cluster_zk_run.bat
REM
REM Run clusters after you create the cluster with cluster_zk_create.bat

SET IMAGE=zookeeper
SET VERSION=3.8.3

REM Create and run ZK node 1
REM SET NODE=1
REM docker run -d --rm=true --publish 218%NODE%:2181 --publish 960%NODE%:8080 --env-file zookeeper%NODE%.env -v zookeeper%NODE%-dataDir:/data -v zookeeper%NODE%-dataLogDir:/datalog -v zookeeper%NODE%-logs:/logs --name zookeeper%NODE% --network zookeeper-cluster zookeeper:3.8.3
SET NODE=1
SET NAME=zkApp%NODE%
docker run -d --name %NAME% --rm=true --hostname %NAME% --publish 218%NODE%:2181 --publish 906%NODE%:8080 --publish 909%NODE%:9090 --env-file zookeeper%NODE%.env -v zookeeper%NODE%-dataDir:/data -v zookeeper%NODE%-dataLogDir:/datalog -v zookeeper%NODE%-logs:/logs --network zookeeper-cluster %IMAGE%:%VERSION%

REM Create and run ZK node 2
SET NODE=2
SET NAME=zkApp%NODE%
docker run -d --name %NAME% --rm=true --hostname %NAME% --publish 218%NODE%:2181 --publish 906%NODE%:8080 --publish 807%NODE%:8081 --env-file zookeeper%NODE%.env -v zookeeper%NODE%-dataDir:/data -v zookeeper%NODE%-dataLogDir:/datalog -v zookeeper%NODE%-logs:/logs --network zookeeper-cluster %IMAGE%:%VERSION%

REM Create and run ZK node 3
SET NODE=3
SET NAME=zkApp%NODE%
docker run -d --name %NAME% --rm=true --hostname %NAME% --publish 218%NODE%:2181 --publish 906%NODE%:8080 --publish 807%NODE%:8081 --env-file zookeeper%NODE%.env -v zookeeper%NODE%-dataDir:/data -v zookeeper%NODE%-dataLogDir:/datalog -v zookeeper%NODE%-logs:/logs --network zookeeper-cluster %IMAGE%:%VERSION%

REM Sleep 5 sec and then check that all nodes are running.
sleep 5
curl http://localhost:9061/commands/leader
