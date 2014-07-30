package com.gigaspaces.streaming.offset;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class BatchMetadata {

    private Integer routing;
    private String streamId;
    private Long batchId;
    private Long offset;
    private Integer count;

    public BatchMetadata() {
    }

    public BatchMetadata(Integer routing, String streamId, Long batchId, Long offset, Integer count) {
        this.routing = routing;
        this.streamId = streamId;
        this.batchId = batchId;
        this.offset = offset;
        this.count = count;
    }

    @SpaceRouting
    public Integer getRouting() {
        return routing;
    }

    public void setRouting(Integer routing) {
        this.routing = routing;
    }

    @SpaceId
    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }
}
