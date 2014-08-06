package com.gigaspaces.streaming.offset;

import com.gigaspaces.annotation.pojo.*;

/**
 * Wrapper around stream element payload.
 * Items are kept ordered by offset property.
 *
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class Item<T> {

    private String streamId;
    private Long offset;
    private T value;
    private Integer routing;

    @SpaceIndex
    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    @SpaceId
    @SpaceProperty(index= SpaceProperty.IndexType.EXTENDED)
    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @SpaceRouting
    public Integer getRouting() {
        return routing;
    }

    public void setRouting(Integer routing) {
        this.routing = routing;
    }
}
