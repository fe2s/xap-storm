package com.gigaspaces.streaming.registry;

import org.apache.log4j.Logger;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.cluster.ClusterInfo;
import org.openspaces.core.cluster.ClusterInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Oleksiy_Dyagilev
 */
@Component
public class ConsumerRegistryInitializer {

    private static Logger log = Logger.getLogger(ConsumerRegistryInitializer.class);

    @Autowired
    private GigaSpace space;

    @ClusterInfoContext
    private ClusterInfo clusterInfo;

    @PostConstruct
    public void initRegistry() {
        // clusterInfo is null when running Integrated PU
        int instanceId = clusterInfo == null ? 1 : clusterInfo.getInstanceId();
        int numberOfPartitions = clusterInfo == null ? 1 : clusterInfo.getNumberOfInstances();

        // we want to init registry only once
        if (instanceId == 1) {
            log.info("Initializing consumer registry");
            ConsumerRegistry registry = new ConsumerRegistry();
            registry.setNumberOfPartitions(numberOfPartitions);

            space.getClustered().write(registry);
        }
    }
}
