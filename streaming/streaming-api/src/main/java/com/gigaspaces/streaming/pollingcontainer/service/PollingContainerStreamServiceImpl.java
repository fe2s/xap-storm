package com.gigaspaces.streaming.pollingcontainer.service;

import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.streaming.pollingcontainer.Batch;
import com.gigaspaces.streaming.pollingcontainer.BatchItem;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mykola_Zalyayev
 */
public class PollingContainerStreamServiceImpl implements PollingContainerStreamService {

    private GigaSpace space;

    private StreamRemoteService remoteService;

    private Long lastTxId;

    public PollingContainerStreamServiceImpl(GigaSpace space, StreamRemoteService remoteService) {
        this.space = space;
        this.remoteService = remoteService;
    }

    @Override
    public void write(Serializable o) {
        BatchItem item = new BatchItem();
        item.setValue(o);

        space.write(item);
    }

    @Override
    public void writeBatch(List<Serializable> batch) {
        List<BatchItem> batchItems = new ArrayList<>();
        for (Serializable o : batch) {
            BatchItem item = new BatchItem();
            item.setValue(o);
            batchItems.add(item);
        }

        space.writeMultiple(batchItems.toArray());
    }

    @Override
    public Batch read(Long txId, int partitionNumber) {
        return remoteService.read(partitionNumber,txId);
    }

    @Override
    public void acknowledge(Long txId, int partitionNumber) {
        Batch batch = new Batch();
        batch.setTxId(txId);
        batch.setPartitionNumber(partitionNumber);

        space.clear(batch);
    }


}
