#!/usr/bin/env bash
#
# cluster_zk_run.sh
#
# Run clusters after you create the cluster with cluster_zk_create.sh

IMAGE=zookeeper
VERSION=3.8.3

# Create and run ZK node 1
NODE=1
NAME=zkApp${NODE}
docker run -d --name ${NAME} --rm=true --hostname ${NAME} --publish 218${NODE}:2181 --publish 906${NODE}:8080 --env-file zookeeper${NODE}.env -v zookeeper${NODE}-dataDir:/data -v zookeeper${NODE}-dataLogDir:/datalog -v zookeeper${NODE}-logs:/logs --network zookeeper-cluster ${IMAGE}:${VERSION}

# Create and run ZK node 2
NODE=2
NAME=zkApp${NODE}
docker run -d --name ${NAME} --rm=true --hostname ${NAME} --publish 218${NODE}:2181 --publish 906${NODE}:8080 --env-file zookeeper${NODE}.env -v zookeeper${NODE}-dataDir:/data -v zookeeper${NODE}-dataLogDir:/datalog -v zookeeper${NODE}-logs:/logs --network zookeeper-cluster ${IMAGE}:${VERSION}

# Create and run ZK node 3
NODE=3
NAME=zkApp${NODE}
docker run -d --name ${NAME} --rm=true --hostname ${NAME} --publish 218${NODE}:2181 --publish 906${NODE}:8080 --env-file zookeeper${NODE}.env -v zookeeper${NODE}-dataDir:/data -v zookeeper${NODE}-dataLogDir:/datalog -v zookeeper${NODE}-logs:/logs --network zookeeper-cluster ${IMAGE}:${VERSION}

# Sleep 5 sec and then check that all nodes are running.
sleep 5
curl http://localhost:9061/commands/leader
