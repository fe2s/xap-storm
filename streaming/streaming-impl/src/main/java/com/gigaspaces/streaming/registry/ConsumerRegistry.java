package com.gigaspaces.streaming.registry;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the last allocated routing key for stream.
 *
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class ConsumerRegistry {

    private String streamId;
    private Integer numberOfPartitions;
    private Integer lastAllocatedRoutingKey = -1;

    public ConsumerRegistry() {
    }

    public ConsumerRegistry(String streamId) {
        this.streamId = streamId;
    }

    /**
     * Allocates routing key for stream consumers.
     *
     * @return allocated routing key
     */
    public int registerConsumer() {
        lastAllocatedRoutingKey = (lastAllocatedRoutingKey + 1) % numberOfPartitions;
        return lastAllocatedRoutingKey;
    }

    @SpaceId
    @SpaceRouting
    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public Integer getNumberOfPartitions() {
        return numberOfPartitions;
    }

    public void setNumberOfPartitions(Integer numberOfPartitions) {
        this.numberOfPartitions = numberOfPartitions;
    }

    public Integer getLastAllocatedRoutingKey() {
        return lastAllocatedRoutingKey;
    }

    public void setLastAllocatedRoutingKey(Integer lastAllocatedRoutingKey) {
        this.lastAllocatedRoutingKey = lastAllocatedRoutingKey;
    }
}
