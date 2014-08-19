#!/bin/bash

storm jar google-analytics/storm-topology/target/storm-topology-1.0-SNAPSHOT.jar com.gigaspaces.storm.googleanalytics.topology.GoogleAnalyticsTopology google 127.0.0.1
