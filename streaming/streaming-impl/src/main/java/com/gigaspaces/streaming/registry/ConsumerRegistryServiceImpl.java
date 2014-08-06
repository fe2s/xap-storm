package com.gigaspaces.streaming.registry;

import com.gigaspaces.client.ReadModifiers;
import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.query.IdQuery;
import org.apache.log4j.Logger;
import org.openspaces.core.EntryAlreadyInSpaceException;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.cluster.ClusterInfo;
import org.openspaces.core.cluster.ClusterInfoContext;
import org.openspaces.remoting.RemotingService;
import org.openspaces.remoting.Routing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registers stream consumers.
 *
 * @author Oleksiy_Dyagilev
 */
@RemotingService
public class ConsumerRegistryServiceImpl implements ConsumerRegistryService {

    private static Logger log = Logger.getLogger(ConsumerRegistryServiceImpl.class);

    @Autowired
    private GigaSpace space;

    @ClusterInfoContext
    private ClusterInfo clusterInfo;

    @Override
    @Transactional
    public int registerConsumer(@Routing String streamId) {

        ConsumerRegistry registry = findRegistryWithLock(streamId);

        if (registry == null) {
            registry = initRegistry(streamId);
        }

        int routingKey = registry.registerConsumer();

        space.write(registry);

        return routingKey;
    }

    private ConsumerRegistry findRegistryWithLock(String streamId) {
        IdQuery<ConsumerRegistry> query = new IdQuery<>(ConsumerRegistry.class, streamId);
        return space.readById(query, 1000, ReadModifiers.EXCLUSIVE_READ_LOCK);
    }

    private ConsumerRegistry initRegistry(String streamId) {
        log.info("Initializing consumer registry");

        int numberOfPartitions = clusterInfo == null ? 1 : clusterInfo.getNumberOfInstances();

        ConsumerRegistry registry = new ConsumerRegistry(streamId);
        registry.setNumberOfPartitions(numberOfPartitions);

        // in case there are concurrent requests, we want to init it only once
        try {
            space.write(registry, WriteModifiers.WRITE_ONLY);
        } catch (EntryAlreadyInSpaceException e) {
            registry = findRegistryWithLock(streamId);
        }

        return registry;
    }

}
