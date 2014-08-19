package com.gigaspaces.streaming.offset;

import com.j_spaces.core.client.SQLQuery;
import org.apache.log4j.Logger;
import org.openspaces.core.GigaSpace;

import java.util.ArrayList;
import java.util.List;


/**
 * Stream on local space partition.
 *
 * @author Oleksiy_Dyagilev
 */
public class PartitionedStream<T> {

    private static Logger log = Logger.getLogger(PartitionedStream.class);

    private String streamId;
    private StreamWriterHead writerHead;
    private GigaSpace localSpace;
    private int routing;

    /**
     * Creates new stream reference
     *
     * @param streamId stream id
     * @param writerHead stream producer head
     * @param localSpace local space
     * @param routing routing key that corresponds to this partition
     */
    PartitionedStream(String streamId, StreamWriterHead writerHead, GigaSpace localSpace, int routing) {
        this.streamId = streamId;
        this.writerHead = writerHead;
        this.localSpace = localSpace;
        this.routing = routing;
    }

    /**
     * Write single element to stream
     *
     * @param obj element to write
     */
    public void write(T obj) {
        Long offset = writerHead.incrementAndGet();
        Item<T> item = convertToItem(obj, offset);
        localSpace.write(item);
    }

    /**
     * Write batch of elements to stream
     *
     * @param objects elements to write
     */
    public void writeBatch(List<T> objects) {
        int batchSize = objects.size();
        Long endOffset = writerHead.addAndGet(batchSize);
        List<Item> items = new ArrayList<Item>(batchSize);

        long offset = endOffset - batchSize + 1;
        for (T object : objects) {
            Item<T> item = convertToItem(object, offset);
            items.add(item);
            offset = offset + 1;
        }

        localSpace.writeMultiple(items.toArray());
    }

    /**
     * Read batch of elements
     *
     * @param batchId batch id
     * @param count max numbers to read
     * @return list of elements
     */
    public List<T> readBatch(long batchId, int count) {
        BatchMetadata batchMetadata = localSpace.readById(BatchMetadata.class, batchId);

        if (batchMetadata == null) {
            // new batchId
            StreamReaderHead readerHead = findReaderHead();
            long lastReadOffset = readerHead.getLastReadOffset();
            List<T> items = this.read(lastReadOffset, count);

            batchMetadata = new BatchMetadata(routing, streamId, batchId, lastReadOffset, items.size());
            readerHead.setLastReadOffset(lastReadOffset + items.size());

            localSpace.writeMultiple(new Object[]{batchMetadata, readerHead});
            return items;

        } else {
            // replaying
            return this.read(batchMetadata.getOffset(), batchMetadata.getCount());
        }
    }

    /**
     * Acknowledge that batch successfully consumed by reader.
     * Elements removed from the stream
     *
     * @param batchId batch id
     */
    public void ack(long batchId) {
        BatchMetadata batchMetadata = localSpace.takeById(BatchMetadata.class, batchId);
        SQLQuery<Item> query = new SQLQuery<Item>(Item.class, "streamId = ? and (offset >= ? and offset < ?)");
        long startOffset = batchMetadata.getOffset();
        long endOffset = startOffset + batchMetadata.getCount();
        query.setParameters(streamId, startOffset, endOffset);
        localSpace.takeMultiple(query);
    }

    /**
     * @return number of elements in stream
     */
    public int size() {
        Item<T> template = new Item<T>();
        template.setStreamId(streamId);
        return localSpace.count(template);
    }

    /**
     * @return number of batches pending acknowledge
     */
    public int ackPendingBatchCount() {
        BatchMetadata batchMetadata = new BatchMetadata();
        batchMetadata.setStreamId(streamId);
        return localSpace.count(batchMetadata);
    }

    private List<T> read(long offset, long count) {
        SQLQuery<Item> query = new SQLQuery<Item>(Item.class, "streamId = ? and (offset >= ? and offset < ?)");
        query.setParameters(streamId, offset, offset + count);

        Item[] items = localSpace.readMultiple(query);
        List<T> objects = new ArrayList<T>(items.length);
        for (Item item : items) {
            objects.add((T) item.getValue());
        }

        return objects;
    }

    private Item<T> convertToItem(T obj, long offset) {
        Item<T> item = new Item<T>();
        item.setStreamId(streamId);
        item.setValue(obj);
        item.setOffset(offset);
        item.setRouting(routing);
        return item;
    }

    private StreamReaderHead findReaderHead() {
        StreamReaderHead readerHead = localSpace.readById(StreamReaderHead.class, streamId);
        if (readerHead == null) {
            readerHead = new StreamReaderHead();
            readerHead.setRouting(routing);
            readerHead.setStreamId(streamId);
            readerHead.setLastReadOffset(0L);
        }
        return readerHead;
    }


}
