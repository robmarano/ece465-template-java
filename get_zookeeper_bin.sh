#!/usr/bin/env bash
#
# Fetch Zookeeper binaries to use

ZK=apache-zookeeper-3.8.3-bin
wget https://dlcdn.apache.org/zookeeper/zookeeper-3.8.3/${ZK}.tar.gz
/bin/rm -rf ./${ZK}
tar -xvf ${ZK}.tar.gz
/bin/rm -f ./${ZK}.tar.gz
mv ${ZK} zk