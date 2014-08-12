#!/bin/sh
sleep 300

java -classpath overwrite/feeder-1.0-SNAPSHOT.jar com.gigaspaces.storm.googleanalytics.feeder.Main 127.0.0.1:4242 &
