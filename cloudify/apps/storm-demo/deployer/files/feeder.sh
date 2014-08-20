#!/bin/sh

LOOK=$1

java -classpath files/feeder-1.0-SNAPSHOT.jar com.gigaspaces.storm.googleanalytics.feeder.Main $LOOK &>> ~/feeder.log &

