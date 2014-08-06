package com.gigaspaces.streaming.registry;

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
    int registerConsumer(String streamId);

}
