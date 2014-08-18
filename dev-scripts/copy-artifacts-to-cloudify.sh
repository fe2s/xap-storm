#!/bin/bash

echo "Copying space-1.0-SNAPSHOT.jar"
cp -f google-analytics/space/target/space-1.0-SNAPSHOT.jar cloudify/apps/storm-demo/deployer/files/

echo "Copying feeder-1.0-SNAPSHOT.jar"
cp -f google-analytics/feeder/target/feeder-1.0-SNAPSHOT.jar cloudify/apps/storm-demo/deployer/files/

echo "Copying web.war"
cp -f google-analytics/rest-service/target/web.war cloudify/apps/storm-demo/deployer/files/

echo "Copying storm-topology-1.0-SNAPSHOT.jar"
cp -f google-analytics/storm-topology/target/storm-topology-1.0-SNAPSHOT.jar cloudify/apps/storm-demo/storm-nimbus/commands


