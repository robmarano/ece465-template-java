#!/usr/bin/env bash

pwd
mvn clean package && cd ./test/server && clear && java -cp ../../target/classes ece465.Server 1971 1972
cd ../../
