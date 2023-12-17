#!/usr/bin/env bash
#
# Run clusters after you create the cluster with cluster_zk_create.sh

# using docker image zookeeper:3.8.3
# separate servers for ZK and for APP clusters

# Create and run ZK node 1
#docker run -d --rm=true --publish 2181:2181 --publish 9665:8080 --publish 8091:8081 --env-file zookeeper1.env -v zookeeper1-dataDir:/data -v zookeeper1-dataLogDir:/datalog -v zookeeper1-logs:/logs --name zkApp1 --network zookeeper-cluster zookeeper:3.8.3

# Create and run ZK node 2
#docker run -d --rm=true --publish 2182:2181 --publish 9666:8080 --publish 8092:8081 --env-file zookeeper2.env -v zookeeper2-dataDir:/data -v zookeeper2-dataLogDir:/datalog -v zookeeper2-logs:/logs --name zkApp2 --network zookeeper-cluster zookeeper:3.8.3

# Create and run ZK node 3
#docker run -d --rm=true --publish 2183:2181 --publish 9667:8080 --publish 8093:8081 --env-file zookeeper3.env -v zookeeper3-dataDir:/data -v zookeeper3-dataLogDir:/datalog -v zookeeper3-logs:/logs --name zkApp3 --network zookeeper-cluster zookeeper:3.8.3

# using docker image ece465_zkapp:v1
# one server for each node for ZK and for APP clusters

# Create and run ZK node 1
docker run -d --rm=true --publish 2181:2181 --publish 9665:8080 --publish 8091:8081 --publish 9091:9090 --env-file zookeeper1.env -v zookeeper1-dataDir:/data -v zookeeper1-dataLogDir:/datalog -v zookeeper1-logs:/logs --name zkApp1 --network zookeeper-cluster ece465_zkapp:v1

# Create and run ZK node 2
docker run -d --rm=true --publish 2182:2181 --publish 9666:8080 --publish 8092:8081 --publish 9092:9090 --env-file zookeeper2.env -v zookeeper2-dataDir:/data -v zookeeper2-dataLogDir:/datalog -v zookeeper2-logs:/logs --name zkApp2 --network zookeeper-cluster ece465_zkapp:v1

# Create and run ZK node 3
docker run -d --rm=true --publish 2183:2181 --publish 9667:8080 --publish 8093:8081 --publish 9093:9090 --env-file zookeeper3.env -v zookeeper3-dataDir:/data -v zookeeper3-dataLogDir:/datalog -v zookeeper3-logs:/logs --name zkApp3 --network zookeeper-cluster ece465_zkapp:v1


# Sleep 5 sec and then check that all nodes are running.
sleep 5
curl http://localhost:9666/commands/leader
