#!/usr/bin/env bash

ps aux|egrep zkApp-1.0-SNAPSHOT.jar | egrep -v egrep | awk {'print $2'} | xargs kill