#!/usr/bin/env bash
#
# app_data_add.sh
#

#
# Functions
#
function call_person_api() {
  PUID=$1
  PNAME=$2
  API_CALL="/person/${PUID}/${PNAME}"
  API_CALL_URL="http://${APP_LEADER_HOST}:${APP_LEADER_PORT}${API_CALL}"

  # Make the request
  response=$(curl --silent -X 'PUT' ${API_CALL_URL}  -H 'accept: */*')

  # Check the response status code
  if [[ $response -ne 200 ]]; then
    echo "Error: API call failed with status code $response"
    exit 1
  fi

  # Print the response body
  echo "Response body: $response"
}

function get_persons() {
  API_CALL="/persons"
  API_CALL_URL="http://${APP_LEADER_HOST}:${APP_LEADER_PORT}${API_CALL}"

  # Make the request
  response=$(curl --silent -X 'GET' ${API_CALL_URL}  -H 'accept: */*')

  # Check the response status code
  if [[ $response -ne 200 ]]; then
    echo "Error: API call failed with status code $response"
    exit 1
  fi

  # Print the response body
  echo "$response"
}

#
# Simple Naming Service to get APP_LEADER_HOST and its APP_LEADER_PORT
#
#APP_NODE=localhost
APP_NODE=172.17.144.1
APP_PORT=8081
APP_LEADER_COUPLET=$(curl --silent -X 'GET' http://${APP_NODE}:${APP_PORT}/clusterInfo -H 'accept: */*' | jq .master | sed 's/^"\(.*\)"$/\1/')
APP_LEADER_HOST=$(echo ${APP_LEADER_COUPLET} | awk -F : '{print $1}')
APP_LEADER_PORT=$(echo ${APP_LEADER_COUPLET} | awk -F : '{print $2}')

echo $APP_LEADER_HOST
echo $APP_LEADER_PORT

#
# Using APP API, add new data, i.e., person name = luca; person id = 2121
#
#PUID=2121
#PNAME=luca
#API_CALL="/person/${PUID}/${PNAME}"
#API_CALL_URL="http://${APP_LEADER_HOST}:${APP_LEADER_PORT}${API_CALL}"
#curl -X 'PUT' ${API_CALL_URL}  -H 'accept: */*'

#for i in {1..10}; do
#  echo $i
#done

#persons=(alice bob john joe mary stuart cain morgan betty alfred)

# create persons
#i=0
#for p in ""${persons[@]}""; do
#  i=$((i+1))
#  echo $p $i
#  call_person_api $i $p
#done
#
##  get persons
#get_persons | jq .