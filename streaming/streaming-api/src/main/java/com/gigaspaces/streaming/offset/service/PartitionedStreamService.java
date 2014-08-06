package com.gigaspaces.streaming.offset.service;

import org.openspaces.remoting.Routing;

import java.util.List;

/**
 * Stream service on top of PartitionedStream.
 * API is adapted for Trident spout.
 *
 * @author Oleksiy_Dyagilev
 */
public interface PartitionedStreamService<T> {

    /**
     * write single element to stream
     *
     * @param partitionRouting routing key
     * @param streamId stream id
     * @param obj element to write
     */
    void write(@Routing int partitionRouting, String streamId, T obj);

    /**
     * write batch of elements to stream
     *
     * @param partitionRouting routing key
     * @param streamId stream id
     * @param objects elements to write
     */
    void writeBatch(@Routing int partitionRouting, String streamId, List<T> objects);

    /**
     * Reads batch of elements from stream. Batch can be reread later.
     * Elements are not removed from stream until ack() for given batchId called.
     *
     * @param partitionRouting routing key
     * @param streamId stream id
     * @param batchId batch id
     * @param count max number of elements to reads
     * @return list of items
     */
    List<T> readBatch(@Routing int partitionRouting, String streamId, long batchId, int count);

    /**
     * Acknowledge that batch has been successfully consumed by reader.
     * Elements removed from the stream.
     *
     * @param partitionRouting routing key
     * @param streamId stream id
     * @param batchId batch id
     */
    void ack(@Routing int partitionRouting, String streamId, long batchId);

}
