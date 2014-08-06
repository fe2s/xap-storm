package com.gigaspaces.streaming.registry;

import org.openspaces.remoting.Routing;

/**
 * Registers stream consumers.
 *
 * @author Oleksiy_Dyagilev
 */
public interface ConsumerRegistryService {

    /**
     * register stream consumer
     *
     * @param streamId stream id
     * @return routing key allocated for consumer
     */
    int registerConsumer(@Routing String streamId);

}
