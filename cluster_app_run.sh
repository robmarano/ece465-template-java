#!/usr/bin/env bash
#
# Run clusters after you create the cluster with cluster_zk_create.sh

# Create and run ZK node 1
docker run -d --rm=true --publish 2181:2181 --publish 9665:8080 --publish 8091:8081 --env-file zookeeper1.env -v zookeeper1-dataDir:/data -v zookeeper1-dataLogDir:/datalog -v zookeeper1-logs:/logs --name zookeeper1 --network zookeeper-cluster ece465_zkapp:v1

# Create and run ZK node 2
docker run -d --rm=true --publish 2182:2181 --publish 9666:8080 --publish 8092:8081 --env-file zookeeper2.env --mount source=zookeeper2-dataDir,target=/data --mount source=zookeeper2-dataLogDir,target=/datalog --mount source=zookeeper2-logs,target=/logs --name zookeeper2 --network zookeeper-cluster ece465_zkapp:v1

# Create and run ZK node 3
docker run -d --rm=true --publish 2183:2181 --publish 9667:8080 --publish 8093:8081 --env-file zookeeper3.env --mount source=zookeeper3-dataDir,target=/data --mount source=zookeeper3-dataLogDir,target=/datalog --mount source=zookeeper3-logs,target=/logs --name zookeeper3 --network zookeeper-cluster ece465_zkapp:v1

# Sleep 5 sec and then check that all nodes are running.
sleep 5
curl http://localhost:9665/commands/leader
