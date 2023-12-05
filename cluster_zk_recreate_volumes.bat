REM @ECHO OFF
REM
REM cluster_zk_recreate_volumes.bat
REM

docker stop zkApp1 zkApp2 zkApp3

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
