# zkApp for ECE465

# Running App Nodes as Processes
First, if ZK cluster is already running, as well as the app nodes, bring them down first:
```bash
ps aux|egrep zkApp-1.0-SNAPSHOT.jar | egrep -v egrep | awk {'print $2'} | xargs kill && ./cluster_zk_recreate_volumes.sh  && ./cluster_zk_run.sh
```

Then, start your Zookeeper (ZK) cluster. I am starting them as containers on my local computer running Docker:
```bash
cluster_zk_run.sh
```

Next, I start up the App nodes as processes that each depend on the running ZK cluster.
```bash
app_build.sh && app_run_local.sh
```

Then you can tail the app log for each app node:
```bash
tail -F -f ~/dev/cooper/ece465/ece465-template-java/nodes/node1/node1.log
tail -F -f ~/dev/cooper/ece465/ece465-template-java/nodes/node2/node2.log
tail -F -f ~/dev/cooper/ece465/ece465-template-java/nodes/node3/node3.log
```

Next I open my browser and hit the following URLs to interact with the App:

## For "app node 1":
### App Interface
```bash
http://localhost:8081/
```
### Swagger Interface to issue API commands to my App service
```bash
http://localhost:8081/swagger-ui/index.html
http://localhost:9091/actuator/swagger-ui/index.html#/
```

## For "app node 2":
### App Interface
```bash
http://localhost:8082/
```
### Swagger Interface to issue API commands to my App service
```bash
http://localhost:8082/swagger-ui/index.html
http://localhost:9092/actuator/swagger-ui/index.html#/
```

## For "app node 3":
### App Interface
```bash
http://localhost:8083/
```
### Swagger Interface to issue API commands to my App service
```bash
http://localhost:8083/swagger-ui/index.html
http://localhost:9093/actuator/swagger-ui/index.html#/
```

# Interacting with the Service

## Set data via API
```bash
ZK_NODE=localhost
ZK_PORT=9061
ZK_LEADER=$(curl --silent -X 'GET' http://${ZK_NODE}:${ZK_PORT}/commands/leader -H 'accept: */*' | jq .leader_ip | sed 's/^"\(.*\)"$/\1/')

#
# Simple Naming Service to get APP_LEADER_HOST and its APP_LEADER_PORT
#
APP_NODE=localhost
APP_PORT=8081
APP_LEADER_COUPLET=$(curl --silent -X 'GET' http://${APP_NODE}:${APP_PORT}/clusterInfo -H 'accept: */*' | jq .master | sed 's/^"\(.*\)"$/\1/')
APP_LEADER_HOST=$(echo ${APP_LEADER_COUPLET} | awk -F : '{print $1}')
APP_LEADER_PORT=$(echo ${APP_LEADER_COUPLET} | awk -F : '{print $2}')

#
# Using APP API, add new data, i.e., person name = luca; person id = 2121
#
PUID=2121
PNAME=luca
curl -X 'PUT' \
  'http://localhost:8081/person/${PUID}/${PNAME}' \
  -H 'accept: */*'
  ```

# Resources
* [Apache Zookeeper Explained: Tutorial, Use Cases and Zookeeper Java API Examples](http://java.globinch.com/enterprise-services/zookeeper/apache-zookeeper-explained-tutorial-cases-zookeeper-java-api-examples/)
* [Spring Boot - Uploading Files](https://spring.io/guides/gs/uploading-files/)

# Review of CAP Theorem
CAP Theorem (Brewer's theorem/ By Eric Brewer) states that, it is nearly impossible to achieve all three of Consistency, Availability and Partition tolerance. Any distributed system can choose only two of the following:
1. Consistency : All nodes is a distributed environment see the same data at the same time.
2. Availability: Guarantee that every request receives a response.
3. Partition Tolerance: Even if the connections between nodes are down, the other nodes works as guaranteed. (Tolerance to network partitions/broken communication)
   Zookeeper relaxes availability, but guarantees consistency and partition tolerance. Zookeeper implements an API that manipulates simple waitfree data objects organized hierarchically as in file systems.Pipelined architecture of ZooKeeper enables the execution of operations from a single client in FIFO order.









