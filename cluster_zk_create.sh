#!/usr/bin/env bash
#
# Create Zookeeper 3-node cluster on Docker.

VERSION=3.8.3

# Download Docker image for Zookeeper
docker pull zookeeper:${VERSION}

# delete any previous volumes
docker volume rm zookeeper1-dataDir
docker volume rm zookeeper1-dataLogDir
docker volume rm zookeeper1-logs
docker volume rm zookeeper2-dataDir
docker volume rm zookeeper2-dataLogDir
docker volume rm zookeeper2-logs
docker volume rm zookeeper3-dataDir
docker volume rm zookeeper3-dataLogDir
docker volume rm zookeeper3-logs

# create new volumes for Zookeeper
docker volume create zookeeper1-dataDir
docker volume create zookeeper1-dataLogDir
docker volume create zookeeper1-logs

docker volume create zookeeper2-dataDir
docker volume create zookeeper2-dataLogDir
docker volume create zookeeper2-logs

docker volume create zookeeper3-dataDir
docker volume create zookeeper3-dataLogDir
docker volume create zookeeper3-logs

# check new volumes exist
docker volume ls | grep zookeeper

# create Docker network for zookeeper
#docker network create --driver bridge zookeeper-cluster

