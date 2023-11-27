@ECHO OFF
REM
REM cluster_zk_create.bat
REM Create Zookeeper 3-node cluster on Docker.

SET VERSION=3.8.3

REM Download Docker image for Zookeeper
docker pull zookeeper:%VERSION%

REM delete any previous volumes
docker volume rm zookeeper1-dataDir
docker volume rm zookeeper1-dataLogDir
docker volume rm zookeeper1-logs
docker volume rm zookeeper2-dataDir
docker volume rm zookeeper2-dataLogDir
docker volume rm zookeeper2-logs
docker volume rm zookeeper3-dataDir
docker volume rm zookeeper3-dataLogDir
docker volume rm zookeeper3-logs

REM create new volumes for Zookeeper
docker volume create zookeeper1-dataDir
docker volume create zookeeper1-dataLogDir
docker volume create zookeeper1-logs

docker volume create zookeeper2-dataDir
docker volume create zookeeper2-dataLogDir
docker volume create zookeeper2-logs

docker volume create zookeeper3-dataDir
docker volume create zookeeper3-dataLogDir
docker volume create zookeeper3-logs

REM check new volumes exist
docker volume ls

REM create Docker network for zookeeper
REM docker network create --driver bridge zookeeper-cluster
