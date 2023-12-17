#!/usr/bin/env bash

ps aux|egrep zkApp-1.0-SNAPSHOT.jar | egrep -v egrep | awk {'print $2'} | xargs kill
./cluster_zk_recreate_volumes.sh && \
./cluster_zk_run.sh && \
./app_build.sh && ./app_run_local.sh
