package com.gigaspaces.streaming.offset.service;

import com.gigaspaces.streaming.offset.StreamManager;
import org.openspaces.core.GigaSpace;
import org.openspaces.remoting.RemotingService;
import org.openspaces.remoting.Routing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Oleksiy_Dyagilev
 */
@RemotingService
public class PartitionedStreamServiceImpl<T> implements PartitionedStreamService<T> {

    @Autowired
    private StreamManager streamManager;

    public void write(@Routing int partitionRouting, String streamId, T obj) {
        streamManager.getStream(streamId).write(obj);
    }

    @Override
    public void writeBatch(@Routing int partitionRouting, String streamId, List<T> objects) {
        streamManager.<T>getStream(streamId).writeBatch(objects);
    }

    @Transactional
    public List<T> readBatch(@Routing int partitionRouting, String streamId, long batchId, int count) {
        return streamManager.<T>getStream(streamId).readBatch(batchId, count);
    }

    @Transactional
    public void ack(@Routing int partitionRouting, String streamId, long batchId) {
        streamManager.<T>getStream(streamId).ack(batchId);
    }

}
