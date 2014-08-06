package com.gigaspaces.streaming.offset;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Holds offset of the last item in the stream.
 *
 * @author Oleksiy_Dyagilev
 */
public class StreamWriterHead {

    private AtomicLong offset;

    public StreamWriterHead(Long offset) {
        this.offset = new AtomicLong(offset);
    }

    public Long incrementAndGet() {
        return offset.incrementAndGet();
    }

    public Long addAndGet(long delta) {
        return offset.addAndGet(delta);
    }
}
