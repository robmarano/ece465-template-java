#!/usr/bin/env bash

cd ./test/client
clear && java -cp ../../target/classes/ ece465.Client localhost 1971 1972
cd ../..
