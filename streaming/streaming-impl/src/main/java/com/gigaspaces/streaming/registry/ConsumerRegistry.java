package com.gigaspaces.streaming.registry;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class ConsumerRegistry {

    // singleton in the space
    private Long id = 1L;
    private Integer numberOfPartitions;
    private Map<String, Integer> lastAllocatedRoutingKeyByStreamName = new HashMap<>();

    public ConsumerRegistry() {
    }

    public int registerConsumer(String streamName) {
        Integer lastAllocatedRouting = lastAllocatedRoutingKeyByStreamName.get(streamName);
        if (lastAllocatedRouting == null) {
            lastAllocatedRouting = 0;
        } else {
            lastAllocatedRouting = (lastAllocatedRouting + 1) % numberOfPartitions;
        }
        lastAllocatedRoutingKeyByStreamName.put(streamName, lastAllocatedRouting);

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

    public Map<String, Integer> getLastAllocatedRoutingKeyByStreamName() {
        return lastAllocatedRoutingKeyByStreamName;
    }

    public void setLastAllocatedRoutingKeyByStreamName(Map<String, Integer> lastAllocatedRoutingKeyByStreamName) {
        this.lastAllocatedRoutingKeyByStreamName = lastAllocatedRoutingKeyByStreamName;
    }
}
