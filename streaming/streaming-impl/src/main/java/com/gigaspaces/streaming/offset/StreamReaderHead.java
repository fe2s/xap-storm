package com.gigaspaces.streaming.offset;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class StreamReaderHead {

    private String streamId;
    private Long lastReadOffset;
    private Integer routing;

    @SpaceId
    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public Long getLastReadOffset() {
        return lastReadOffset;
    }

    public void setLastReadOffset(Long lastReadOffset) {
        this.lastReadOffset = lastReadOffset;
    }

    @SpaceRouting
    public Integer getRouting() {
        return routing;
    }

    public void setRouting(Integer routing) {
        this.routing = routing;
    }

    @Override
    public String toString() {
        return "StreamReaderHead{" +
                "streamId='" + streamId + '\'' +
                ", lastReadOffset=" + lastReadOffset +
                ", routing=" + routing +
                '}';
    }
}
