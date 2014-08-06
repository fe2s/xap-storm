package com.gigaspaces.streaming.registry;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the last allocated routing key for stream.
 *
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class ConsumerRegistry {

    // singleton in the space
    private Long id = 1L;
    private Integer numberOfPartitions;
    private Map<String, Integer> lastAllocatedRoutingKeyByStreamId = new HashMap<>();

    public ConsumerRegistry() {
    }

    /**
     * Allocates routing key for stream consumers.
     *
     * @param streamId stream id
     * @return allocated routing key
     */
    public int registerConsumer(String streamId) {
        Integer lastAllocatedRouting = lastAllocatedRoutingKeyByStreamId.get(streamId);
        if (lastAllocatedRouting == null) {
            lastAllocatedRouting = 0;
        } else {
            lastAllocatedRouting = (lastAllocatedRouting + 1) % numberOfPartitions;
        }
        lastAllocatedRoutingKeyByStreamId.put(streamId, lastAllocatedRouting);

        return lastAllocatedRouting;
    }

    @SpaceId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumberOfPartitions() {
        return numberOfPartitions;
    }

    public void setNumberOfPartitions(Integer numberOfPartitions) {
        this.numberOfPartitions = numberOfPartitions;
    }

    public Map<String, Integer> getLastAllocatedRoutingKeyByStreamId() {
        return lastAllocatedRoutingKeyByStreamId;
    }

    public void setLastAllocatedRoutingKeyByStreamId(Map<String, Integer> lastAllocatedRoutingKeyByStreamId) {
        this.lastAllocatedRoutingKeyByStreamId = lastAllocatedRoutingKeyByStreamId;
    }
}
