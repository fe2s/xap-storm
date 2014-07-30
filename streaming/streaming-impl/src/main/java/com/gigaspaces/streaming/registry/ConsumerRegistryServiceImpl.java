package com.gigaspaces.streaming.registry;

import com.gigaspaces.client.ReadModifiers;
import com.gigaspaces.query.IdQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.cluster.ClusterInfo;
import org.openspaces.core.cluster.ClusterInfoContext;
import org.openspaces.remoting.RemotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oleksiy_Dyagilev
 */
@RemotingService
public class ConsumerRegistryServiceImpl implements ConsumerRegistryService {

    @Autowired
    private GigaSpace space;

    @Override
    @Transactional
    public int registerConsumer(String streamId) {
        GigaSpace clustered = space.getClustered();

        IdQuery<ConsumerRegistry> query = new IdQuery<>(ConsumerRegistry.class, 1L);

        ConsumerRegistry registry = clustered.readById(query, 1000, ReadModifiers.EXCLUSIVE_READ_LOCK);
        int routingKey = registry.registerConsumer(streamId);

        clustered.write(registry);

        return routingKey;
    }

}
