package com.gigaspaces.streaming.offset.service;

import org.openspaces.remoting.Routing;

import java.util.List;

/**
 * @author Oleksiy_Dyagilev
 */

public interface PartitionedStreamService<T> {

    void write(@Routing int partitionRouting, String streamId, T obj);

    void writeBatch(@Routing int partitionRouting, String streamId, List<T> objects);

    List<T> readBatch(@Routing int partitionRouting, String streamId, long batchId, int count);

    void ack(@Routing int partitionRouting, String streamId, long batchId);

}
