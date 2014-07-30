package com.gigaspaces.streaming.offset;

import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.cluster.ClusterInfo;
import org.openspaces.core.cluster.ClusterInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static org.openspaces.extensions.QueryExtension.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Oleksiy_Dyagilev
 */
@Component
public class StreamManager {

    @Autowired
    private GigaSpace space;
    @ClusterInfoContext
    private ClusterInfo clusterInfo;
    private int clusterInstanceId;

    private Map<String, PartitionedStream> streamsById = new ConcurrentHashMap<>();

    @PostConstruct
    public void initClusterInstanceId(){
        // clusterInfo is null when running Integrated PU
        clusterInstanceId = clusterInfo == null ? 1 : clusterInfo.getInstanceId();
    }

    @SuppressWarnings("unchecked")
    public <T> PartitionedStream<T> getStream(String streamId) {
        PartitionedStream stream = streamsById.get(streamId);
        if (stream == null) {
            stream = initStream(streamId);
        }
        return stream;
    }

    // calling this method simulates partition recovering
    public void reloadStreams(){
        streamsById.clear();
    }

    private synchronized PartitionedStream initStream(String streamId) {
        PartitionedStream stream = streamsById.get(streamId);
        if (stream != null) {
            return stream;
        }

        StreamWriterHead writerHead = findStreamWriterHead(streamId);

        int routing = clusterInstanceId - 1;
        stream = new PartitionedStream<>(streamId, writerHead, space, routing);

        streamsById.put(streamId, stream);

        return stream;
    }

    private StreamWriterHead findStreamWriterHead(String streamId) {
        SQLQuery<Item> query = new SQLQuery<>(Item.class, "streamId=?");
        query.setParameters(streamId);
        Long maxOffset = max(space, query, "offset");
        if (maxOffset == null) {
            maxOffset = -1L;
        }

        return new StreamWriterHead(maxOffset);
    }

    public void setClusterInfo(ClusterInfo clusterInfo) {
        this.clusterInfo = clusterInfo;
    }
}
